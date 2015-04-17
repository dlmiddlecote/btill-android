package com.g1453012.btill.UI.HomeScreenFragments.Receipts;

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
public class ReceiptStore {

    private static final String TAG = "NewReceiptStore";

    private ArrayList<SavedReceipt> mReceipts;
    private String mReceiptStoreFile;
    private Context mContext;
    private Integer maxReceiptId;

    public ReceiptStore(Context context, String receiptStoreFile) {
        mReceiptStoreFile = receiptStoreFile;
        mContext = context;
        mReceipts = new ArrayList<SavedReceipt>();
        ArrayList<SavedReceipt> receiptsToAdd = read();
        if (receiptsToAdd != null) {
            mReceipts.addAll(receiptsToAdd);
        }
        maxReceiptId = maxReceiptId();
    }

    public void add(int receiptID, String restaurant, Receipt receipt, Menu orderedMenu) {
        if (receipt != null && orderedMenu != null) {
            mReceipts.add(new SavedReceipt(receiptID, restaurant, receipt, orderedMenu));
            try {
                write();
                Log.d(TAG, "Written Receipt Store");
            } catch (IOException e) {
                Log.e(TAG, "Didn't write");
            }
        }
    }

    public void remove(int receiptID) {
        for (SavedReceipt receipt : mReceipts) {
            if (receipt.getReceiptID() == receiptID) {
                mReceipts.remove(receipt);
                try {
                    write();
                    Log.d(TAG, "Written Receipt Store");
                } catch (IOException e) {
                    Log.e(TAG, "Didn't write");
                }
                break;
            }
        }
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
        for (SavedReceipt receipt : mReceipts) {
            if (receipt.getReceiptID() == receiptID) {
                return receipt.getRestaurant();
            }
        }
        return null;
    }

    public Receipt getReceipt(int receiptID) {
        for (SavedReceipt receipt : mReceipts) {
            if (receipt.getReceiptID() == receiptID) {
                return receipt.getReceipt();
            }
        }
        return null;
    }

    public Menu getMenu(int receiptID) {
        for (SavedReceipt receipt : mReceipts) {
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

    private Integer maxReceiptId() {
        Integer max = new Integer(0);
        for (SavedReceipt receipt : mReceipts) {
            if (receipt.getReceiptID() > max) {
                max = receipt.getReceiptID();
            }
        }
        return max;
    }

}
