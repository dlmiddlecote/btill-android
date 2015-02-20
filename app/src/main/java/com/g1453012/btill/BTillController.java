package com.g1453012.btill;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.g1453012.btill.Shared.Menu;
import com.google.gson.Gson;

import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;

/**
 * Created by dlmiddlecote on 18/02/15.
 */
public class BTillController {

    private static final String TAG = "BTillController";

    private Wallet mWallet;
    private TestNet3Params netParams = TestNet3Params.get();

    BluetoothSocket mBluetoothSocket = null;

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

        /*try {
            mWallet.loadFromFile(new File("wallet.dat"));
        } catch (UnreadableWalletException e) {
            mWallet = new Wallet(netParams);
            // TODO Add in some coins from testnet
        }*/
    }

    /*public Order[] getMenu() {

    }



    public boolean confirmTransaction(Protos.Transaction trans){

    }

    public void cancelTransaction(Protos.Transaction trans) {

    }*/

    public boolean sendOrders(Menu menu) {
        String jsonFormatted = formatOrders(menu);

        ConnectedThread mConnectedThread = new ConnectedThread(mBluetoothSocket);
        if (mConnectedThread != null) {
            mConnectedThread.start();
            return mConnectedThread.write(jsonFormatted);
        }

        Log.d(TAG, "Connected Thread was null");
        return false;
    }

    public String formatOrders(Menu menu) {
        Gson gson = new Gson();
        String json = gson.toJson(menu, Menu.class);
        Log.d(TAG, json);

        return json;
    }




}
