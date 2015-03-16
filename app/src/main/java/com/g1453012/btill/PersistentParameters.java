package com.g1453012.btill;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.g1453012.btill.Shared.Bill;
import com.google.gson.Gson;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Wallet;

import java.io.Serializable;

public class PersistentParameters{

    private final static String TAG = "Persistent Parameters";

    private Wallet mWallet;
    private BluetoothSocket mSocket;
    private Bill mBill;
    private Transaction mTx;
    private ReceiptStore mReceiptStore;

    public PersistentParameters(Context context, String file) {
        mReceiptStore = new ReceiptStore(context, file);
        Log.d(TAG, "Made the Receipt");
    }

    public Wallet getWallet() {
        return mWallet;
    }

    public void setWallet(Wallet mWallet) {
        this.mWallet = mWallet;
    }

    public Transaction getTx() {
        return mTx;
    }

    public void setTx(Transaction tx) {
        mTx = tx;
    }

    public void resetTx() {
        mTx = null;
    }

    public ReceiptStore getReceiptStore() {
        return mReceiptStore;
    }

    public BluetoothSocket getSocket() {
        return mSocket;
    }

    public void setSocket(BluetoothSocket mSocket) {
        this.mSocket = mSocket;
    }

    public Bill getBill() {
        return mBill;
    }

    public void setBill(Bill bill) {
        mBill = bill;
    }

    public void resetBill() {
        mBill = null;
    }
}
