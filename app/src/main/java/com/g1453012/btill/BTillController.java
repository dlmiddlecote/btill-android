package com.g1453012.btill;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

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

/**
 * Created by dlmiddlecote on 18/02/15.
 */
public class BTillController {

    private static final String TAG = "BTillController";

    private Wallet mWallet;

    private BluetoothSocket mBluetoothSocket = null;

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private NewBill mBill = null;

    private final ExecutorService pool = Executors.newFixedThreadPool(20);

    public Wallet getWallet() {
        return mWallet;
    }

    public void setWallet(Wallet wallet) {
        mWallet = wallet;
    }

    public BluetoothSocket getBluetoothSocket() {
        return mBluetoothSocket;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        mBluetoothSocket = bluetoothSocket;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public BTillController() {
    }

    public void closeBluetoothSocket() {
        try {
            mBluetoothSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing socket");
        }
    }

    // TODO Update get Menu to pull menu from Till.
    public Menu getMenu() {

        sendMenuRequest();
        Log.d(TAG, "Sends Menu Request");
        return receiveMenu();
    }

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

    public boolean sendPayment(Protos.Payment payment) {
        Future<Boolean> writeFuture = writeBT(new BTMessageBuilder(payment).build());
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


    public boolean sendOrders(Menu menu) {
        Future<Boolean> writeFuture = writeBT(new BTMessageBuilder(menu).build());
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

    // TODO remember to change this
    public Protos.PaymentRequest getPaymentRequest(){
        //return getRequest("bitcoin:mhKuHFtbzF5khjNSDDbM8z6x18avzt4EgY?amount=0.001&r=http://www.b-till.com&message=Payment%20for%20coffee");
        Log.d(TAG, "Going to fetch Bill");
        //fetchBill();
        Future<Boolean> fetchBillFuture = fetchBillFuture();
        try {
            if (fetchBillFuture.get()) {
                Log.d(TAG, "Received Bill");
                closeBluetoothSocket();
                return mBill.getRequest();
            }
            else {
                Log.d(TAG, "Null Bill");
                closeBluetoothSocket();
                return null;
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "Getting the Bill was interrupted");
        } catch (ExecutionException e) {
            Log.e(TAG, "Getting the Bill had an Execution Exception");
        }
        Log.d(TAG, "Null Bill -- outside try");
        closeBluetoothSocket();
        return null;

    }

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

    }

    public Future<Boolean> fetchBillFuture() {
        return pool.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Future<BTMessage> billMessageFuture = readBT();
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
    }

    public Future<Receipt> getReceipt() {
        return pool.submit(new Callable<Receipt>() {
            @Override
            public Receipt call() throws Exception {
                Future<BTMessage> receiptFuture = readBT();
                BTMessage receiptMessage = null;
                try {
                    receiptMessage = receiptFuture.get();
                } catch (InterruptedException e) {
                    Log.e(TAG, "Getting the BTMessage was interrupted");
                } catch (ExecutionException e) {
                    Log.e(TAG, "Getting the BTMessage had an Execution Exception");
                }

                if (receiptMessage != null && receiptMessage.getHeader().equals(Status.OK.toString())) {
                    Log.d(TAG, "Created Receipt");
                    closeBluetoothSocket();
                    return new Gson().fromJson(receiptMessage.getBodyString(), Receipt.class);
                }
                else {
                    Log.e(TAG, "Receipt is null");
                    closeBluetoothSocket();
                    return null;
                }
            }
        });

    }

    public Future<BTMessage> readBT() {
        return pool.submit(new Callable<BTMessage>() {
            @Override
            public BTMessage call() throws Exception {
                ConnectedThread mConnectedThread = new ConnectedThread(mBluetoothSocket);
                mConnectedThread.start();
                Future<String> messageFuture = mConnectedThread.readFuture();
                return new Gson().fromJson(messageFuture.get(), BTMessage.class);
            }
        });
    }

    public Future<Boolean> writeBT(final BTMessage message) {
        return pool.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                ConnectedThread mConnectedThread = new ConnectedThread(mBluetoothSocket);
                mConnectedThread.start();
                Future<Boolean> writeFuture = mConnectedThread.writeFuture(message);
                return writeFuture.get();
            }
        });
    }

    public Bitmap generateQR() {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        Bitmap mBitmap = null;
        try {
            bitMatrix = writer.encode("bitcoin:" + mWallet.currentReceiveAddress().toString(), BarcodeFormat.QR_CODE, 512, 512);
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
