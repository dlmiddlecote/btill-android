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
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by dlmiddlecote on 13/03/15.
 */
public class ReceiptStore{

    private static final String TAG = "ReceiptStore";

    private TreeMap<Integer, Pair<Receipt, Menu>> mReceipts;
    private String mReceiptStoreFile;
    private Context mContext;
    ArrayList<Integer> keys;

    public ReceiptStore(Context context, String file) {
        Log.d(TAG, "Inside Constructor");
        mContext = context;
        mReceiptStoreFile = file;
        try {
            read();
            Log.d(TAG, "Read Receipt Store");
        } catch (Exception e) {
            Log.d(TAG, "Error loading from file");
            mReceipts = new TreeMap<Integer, Pair<Receipt, Menu>>();
        }
        keys = new ArrayList<Integer>();
        for (TreeMap.Entry<Integer, Pair<Receipt, Menu>> entry : mReceipts.entrySet()) {
            keys.add(entry.getKey());
            Log.d(TAG, "" + entry.getKey());
        }
    }

    public void add(Receipt receipt, Menu menu, int id) {
        if (receipt != null) {
            mReceipts.put(id, new Pair(receipt, menu));
            keys.add(id);
            try {
                write();
                Log.d(TAG, "Written Receipt Store");
                /*for (int i = 1; i <= mReceipts.size(); i++) {
                    Log.d(TAG, mReceipts.get(i).first.getGbp().toString());
                }*/
            } catch (IOException e) {
                Log.e(TAG, "Didn't write");
            }
        }
    }

    public void remove(int id) {
        Log.d(TAG, "Inside Remove");
        mReceipts.remove(id);
        try {
            write();
            Log.d(TAG, "Written Receipt Store");
        } catch (IOException e) {
            Log.e(TAG, "Didn't write");
        }
    }

    private void read() throws Exception {
        FileInputStream fileIn = mContext.openFileInput(mReceiptStoreFile);
        byte[] read = new byte[1048576];
        int bytes = fileIn.read(read);
        String string = new String(read, 0, bytes);
        mReceipts = new Gson().fromJson(string , new TypeToken<TreeMap<Integer, Pair<Receipt, Menu>>>() {}.getType());
    }

    private void write() throws IOException {
        FileOutputStream fileOut = mContext.openFileOutput(mReceiptStoreFile, Context.MODE_PRIVATE);
        String mReceiptsJson = new Gson().toJson(mReceipts);
        fileOut.write(mReceiptsJson.getBytes());
        fileOut.close();
    }

    public void refreshReceipts() {
        Log.d(TAG, "Refreshing Receipts");
        mReceipts.clear();
        try {
            read();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        mReceipts = new TreeMap<Integer, Pair<Receipt, Menu>>();
        try {
            write();
            Log.d(TAG, "Reset Receipt Store");
        } catch (IOException e) {
            Log.e(TAG, "Didn't Permanently Reset");
        }
    }

    public int getSize() { return mReceipts.size(); }

    public Integer getID(int position) {
        return keys.get(position);

    }

}
