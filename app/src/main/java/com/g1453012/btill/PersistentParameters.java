package com.g1453012.btill;

import android.bluetooth.BluetoothSocket;

import com.g1453012.btill.Shared.Bill;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Wallet;

public class PersistentParameters {

    private Wallet mWallet;
    private BluetoothSocket mSocket;
    private Bill mBill;
    private Transaction mTx;

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
