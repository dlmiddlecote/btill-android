package com.g1453012.btill;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.g1453012.btill.Shared.Bill;
import com.g1453012.btill.Shared.GBP;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.MenuItem;

import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.protocols.payments.PaymentProtocol;
import org.bitcoinj.protocols.payments.PaymentProtocolException;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.uri.BitcoinURIParseException;

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

    public Menu getMenu() {

        ArrayList<MenuItem> mMenuItems = new ArrayList<MenuItem>();
        mMenuItems.add(new MenuItem("Chicken", new GBP(200)));
        mMenuItems.add(new MenuItem("More Chicken", new GBP(100)));
        mMenuItems.add(new MenuItem("Hot Wings", new GBP(250)));
        mMenuItems.add(new MenuItem("Chicken Burger", new GBP(300)));
        mMenuItems.add(new MenuItem("Popcorn Chicken", new GBP(150)));
        return new Menu(mMenuItems);
    }

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
        return null;
    }

    // TODO remember to change this
    public Protos.PaymentRequest getPaymentRequest(){
        return getRequest("bitcoin:mhKuHFtbzF5khjNSDDbM8z6x18avzt4EgY?amount=0.001&r=http://www.b-till.com");
    }

}
