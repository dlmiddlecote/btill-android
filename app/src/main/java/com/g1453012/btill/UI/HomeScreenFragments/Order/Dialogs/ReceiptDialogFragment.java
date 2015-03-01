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

import com.g1453012.btill.R;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.Receipt;
import com.g1453012.btill.UI.OrderDialogAdapter;


/**
 * Created by dlmiddlecote on 01/03/15.
 */
public class ReceiptDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "ReceiptFragment";

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

        final Dialog mReceiptDialog = new Dialog(getActivity());
        mReceiptDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mReceiptDialog.setContentView(R.layout.custom_receipt_dialog);

        TextView mOrderID = (TextView) mReceiptDialog.findViewById(R.id.receiptDialogOrderID);
        TextView mGBPAmount = (TextView) mReceiptDialog.findViewById(R.id.receiptDialogPriceAmount);
        TextView mBitcoinAmount = (TextView) mReceiptDialog.findViewById(R.id.receiptDialogBitcoinAmount);
        ListView mListView = (ListView) mReceiptDialog.findViewById(R.id.receiptDialogList);
        mListView.setAdapter(new OrderDialogAdapter(getActivity(), mMenu));

        mOrderID.setText("" + mMenu.getOrderId());

        if (mReceipt != null) {
            mGBPAmount.setText(mReceipt.getGbp().toString());

            mBitcoinAmount.setText(mReceipt.getBitcoins().toFriendlyString());
        }
        Button mOKButton = (Button) mReceiptDialog.findViewById(R.id.receiptDialogButton);
        mOKButton.setOnClickListener(this);

        mReceiptDialog.setCanceledOnTouchOutside(false);

        return mReceiptDialog;
    }

    @Override
    public void onClick(View v) {
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
        dismiss();
    }
}
