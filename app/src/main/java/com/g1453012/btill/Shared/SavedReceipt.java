package com.g1453012.btill.Shared;

/**
 * Created by dlmiddlecote on 19/03/15.
 */
public class SavedReceipt {

    private int mReceiptID;
    private String mRestaurant;
    private Receipt mReceipt;
    private Menu mOrderedMenu;

    public SavedReceipt(int receiptID, String restaurant, Receipt receipt, Menu orderedMenu) {
        mReceiptID = receiptID;
        mRestaurant = restaurant;
        mReceipt = receipt;
        mOrderedMenu = orderedMenu;
    }

    public int getReceiptID() {
        return mReceiptID;
    }

    public String getRestaurant() {
        return mRestaurant;
    }

    public Receipt getReceipt() {
        return mReceipt;
    }

    public Menu getOrderedMenu() {
        return mOrderedMenu;
    }
}
