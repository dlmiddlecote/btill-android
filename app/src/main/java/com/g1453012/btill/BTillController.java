package com.g1453012.btill;

import android.app.Dialog;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.UnreadableWalletException;
import org.bitcoinj.wallet.Protos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dlmiddlecote on 18/02/15.
 */
public class BTillController {

    private static final String TAG = "BTillController";

    private Wallet mWallet;
    private TestNet3Params netParams = TestNet3Params.get();

    BluetoothSocket mBluetoothSocket = null;

    public BluetoothSocket getBluetoothSocket() {
        return mBluetoothSocket;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        mBluetoothSocket = bluetoothSocket;
    }

    public BTillController() {
        try {
            mWallet.loadFromFile(new File("wallet.dat"));
        } catch (UnreadableWalletException e) {
            mWallet = new Wallet(netParams);
            // TODO Add in some coins from testnet
        }
    }

    /*public Order[] getMenu() {

    }



    public boolean confirmTransaction(Protos.Transaction trans){

    }

    public void cancelTransaction(Protos.Transaction trans) {

    }*/

    public boolean sendOrders(Order[] orders) {
        String jsonFormatted = formatOrders(orders);

        ConnectedThread mConnectedThread = new ConnectedThread(mBluetoothSocket);
        if (mConnectedThread != null) {
            mConnectedThread.start();
            return mConnectedThread.write(jsonFormatted);
        }

        Log.d(TAG, "Connected Thread was null");
        return false;
    }

    public String formatOrders(Order[] orders) {
        Gson gson = new Gson();
        List<Order> nonZeroOrders = new ArrayList<Order>();

        for (Order order: orders)
        {
            if (order.getQuantity()!=0)
                nonZeroOrders.add(order);
        }

        String json = gson.toJson(nonZeroOrders, new TypeToken <ArrayList<Order>>(){}.getType());

        Log.d(TAG, json);

        return json;
    }




}
