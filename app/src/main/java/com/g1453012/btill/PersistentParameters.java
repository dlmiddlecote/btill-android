package com.g1453012.btill;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.g1453012.btill.Shared.Bill;
import com.g1453012.btill.Shared.LocationData;
import com.g1453012.btill.UI.HomeScreenFragments.Balance.BalanceFragment;
import com.g1453012.btill.UI.HomeScreenFragments.MainScreen;
import com.g1453012.btill.UI.HomeScreenFragments.Order.OrderFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Receipts.ReceiptFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Receipts.ReceiptStore;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Wallet;

import java.util.TreeMap;

public class PersistentParameters {

    private final static String TAG = "Persistent Parameters";

    private Wallet mWallet;
    private BluetoothSocket mSocket;
    private Bill mBill;
    private Transaction mTx;
    private LocationData mLocationData;
    private MainScreen mMainScreen;
    private ReceiptStore mReceiptStore;
    private ReceiptFragment mReceiptFragment;
    private BalanceFragment mBalanceFragment;
    private OrderFragment mOrderFragment;

    public PersistentParameters(Context context, String file, boolean loadReceipts) {
        if (loadReceipts) {
            mReceiptStore = new ReceiptStore(context, file);
            Log.d(TAG, "Made the Receipt");
        }
        mLocationData = new LocationData(new TreeMap<String, Double>());
    }

    public MainScreen getMainScreen() {
        return mMainScreen;
    }

    public void setMainScreen(MainScreen mainScreen) {
        mMainScreen = mainScreen;
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

    public OrderFragment getOrderFragment() {
        return mOrderFragment;
    }

    public void setOrderFragment(OrderFragment orderFragment) {
        mOrderFragment = orderFragment;
    }

    public LocationData getLocationData() {
        return mLocationData;
    }
}
