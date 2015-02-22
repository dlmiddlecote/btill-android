package com.g1453012.btill;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.g1453012.btill.Shared.GBP;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.MenuItem;
import com.google.gson.Gson;

import org.bitcoinj.core.Wallet;
import org.bitcoinj.kits.WalletAppKit;

import java.util.ArrayList;

/**
 * Created by dlmiddlecote on 18/02/15.
 */
public class BTillController {

    private static final String TAG = "BTillController";

    private Wallet mWallet;

    private WalletAppKit mWalletAppKit = null;

    private BluetoothSocket mBluetoothSocket = null;


    public WalletAppKit getWalletAppKit() {
        return mWalletAppKit;
    }

    public void setWalletAppKit(WalletAppKit walletAppKit) {
        mWalletAppKit = walletAppKit;
    }

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

    /*
    public boolean confirmTransaction(Protos.Transaction trans){

    }

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




}
