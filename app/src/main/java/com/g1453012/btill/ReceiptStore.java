package com.g1453012.btill;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.Receipt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by dlmiddlecote on 13/03/15.
 */
public class ReceiptStore{

    private static final String TAG = "ReceiptStore";

    private HashMap<Integer, Pair<Receipt, Menu>> mReceipts;
    private String mReceiptStoreFile;
    private Context mContext;

    public ReceiptStore(Context context, String file) {
        Log.d(TAG, "Inside Constructor");
        mContext = context;
        mReceiptStoreFile = file;
        try {
            read();
            Log.d(TAG, "Read Receipt Store");
        } catch (Exception e) {
            Log.d(TAG, "Error loading from file");
            mReceipts = new HashMap<Integer, Pair<Receipt, Menu>>();
        }
    }

    public void add(Receipt receipt, Menu menu, int id) {
        if (receipt != null) {
            mReceipts.put(id, new Pair(receipt, menu));
            try {
                write();
                Log.d(TAG, "Written Receipt Store");
                for (int i = 1; i <= mReceipts.size(); i++) {
                    Log.d(TAG, mReceipts.get(i).first.getGbp().toString());
                }
            } catch (IOException e) {
                Log.e(TAG, "Didn't write");
            }
        }
    }

    public void read() throws Exception {
        FileInputStream fileIn = mContext.openFileInput(mReceiptStoreFile);
        byte[] read = new byte[1048576];
        int bytes = fileIn.read(read);
        String string = new String(read, 0, bytes);
        mReceipts = new Gson().fromJson(string , new TypeToken<HashMap<Integer, Pair<Receipt, Menu>>>() {}.getType());
    }

    public void write() throws IOException {
        FileOutputStream fileOut = mContext.openFileOutput(mReceiptStoreFile, Context.MODE_PRIVATE);
        String mReceiptsJson = new Gson().toJson(mReceipts);
        fileOut.write(mReceiptsJson.getBytes());
        fileOut.close();
    }

    public Receipt getReceipt(int ID) {
        return mReceipts.get(ID).first;
    }

    public Menu getMenu(int ID) {
        return mReceipts.get(ID).second;
    }

    public int next() {
        return mReceipts.size() + 1;
    }

    public void resetStoreForTesting() {
        mReceipts = new HashMap<Integer, Pair<Receipt, Menu>>();
        try {
            write();
            Log.d(TAG, "Reset Receipt Store");
        } catch (IOException e) {
            Log.e(TAG, "Didn't Permanently Reset");
        }
    }

    public int getSize() { return mReceipts.size(); }

    public Set<Integer> getKeySet() { return mReceipts.keySet(); }

    public Integer getID(int position) {
        Set<Integer> keys = mReceipts.keySet();
        Integer[] keyArray = (Integer[])keys.toArray();
        return keyArray[position];
    }

}
