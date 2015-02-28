package com.g1453012.btill;

import android.bluetooth.BluetoothSocket;

import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Wallet;

public class PersistentParameters {

    private Wallet mWallet;
    private BluetoothSocket mSocket;

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
}
