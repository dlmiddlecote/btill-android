package com.g1453012.btill;

import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.g1453012.btill.Bluetooth.ConnectedThread;
import com.g1453012.btill.Shared.BTMessage;
import com.g1453012.btill.Shared.BTMessageBuilder;
import com.g1453012.btill.Shared.Bill;
import com.g1453012.btill.Shared.GBP;
import com.g1453012.btill.Shared.LocationData;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.OrderConfirmation;
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

    public static Protos.Payment transactionSigner(Protos.PaymentRequest request, final PersistentParameters params) throws PaymentProtocolException, InsufficientMoneyException {
        Protos.Payment mPayment = null;
        PaymentSession mPaymentSession = new PaymentSession(request, false);
        if (mPaymentSession.isExpired()) {
            return mPayment;
        }
        else {
            Wallet.SendRequest mSendRequest = mPaymentSession.getSendRequest();
            //mWallet.signTransaction(Wallet.SendRequest.forTx(mSendRequest.tx));
            //try {
            if (params.getWallet() != null) {
                params.getWallet().completeTx(Wallet.SendRequest.forTx(mSendRequest.tx));
                Log.d(TAG, "Signed Transaction");
                // TODO uncomment this to decrease Bitcoin in wallet, when working.
                //mWallet.commitTx(mSendRequest.tx);
                params.setTx(mSendRequest.tx);

                //} catch (InsufficientMoneyException e) {
                //    // TODO this
                //    Log.e(TAG, "Insufficient Money");
                //}
                try {
                    mPayment = mPaymentSession.getPayment(ImmutableList.of(mSendRequest.tx), params.getWallet().freshReceiveAddress(), null);
                } catch (IOException e) {
                    // TODO this
                    Log.e(TAG, "Error making payment");
                }
            }
            if (mPayment != null) {
                return mPayment;
            } else {
                // TODO this
                return mPayment;
            }
        }
    }

    public static Future<Bill> processOrders(final Menu menu, final BluetoothSocket socket) {
        return pool.submit(new Callable<Bill>() {
            @Override
            public Bill call() throws Exception {
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

    private static Bill receiveBill(final BluetoothSocket socket) {
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
        try {
            socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Couldn't close socket");
        }
        if (billMessage != null && billMessage.getHeader().equals(Status.OK.toString())) {
            return new Gson().fromJson(billMessage.getBodyString(), Bill.class);
        }
        else {
            return null;
        }
    }


    public static Future<OrderConfirmation> processPayment(final int orderId, final Protos.Payment payment, final GBP gbpAmount, final Coin btcAmount, final LocationData locationData, final BluetoothSocket socket) {
        return pool.submit(new Callable<OrderConfirmation>() {
            @Override
            public OrderConfirmation call() throws Exception {
                while (!sendPayment(orderId, payment, gbpAmount, btcAmount, locationData, socket)) {}
                return getOrderConfirmation(socket);
            }
        });
    }

    private static boolean sendPayment(int orderId, Protos.Payment payment, GBP gbpAmount, Coin btcAmount, LocationData locationData, final BluetoothSocket socket) {
        Future<Boolean> writeFuture = writeBT(new BTMessageBuilder(orderId, payment, gbpAmount, btcAmount, locationData).build(), socket);
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

    private static OrderConfirmation getOrderConfirmation(final BluetoothSocket socket) {
        Future<BTMessage> confFuture = readBT(socket);
        BTMessage confMessage = null;
        try {
            confMessage = confFuture.get();
        } catch (InterruptedException e) {
            Log.e(TAG, "Getting the BTMessage was interrupted");
        } catch (ExecutionException e) {
            Log.e(TAG, "Getting the BTMessage had an Execution Exception");
        }
        try {
            if (confMessage != null && confMessage.getHeader().equals(Status.OK.toString())) {
                Log.d(TAG, "Created Order Confirmation");
                //closeBluetoothSocket();
                socket.close();
                return new Gson().fromJson(confMessage.getBodyString(), OrderConfirmation.class);
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
        Bitmap mBitmap;
        Bitmap mapToReturn = null;
        try {
            bitMatrix = writer.encode("bitcoin:" + wallet.currentReceiveAddress().toString(), BarcodeFormat.QR_CODE, 512, 512);
            mBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565);
            mapToReturn = mBitmap.copy(Bitmap.Config.RGB_565, true);
            for (int x = 0; x < mapToReturn.getWidth(); x++) {
                for (int y = 0; y < mapToReturn.getHeight(); y++) {
                    if (bitMatrix.get(x, y))
                        mapToReturn.setPixel(x, y, Color.BLACK);
                    else
                        mapToReturn.setPixel(x, y, Color.WHITE);
                }
            }
        } catch (WriterException e) {
            Log.e(TAG, "QRWriter error");
        }
        return mapToReturn;
    }


}
