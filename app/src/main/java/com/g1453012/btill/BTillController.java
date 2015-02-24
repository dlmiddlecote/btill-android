package com.g1453012.btill;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.g1453012.btill.Shared.BTMessage;
import com.g1453012.btill.Shared.BTMessageBuilder;
import com.g1453012.btill.Shared.Bill;
import com.g1453012.btill.Shared.GBP;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.MenuItem;
import com.g1453012.btill.Shared.Status;
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

    private Bill mBill = null;

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

        // sendMenuRequest();
        // return receiveMenu();
    }

    public boolean sendMenuRequest() {
        return write(new BTMessageBuilder("REQUEST_MENU").build());
    }

    public Menu receiveMenu() {
        BTMessage menuMessage = read();
        if (menuMessage.getHeader().equals(Status.OK.toString())) {
            return new Gson().fromJson(menuMessage.getBodyString(), Menu.class);
        }
        else {
            return null;
        }
    }

    /*public boolean confirmTransaction(Protos.PaymentRequest paymentRequest) {

    }*/

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
        return write(new BTMessageBuilder(payment).build());
    }

    /*
    public void cancelTransaction(Protos.Transaction trans) {

    }*/

    public boolean sendOrders(Menu menu) {
        return write(new BTMessageBuilder(menu).build());
    }

    // TODO remember to change this
    public Protos.PaymentRequest getPaymentRequest(){
        return getRequest("bitcoin:mhKuHFtbzF5khjNSDDbM8z6x18avzt4EgY?amount=0.001&r=http://www.b-till.com");
        /*fetchBill();
        if (mBill != null) {
            return mBill.getRequest();
        }
        else {
            return null;
        }*/

    }

    public void fetchBill(){
        BTMessage billMessage = read();
        if (billMessage.getHeader().equals(Status.OK.toString())) {
            mBill = new Gson().fromJson(billMessage.getBodyString(), Bill.class);
        }
        else {
            mBill = null;
        }
    }

    public BTMessage read() {
        ConnectedThread mConnectedThread = new ConnectedThread(mBluetoothSocket);
        mConnectedThread.start();
        return new Gson().fromJson(mConnectedThread.read(), BTMessage.class);
    }

    public boolean write(BTMessage message) {
        ConnectedThread mConnectedThread = new ConnectedThread(mBluetoothSocket);
        mConnectedThread.start();
        return mConnectedThread.write(message);
    }


}
