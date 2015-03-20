package com.g1453012.btill;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.g1453012.btill.Shared.Bill;
import com.g1453012.btill.UI.HomeScreenFragments.Balance.BalanceFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Receipts.ReceiptFragment;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Wallet;

public class PersistentParameters{

    private final static String TAG = "Persistent Parameters";

    private Wallet mWallet;
    private BluetoothSocket mSocket;
    private Bill mBill;
    private Transaction mTx;
    private ReceiptStore mReceiptStore;

    // TODO testing
    private NewReceiptStore mNewReceiptStore;

    private ReceiptFragment mReceiptFragment;
    private BalanceFragment mBalanceFragment;

    public PersistentParameters(Context context, String file) {
        //mReceiptStore = new ReceiptStore(context, file);
        //mReceiptStore.resetStoreForTesting();
        mNewReceiptStore = new NewReceiptStore(context, file);
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

    public NewReceiptStore getNewReceiptStore() {
        return mNewReceiptStore;
    }

    /*public void refreshReceiptStore() {
        mReceiptStore.refreshReceipts();
    }*/

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

    public ReceiptFragment getReceiptFragment() {
        return mReceiptFragment;
    }

    public void setReceiptFragment(ReceiptFragment receiptFragment) {
        mReceiptFragment = receiptFragment;
    }

    public BalanceFragment getBalanceFragment() {
        return mBalanceFragment;
    }

    public void setBalanceFragment(BalanceFragment balanceFragment) {
        mBalanceFragment = balanceFragment;
    }
}
