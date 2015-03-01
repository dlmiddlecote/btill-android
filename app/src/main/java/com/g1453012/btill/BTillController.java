package com.g1453012.btill;

import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.g1453012.btill.Bluetooth.ConnectedThread;
import com.g1453012.btill.Shared.BTMessage;
import com.g1453012.btill.Shared.BTMessageBuilder;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.NewBill;
import com.g1453012.btill.Shared.Receipt;
import com.g1453012.btill.Shared.Status;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.protocols.payments.PaymentProtocolException;
import org.bitcoinj.protocols.payments.PaymentSession;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BTillController {

    private static final String TAG = "BTillController";

    public static ExecutorService pool = Executors.newFixedThreadPool(10);


    // TODO Update get Menu to pull menu from Till.
    /*public Menu getMenu() {

        sendMenuRequest();
        Log.d(TAG, "Sends Menu Request");
        return receiveMenu();
    }*/

    public static Future<Menu> getMenuFuture(final BluetoothSocket socket) {
        return pool.submit(new Callable<Menu>() {
            @Override
            public Menu call() throws Exception {
                while (!sendMenuRequest(socket)){};
                Log.d(TAG, "Sends Menu Request in Future");
                return receiveMenu(socket);
            }
        });
    }


    private static boolean sendMenuRequest(BluetoothSocket socket) {
        Future<Boolean> writeFuture = writeBT(new BTMessageBuilder("REQUEST_MENU").build(), socket);
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

    private static Menu receiveMenu(final BluetoothSocket socket) {
        //BTMessage menuMessage = read();
        Future<BTMessage> menuMessageFuture = readBT(socket);
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
                //closeBluetoothSocket();
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Couldn't close socket");
                }
                return new Gson().fromJson(menuMessage.getBodyString(), Menu.class);
            }
        }
        //closeBluetoothSocket();
        try {
            socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Couldn't close socket");
        }
        return null;

    }

    // TODO remove this
    /*public Protos.PaymentRequest getRequest(String uri) {
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
    }*/

    public static Protos.Payment transactionSigner(Protos.PaymentRequest request, final Wallet mWallet) throws PaymentProtocolException {
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


/*
    public void fetchBill(){
        Future<BTMessage> billMessageFuture = readBT();
        Log.d(TAG, "Starts to read bill");
        BTMessage billMessage = null;
        try {
            billMessage = billMessageFuture.get();
        } catch (InterruptedException e) {
            Log.e(TAG, "Getting the BTMessage was interrupted");
        } catch (ExecutionException e) {
            Log.e(TAG, "Getting the BTMessage had an Execution Exception");
        }
        Log.d(TAG, "Has Bill");
        if (billMessage.getHeader().equals(Status.OK.toString())) {
            mBill = new Gson().fromJson(billMessage.getBodyString(), NewBill.class);
        }
        else {
            mBill = null;
        }
        return mBill;

    }*/

    public static Future<NewBill> processOrders(final Menu menu, final BluetoothSocket socket) {
        return pool.submit(new Callable<NewBill>() {
            @Override
            public NewBill call() throws Exception {
                while (!sendOrders(menu, socket)) {}
                return receiveBill(socket);
            }
        });
    }

    private static boolean sendOrders(Menu menu, final BluetoothSocket socket) {
        Future<Boolean> writeFuture = writeBT(new BTMessageBuilder(menu).build(), socket);
        try {
            return writeFuture.get().booleanValue();
        }
        catch (InterruptedException e) {
            Log.e(TAG, "Sending the Payment was interrupted");
        } catch (ExecutionException e) {
            Log.e(TAG, "Sending the Payment had an Execution Exception");
        }
        return false;
    }

    /*private NewBill receiveBill(final BluetoothSocket socket) {
        Future<NewBill> billFuture = fetchBillFuture(socket);
        try {
            socket.close();
            return billFuture.get();
        } catch (InterruptedException e) {
            Log.e(TAG, "Getting the Bill was interrupted");
        } catch (ExecutionException e) {
            Log.e(TAG, "Getting the Bill had an Execution Exception");
        } catch (IOException e) {
            Log.e(TAG, "Couldn't close socket");
        }
        try {
            socket.close();
        }
        catch (IOException e) {
            Log.e(TAG, "Couldn't close socket");
        }
        return null;
    }*/

    private static NewBill receiveBill(final BluetoothSocket socket) {
        Future<BTMessage> billMessageFuture = readBT(socket);
        Log.d(TAG, "Starts to read bill");
        //BTMessage billMessage = read();
        BTMessage billMessage = null;
        try {
            billMessage = billMessageFuture.get();
            Log.d(TAG, "Has Bill");
        } catch (InterruptedException e) {
            Log.e(TAG, "Getting the BTMessage was interrupted");
        } catch (ExecutionException e) {
            Log.e(TAG, "Getting the BTMessage had an Execution Exception");
        }

        if (billMessage.getHeader().equals(Status.OK.toString())) {
            return new Gson().fromJson(billMessage.getBodyString(), NewBill.class);
        }
        else {
            return null;
        }
    }





    // TODO remember to change this
    /*public Protos.PaymentRequest getPaymentRequest(final BluetoothSocket socket){
        //return getRequest("bitcoin:mhKuHFtbzF5khjNSDDbM8z6x18avzt4EgY?amount=0.001&r=http://www.b-till.com&message=Payment%20for%20coffee");
        Log.d(TAG, "Going to fetch Bill");
        //fetchBill();
        Future<Boolean> fetchBillFuture = fetchBillFuture(socket);
        try {
            if (fetchBillFuture.get()) {
                Log.d(TAG, "Received Bill");
                //closeBluetoothSocket();
                socket.close();
                return mBill.getRequest();
            }
            else {
                Log.d(TAG, "Null Bill");
                //closeBluetoothSocket();
                socket.close();
                return null;
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "Getting the Bill was interrupted");
        } catch (ExecutionException e) {
            Log.e(TAG, "Getting the Bill had an Execution Exception");
        } catch (IOException e) {
            Log.e(TAG, "Couldn't close Socket");
        }
        Log.d(TAG, "Null Bill -- outside try");
        //closeBluetoothSocket();
        try {
            socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Couldn't close socket");
        }
        return null;

    }*/

    /*public Future<Boolean> fetchBillFuture(final BluetoothSocket socket) {
        return pool.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Future<BTMessage> billMessageFuture = readBT(socket);
                Log.d(TAG, "Starts to read bill");
                //BTMessage billMessage = read();
                BTMessage billMessage = null;
                try {
                    billMessage = billMessageFuture.get();
                    Log.d(TAG, "Has Bill");
                } catch (InterruptedException e) {
                    Log.e(TAG, "Getting the BTMessage was interrupted");
                } catch (ExecutionException e) {
                    Log.e(TAG, "Getting the BTMessage had an Execution Exception");
                }

                if (billMessage.getHeader().equals(Status.OK.toString())) {
                    mBill = new Gson().fromJson(billMessage.getBodyString(), NewBill.class);
                    return Boolean.TRUE;
                }
                else {
                    mBill = null;
                    return Boolean.FALSE;
                }
            }
        });
    }*/

    public static Future<Receipt> processPayment(final Protos.Payment payment, final BluetoothSocket socket) {
        return pool.submit(new Callable<Receipt>() {
            @Override
            public Receipt call() throws Exception {
                while (!sendPayment(payment, socket)) {}
                return getReceipt(socket);
            }
        });
    }

    private static boolean sendPayment(Protos.Payment payment, final BluetoothSocket socket) {
        Future<Boolean> writeFuture = writeBT(new BTMessageBuilder(payment).build(), socket);
        try {
            return writeFuture.get().booleanValue();
        }
        catch (InterruptedException e) {
            Log.e(TAG, "Sending the Payment was interrupted");
        } catch (ExecutionException e) {
            Log.e(TAG, "Sending the Payment had an Execution Exception");
        }
        return false;
    }

    private static Receipt getReceipt(final BluetoothSocket socket) {
        Future<BTMessage> receiptFuture = readBT(socket);
        BTMessage receiptMessage = null;
        try {
            receiptMessage = receiptFuture.get();
        } catch (InterruptedException e) {
            Log.e(TAG, "Getting the BTMessage was interrupted");
        } catch (ExecutionException e) {
            Log.e(TAG, "Getting the BTMessage had an Execution Exception");
        }
        try {
            if (receiptMessage != null && receiptMessage.getHeader().equals(Status.OK.toString())) {
                Log.d(TAG, "Created Receipt");
                //closeBluetoothSocket();
                socket.close();
                return new Gson().fromJson(receiptMessage.getBodyString(), Receipt.class);
            } else {
                Log.e(TAG, "Receipt is null");
                //closeBluetoothSocket();
                socket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Couldn't close socket");
        }
        return null;
    }

    private BTMessage read(BluetoothSocket socket) {
        ConnectedThread mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        return new Gson().fromJson(mConnectedThread.read(), BTMessage.class);
    }

    private static Future<BTMessage> readBT(final BluetoothSocket socket) {
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

    private static boolean write(BTMessage message, BluetoothSocket socket) {
        ConnectedThread mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        return mConnectedThread.write(message);
    }

    private static Future<Boolean> writeBT(final BTMessage message, final BluetoothSocket socket) {
        return pool.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                ConnectedThread mConnectedThread = new ConnectedThread(socket);
                mConnectedThread.start();
                Future<Boolean> writeFuture = mConnectedThread.writeFuture(message);
                return writeFuture.get();
            }
        });
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
