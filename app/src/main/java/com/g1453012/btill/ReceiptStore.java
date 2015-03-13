package com.g1453012.btill;

import android.util.Log;

import com.g1453012.btill.Shared.Receipt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Created by dlmiddlecote on 13/03/15.
 */
public class ReceiptStore {

    private static final String TAG = "ReceiptStore";

    private HashMap<Integer, Receipt > mReceipts;
    private File mReceiptStoreFile;

    public ReceiptStore(File file) {
        mReceiptStoreFile = file;
        try {
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            mReceipts = (HashMap<Integer, Receipt>) in.readObject();
            in.close();
            fileIn.close();
        } catch (Exception e) {
            mReceipts = null;
        }
    }

    public void add(Receipt receipt, int id) {
        mReceipts.put(id, receipt);
        try {
            write();
        } catch (IOException e) {
            Log.e(TAG, "Didn't write");
        }
    }

    public Receipt get(int ID) {
        return mReceipts.get(ID);
    }

    public void write() throws IOException {
        FileOutputStream fileOut = new FileOutputStream(mReceiptStoreFile);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(mReceipts);
        out.close();
        fileOut.close();
    }

}
