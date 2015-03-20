package com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.g1453012.btill.PersistentParameters;
import com.g1453012.btill.R;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.Receipt;
import com.g1453012.btill.UI.HomeScreenFragments.Order.OrderDialogAdapter;


/**
 * Created by dlmiddlecote on 01/03/15.
 */
public class ReceiptDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "ReceiptFragment";

    private Menu mMenu;
    private PersistentParameters params;
    private Receipt mReceipt;
    private int receiptStoreID;
    private int mOrderID;
    private String mOrderDate;
    private boolean mFromStore;

    public Menu getMenu() {
        return mMenu;
    }

    public void setMenu(Menu menu) {
        mMenu = menu;
    }

    public PersistentParameters getParams() {
        return params;
    }

    public void setParams(PersistentParameters params) {
        this.params = params;
    }

    public Receipt getReceipt() {
        return mReceipt;
    }

    public void setReceipt(Receipt receipt) {
        mReceipt = receipt;
    }

    public int getReceiptStoreID() {
        return receiptStoreID;
    }

    public void setReceiptStoreID(int receiptStoreID) {
        this.receiptStoreID = receiptStoreID;
    }

    public void setOrderID(int orderID) {
        mOrderID = orderID;
    }

    public void setOrderDate(String orderDate) {
        mOrderDate = orderDate;
    }

    public void setFromStore(boolean fromStore) {
        mFromStore = fromStore;
    }

    public static ReceiptDialogFragment newInstance(PersistentParameters params, int ID, boolean fromStore) {
        ReceiptDialogFragment receiptDialogFragment = new ReceiptDialogFragment();
        receiptDialogFragment.setParams(params);
        //receiptDialogFragment.setReceipt(params.getReceiptStore().getReceipt(ID));
        receiptDialogFragment.setReceipt(params.getNewReceiptStore().getReceipt(ID));
        //receiptDialogFragment.setMenu(params.getReceiptStore().getMenu(ID));
        receiptDialogFragment.setMenu(params.getNewReceiptStore().getMenu(ID));
        receiptDialogFragment.setReceiptStoreID(ID);
        //receiptDialogFragment.setOrderID(params.getReceiptStore().getReceipt(ID).getOrderId());
        receiptDialogFragment.setOrderID(params.getNewReceiptStore().getReceipt(ID).getOrderId());
        //receiptDialogFragment.setOrderDate(params.getReceiptStore().getReceipt(ID).getDateAsString());
        receiptDialogFragment.setOrderDate(params.getNewReceiptStore().getReceipt(ID).getDateAsString());
        receiptDialogFragment.setFromStore(fromStore);
        return receiptDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog mReceiptDialog = new Dialog(getActivity());
        mReceiptDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mReceiptDialog.setContentView(R.layout.custom_receipt_dialog);

        TextView mOrderIDText = (TextView) mReceiptDialog.findViewById(R.id.receiptDialogOrderID);
        TextView mGBPAmount = (TextView) mReceiptDialog.findViewById(R.id.receiptDialogPriceAmount);
        TextView mBitcoinAmount = (TextView) mReceiptDialog.findViewById(R.id.receiptDialogBitcoinAmount);
        ListView mListView = (ListView) mReceiptDialog.findViewById(R.id.receiptDialogList);
        mListView.setAdapter(new OrderDialogAdapter(getActivity(), mMenu));

        mOrderIDText.setText(mOrderDate + " - " + mOrderID);

        if (mReceipt != null) {
            mGBPAmount.setText(mReceipt.getGbp().toString());

            mBitcoinAmount.setText(mReceipt.getBitcoins().toFriendlyString());
        }
        Button mOKButton = (Button) mReceiptDialog.findViewById(R.id.receiptDialogButton);
        mOKButton.setOnClickListener(this);

        mReceiptDialog.setCanceledOnTouchOutside(false);

        if (mFromStore) {
            Button mDeleteButton = (Button) mReceiptDialog.findViewById(R.id.receiptDialogDeleteButton);
            mDeleteButton.setOnClickListener(this);
            mDeleteButton.setVisibility(View.VISIBLE);
        }

        return mReceiptDialog;
    }

    @Override
    public void onClick(View v) {
        //if (getTargetFragment()!=null) {
            switch(v.getId()) {
                case R.id.receiptDialogButton:
                    if (getTargetFragment() != null) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                    }
                    dismiss();
                    break;
                case R.id.receiptDialogDeleteButton:
                    //params.getReceiptStore().remove(receiptStoreID);
                    params.getNewReceiptStore().remove(receiptStoreID);
                    params.getReceiptFragment().refreshAdapter();
                    dismiss();
                    break;
            }
        //}

    }
}
