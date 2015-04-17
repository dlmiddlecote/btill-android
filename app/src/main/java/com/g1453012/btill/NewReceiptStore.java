package com.g1453012.btill;

import android.content.Context;
import android.util.Log;

import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.Receipt;
import com.g1453012.btill.Shared.SavedReceipt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dlmiddlecote on 19/03/15.
 */
public class NewReceiptStore {

    private static final String TAG = "NewReceiptStore";

    private ArrayList<SavedReceipt> mReceipts;
    private String mReceiptStoreFile;
    private Context mContext;
    //private ArrayList<Integer> keys;
    private Integer maxReceiptId;

    public NewReceiptStore(Context context, String receiptStoreFile) {
        mReceiptStoreFile = receiptStoreFile;
        mContext = context;
        mReceipts = new ArrayList<SavedReceipt>();
        ArrayList<SavedReceipt> receiptsToAdd = read();
        if (receiptsToAdd != null) {
            mReceipts.addAll(receiptsToAdd);
        }
        //keys = extractKeys();
        maxReceiptId = maxReceiptId();
    }

    public void add(int receiptID, String restaurant, Receipt receipt, Menu orderedMenu) {
        if (receipt != null && orderedMenu != null) {
            mReceipts.add(new SavedReceipt(receiptID, restaurant, receipt, orderedMenu));
            //keys.add(receiptID);
            try {
                write();
                Log.d(TAG, "Written Receipt Store");
            } catch (IOException e) {
                Log.e(TAG, "Didn't write");
            }
        }
    }

    public void remove(int receiptID) {
        Log.d(TAG, "" + receiptID);
        int i = 0;
        Log.d(TAG, "Step 1");
        for (SavedReceipt receipt: mReceipts) {
            Log.d(TAG, "Step 2" + i);
            if (receipt.getReceiptID() == receiptID) {
                Log.d(TAG, "Step 3");
                mReceipts.remove(i);
                Log.d(TAG, "Step 4");
                //keys.remove(receiptID);
                Log.d(TAG, "Step 5");
                try {
                    write();
                    Log.d(TAG, "Written Receipt Store");
                } catch (IOException e) {
                    Log.e(TAG, "Didn't write");
                }
                Log.d(TAG, "Step 6");
                break;
            }
            Log.d(TAG, "Step 7");
            i++;
        }
        Log.d(TAG, "Step 8");
        ArrayList<SavedReceipt> receiptsToAdd = read();
        if (receiptsToAdd != null) {
            mReceipts.clear();
            mReceipts.addAll(receiptsToAdd);
        }
    }

    public void refreshReceipts() {
        ArrayList<SavedReceipt> receiptsToAdd = read();
        if (receiptsToAdd != null) {
            mReceipts.clear();
            mReceipts.addAll(receiptsToAdd);
        }
    }

    public String getRestaurant(int receiptID) {
        for (SavedReceipt receipt: mReceipts) {
            if (receipt.getReceiptID() == receiptID) {
                return receipt.getRestaurant();
            }
        }
        return null;
    }

    public Receipt getReceipt(int receiptID) {
        for (SavedReceipt receipt: mReceipts) {
            if (receipt.getReceiptID() == receiptID) {
                return receipt.getReceipt();
            }
        }
        return null;
    }

    public Menu getMenu(int receiptID) {
        for (SavedReceipt receipt: mReceipts) {
            if (receipt.getReceiptID() == receiptID) {
                return receipt.getOrderedMenu();
            }
        }
        return null;
    }

    public int getSize() {
        return mReceipts.size();
    }

    public int getID(int position) {
        return mReceipts.get(position).getReceiptID();
    }

    public int next() {
        maxReceiptId++;
        return maxReceiptId;
    }

    public void resetForTesting() {
        mReceipts.clear();
        try {
            write();
            Log.d(TAG, "Reset Receipt Store");
        } catch (IOException e) {
            Log.e(TAG, "Didn't Permanently Reset");
        }
    }


    private ArrayList<SavedReceipt> read() {
        byte[] inputBytes = new byte[1048576];
        int bytes = 0;
        try {
            FileInputStream fileIn = mContext.openFileInput(mReceiptStoreFile);
            bytes = fileIn.read(inputBytes);
        } catch (Exception e) {
            return null;
        }
        String string = new String(inputBytes, 0, bytes);
        if (string != null) {
            return new Gson().fromJson(string, new TypeToken<ArrayList<SavedReceipt>>() {
            }.getType());
        }
        return null;
    }

    private void write() throws IOException {
        FileOutputStream fileOut = mContext.openFileOutput(mReceiptStoreFile, Context.MODE_PRIVATE);
        String mReceiptsJson = new Gson().toJson(mReceipts);
        fileOut.write(mReceiptsJson.getBytes());
        fileOut.close();
    }

    private ArrayList<Integer> extractKeys() {
        ArrayList<Integer> keyList = new ArrayList<Integer>();
        for (SavedReceipt receipt: mReceipts) {
            keyList.add(receipt.getReceiptID());
        }
        return keyList;
    }

    private Integer maxReceiptId() {
        Integer max = new Integer(0);
        for (SavedReceipt receipt: mReceipts) {
            if (receipt.getReceiptID() > max) {
                max = receipt.getReceiptID();
            }
        }
        return max;
    }




}
