package com.g1453012.btill;

import android.app.Dialog;
import android.util.Log;

import com.google.gson.Gson;

import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.UnreadableWalletException;
import org.bitcoinj.wallet.Protos;

import java.io.File;

/**
 * Created by dlmiddlecote on 18/02/15.
 */
public class BTillController {

    private static final String TAG = "BTillController";

    private Wallet mWallet;
    private TestNet3Params netParams = TestNet3Params.get();

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

    public void sendOrders(Order[] orders) {
        String jsonFormatted = formatOrders(orders);
    }

    public String formatOrders(Order[] orders) {
        String json = "{";
        for (int i = 0; i < orders.length; i++) {
            Gson g = new Gson();
            if (orders[i].getQuantity() != 0) {
                json += g.toJson(orders[i]) + ", ";
                Log.d(TAG, json);
            }
        }
        json += "}";


        return json;
    }




}
