package com.g1453012.btill;

import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.g1453012.btill.Bluetooth.ConnectedThread;
import com.g1453012.btill.Shared.BTMessage;
import com.g1453012.btill.Shared.BTMessageBuilder;
import com.g1453012.btill.Shared.Bill;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.Status;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.protocols.payments.PaymentProtocol;
import org.bitcoinj.protocols.payments.PaymentProtocolException;
import org.bitcoinj.protocols.payments.PaymentSession;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.uri.BitcoinURIParseException;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BTillController {

    private static final String TAG = "BTillController";
    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    // TODO Update get Menu to pull menu from Till.
    /*public Menu getMenu() {

        sendMenuRequest();
        Log.d(TAG, "Sends Menu Request");
        return receiveMenu();
    }*/

    public Future<Menu> getMenuFuture() {
        return pool.submit(new Callable<Menu>() {
            @Override
            public Menu call() throws Exception {
                while (!sendMenuRequest()){};
                Log.d(TAG, "Sends Menu Request in Future");
                return receiveMenu();
            }
        });
    }


    public boolean sendMenuRequest() {
        Future<Boolean> writeFuture = writeBT(new BTMessageBuilder("REQUEST_MENU").build());
        try {
            return writeFuture.get();
        }
        catch (InterruptedException e) {
            Log.e(TAG, "Sending the Menu Request was interrupted");
        } catch (ExecutionException e) {
            Log.e(TAG, "Sending the Menu Request had an Execution Exception");
        }
        return false;
    }

    public Menu receiveMenu() {
        //BTMessage menuMessage = read();
        Future<BTMessage> menuMessageFuture = readBT();
        BTMessage menuMessage = null;
        try {
            menuMessage = menuMessageFuture.get();
            Log.d(TAG, "Received in menu");
        } catch (InterruptedException e) {
            Log.e(TAG, "Getting the BTMessage was interrupted");
        } catch (ExecutionException e) {
            Log.e(TAG, "Getting the BTMessage had an Execution Exception");
        }
        if (menuMessage != null) {
            if (menuMessage.getHeader().equals(Status.OK.toString())) {
                closeBluetoothSocket();
                return new Gson().fromJson(menuMessage.getBodyString(), Menu.class);
            }
        }
        closeBluetoothSocket();
        return null;

    }


    // TODO remove this
    public Protos.PaymentRequest getRequest(String uri) {
        BitcoinURI mUri = null;
        try {
            mUri = new BitcoinURI(TestNet3Params.get(), uri);
        } catch (BitcoinURIParseException e) {
            Log.d(TAG, "Bitcoin URI Parse Exception");
        }

        org.bitcoinj.core.Address address = mUri.getAddress();
        Coin amount = mUri.getAmount();
        String memo = mUri.getMessage();
        String url = mUri.getPaymentRequestUrl();
        Protos.PaymentRequest.Builder requestBuilder = PaymentProtocol.createPaymentRequest(TestNet3Params.get(), amount, address, memo, url, null);
        Protos.PaymentRequest request = requestBuilder.build();
        return request;
    }

    public Protos.Payment transactionSigner(Protos.PaymentRequest request) throws PaymentProtocolException {
        Protos.Payment mPayment = null;
        PaymentSession mPaymentSession = new PaymentSession(request, false);
        if (mPaymentSession.isExpired()) {
            return mPayment;
        }
        else {
            Wallet.SendRequest mSendRequest = mPaymentSession.getSendRequest();
            //mWallet.signTransaction(Wallet.SendRequest.forTx(mSendRequest.tx));
            try {
                mWallet.completeTx(Wallet.SendRequest.forTx(mSendRequest.tx));
                // TODO uncomment this to decrease Bitcoin in wallet, when working.
                //mWallet.commitTx(mSendRequest.tx);
            } catch (InsufficientMoneyException e) {
                // TODO this
                Log.e(TAG, "Insufficient Money");
            }
            try {
                mPayment = mPaymentSession.getPayment(ImmutableList.of(mSendRequest.tx), mWallet.freshReceiveAddress(), null);
            } catch (IOException e) {
                // TODO this
                Log.e(TAG, "Error making payment");
            }
            if (mPayment != null) {
                return mPayment;
            } else {
                // TODO this
                return mPayment;
            }
        }
    }

    public static boolean sendPayment(Protos.Payment payment) {
        return write(new BTMessageBuilder(payment).build());
    }

    public static boolean sendOrders(Menu menu, BluetoothSocket socket) {
        return write(new BTMessageBuilder(menu).build(), socket);
    }

    // TODO remember to change this
    public Protos.PaymentRequest getPaymentRequest(){
        //return getRequest("bitcoin:mhKuHFtbzF5khjNSDDbM8z6x18avzt4EgY?amount=0.001&r=http://www.b-till.com");
        Bill bill = fetchBill();
        if (bill != null) {
            return bill.getRequest();
        }
        else {
            return null;
        }

    }

    public Bill fetchBill(){
        BTMessage billMessage = read();
        Bill bill;
        if (billMessage.getHeader().equals(Status.OK.toString())) {
            bill = new Gson().fromJson(billMessage.getBodyString(), Bill.class);
        }
        else {
            bill = null;
        }
        return bill;
    }


    public BTMessage read(BluetoothSocket socket) {
        ConnectedThread mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        return new Gson().fromJson(mConnectedThread.read(), BTMessage.class);
    }

    public Future<BTMessage> readBT(final BluetoothSocket socket) {
        return pool.submit(new Callable<BTMessage>() {
            @Override
            public BTMessage call() throws Exception {
                ConnectedThread mConnectedThread = new ConnectedThread(socket);
                mConnectedThread.start();
                Future<String> messageFuture = mConnectedThread.readFuture();
                return new Gson().fromJson(messageFuture.get(), BTMessage.class);
            }
        });
    }

    public static boolean write(BTMessage message, BluetoothSocket socket) {
        ConnectedThread mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        return mConnectedThread.write(message);
    }

    public static Bitmap generateQR(Wallet wallet) {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        Bitmap mBitmap = null;
        try {
            bitMatrix = writer.encode("bitcoin:" + wallet.currentReceiveAddress().toString(), BarcodeFormat.QR_CODE, 512, 512);
            mBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565);
            for (int x = 0; x < 512; x++) {
                for (int y = 0; y < 512; y++) {
                    if (bitMatrix.get(x, y))
                        mBitmap.setPixel(x, y, Color.BLACK);
                    else
                        mBitmap.setPixel(x, y, Color.WHITE);
                }
            }
        } catch (WriterException e) {
            Log.e(TAG, "QRWriter error");
        }
        return mBitmap;
    }


}
