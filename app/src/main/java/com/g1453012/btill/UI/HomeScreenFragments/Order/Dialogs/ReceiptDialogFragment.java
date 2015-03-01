package com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.Receipt;


/**
 * Created by dlmiddlecote on 01/03/15.
 */
public class ReceiptDialogFragment extends DialogFragment implements View.OnClickListener {

    private Menu mMenu;
    private Receipt mReceipt;

    public Menu getMenu() {
        return mMenu;
    }

    public void setMenu(Menu menu) {
        mMenu = menu;
    }

    public Receipt getReceipt() {
        return mReceipt;
    }

    public void setReceipt(Receipt receipt) {
        mReceipt = receipt;
    }

    public static ReceiptDialogFragment newInstance(Receipt receipt, Menu menu) {
        ReceiptDialogFragment receiptDialogFragment = new ReceiptDialogFragment();
        receiptDialogFragment.setReceipt(receipt);
        receiptDialogFragment.setMenu(menu);
        return receiptDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    }

    @Override
    public void onClick(View v) {

    }
}
