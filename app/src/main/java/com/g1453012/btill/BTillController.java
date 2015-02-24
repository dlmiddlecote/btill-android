package com.g1453012.btill;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.g1453012.btill.Shared.Bill;
import com.g1453012.btill.Shared.GBP;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.MenuItem;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

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
import java.util.ArrayList;

/**
 * Created by dlmiddlecote on 18/02/15.
 */
public class BTillController {

    private static final String TAG = "BTillController";

    private Wallet mWallet;

    private BluetoothSocket mBluetoothSocket = null;

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

    public BTillController() {
    }

    // TODO Update get Menu to pull menu from Till.
    public Menu getMenu() {

        ArrayList<MenuItem> mMenuItems = new ArrayList<MenuItem>();
        mMenuItems.add(new MenuItem("Chicken", new GBP(200)));
        mMenuItems.add(new MenuItem("More Chicken", new GBP(100)));
        mMenuItems.add(new MenuItem("Hot Wings", new GBP(250)));
        mMenuItems.add(new MenuItem("Chicken Burger", new GBP(300)));
        mMenuItems.add(new MenuItem("Popcorn Chicken", new GBP(150)));
        return new Menu(mMenuItems);
    }

    /*public boolean confirmTransaction(Protos.PaymentRequest paymentRequest) {

    }*/

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
        Log.d(TAG, url);
        Protos.PaymentRequest.Builder requestbuilder = PaymentProtocol.createPaymentRequest(TestNet3Params.get(), amount, address, memo, url, null);
        Protos.PaymentRequest request = requestbuilder.build();
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
            Log.d(TAG, mPaymentSession.getPaymentDetails().toString());
            try {
                mWallet.completeTx(Wallet.SendRequest.forTx(mSendRequest.tx));
                //mWallet.commitTx(mSendRequest.tx);
            } catch (InsufficientMoneyException e) {
                // TODO this
                Log.e(TAG, "Insufficient Money");
            }
            try {
                mPayment = mPaymentSession.getPayment(ImmutableList.of(mSendRequest.tx), mWallet.freshReceiveAddress(), "Hi");

                if (mPayment == null) {
                    Log.e(TAG, "Payment is null");
                }
                else {
                    Log.d(TAG, mPayment.toString());
                }
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
    public void cancelTransaction(Protos.Transaction trans) {

    }*/

    public boolean sendOrders(Menu menu) {
        String jsonFormatted = formatOrders(menu);

        ConnectedThread mConnectedThread = new ConnectedThread(mBluetoothSocket);
        mConnectedThread.start();
        return mConnectedThread.write(jsonFormatted);
    }

    public String formatOrders(Menu menu) {
        Gson gson = new Gson();
        String json = gson.toJson(menu, Menu.class);
        Log.d(TAG, json);

        return json;
    }

    public Bill getBill(){
        ConnectedThread mConnectedThread = new ConnectedThread(mBluetoothSocket);
        String json = mConnectedThread.read();
        Gson gson = new Gson();

       return gson.fromJson(json ,Bill.class);

    }

    public Protos.PaymentRequest getPaymentRequest(){

        Bill mBill =  getBill();

        return mBill.getRequest();


    }


}
