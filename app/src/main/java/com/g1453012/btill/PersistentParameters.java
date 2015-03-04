package com.g1453012.btill;

import android.bluetooth.BluetoothSocket;

import com.g1453012.btill.Shared.NewBill;

import org.bitcoinj.core.Wallet;

public class PersistentParameters {

    private Wallet mWallet;
    private BluetoothSocket mSocket;
    private NewBill mBill;

    public Wallet getWallet() {
        return mWallet;
    }

    public void setWallet(Wallet mWallet) {
        this.mWallet = mWallet;
    }

    public BluetoothSocket getSocket() {
        return mSocket;
    }

    public void setSocket(BluetoothSocket mSocket) {
        this.mSocket = mSocket;
    }

    public NewBill getBill() {
        return mBill;
    }

    public void setBill(NewBill bill) {
        mBill = bill;
    }

    public void resetBill() {
        mBill = null;
    }
}
