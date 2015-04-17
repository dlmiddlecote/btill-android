package com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.g1453012.btill.PersistentParameters;
import com.g1453012.btill.R;
import com.g1453012.btill.Shared.OrderConfirmation;

/**
 * Created by dlmiddlecote on 21/03/15.
 */
public class OrderConfirmationDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "OrderConfirmationFragment";

    private PersistentParameters params;
    private OrderConfirmation mOrderConfirmation;
    private int receiptID;

    public static OrderConfirmationDialogFragment newInstance(PersistentParameters params, OrderConfirmation orderConfirmation, int receiptID) {
        OrderConfirmationDialogFragment orderConfirmationDialogFragment = new OrderConfirmationDialogFragment();
        orderConfirmationDialogFragment.setParams(params);
        orderConfirmationDialogFragment.setOrderConfirmation(orderConfirmation);
        orderConfirmationDialogFragment.setReceiptID(receiptID);
        return orderConfirmationDialogFragment;
    }

    public void setParams(PersistentParameters params) {
        this.params = params;
    }

    public void setOrderConfirmation(OrderConfirmation orderConfirmation) {
        mOrderConfirmation = orderConfirmation;
    }

    public void setReceiptID(int receiptID) {
        this.receiptID = receiptID;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog orderConfirmationDialog = new Dialog(getActivity());
        orderConfirmationDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        orderConfirmationDialog.setContentView(R.layout.custom_order_confirmation_dialog);

        TextView mTable = (TextView) orderConfirmationDialog.findViewById(R.id.orderConfirmationTableNumber);
        TextView mGBPAmount = (TextView) orderConfirmationDialog.findViewById(R.id.orderConfirmationGBPAmount);
        TextView mBitcoinAmount = (TextView) orderConfirmationDialog.findViewById(R.id.orderConfirmationBitcoinAmount);

        if (mOrderConfirmation.getReceipt() != null) {
            mGBPAmount.setText(mOrderConfirmation.getReceipt().getGbp().toString());
            mBitcoinAmount.setText(mOrderConfirmation.getReceipt().getBitcoins().toFriendlyString());
        }

        if (mOrderConfirmation.getTableNumber() > 0) {
            mTable.setText(mOrderConfirmation.getTableNumber().toString());
        }

        Button mConfirmButton = (Button) orderConfirmationDialog.findViewById(R.id.orderConfirmationOKButton);
        mConfirmButton.setOnClickListener(this);

        Button mReceiptButton = (Button) orderConfirmationDialog.findViewById(R.id.orderConfirmationReceiptButton);
        mReceiptButton.setOnClickListener(this);

        return orderConfirmationDialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.orderConfirmationOKButton:
                if (getTargetFragment() != null) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                }
                dismiss();
                break;
            case R.id.orderConfirmationReceiptButton:
                if (getTargetFragment() != null) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), receiptID, getActivity().getIntent());
                }
                break;
        }

    }
}
