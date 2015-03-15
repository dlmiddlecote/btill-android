package com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.g1453012.btill.R;
import com.g1453012.btill.Shared.Bill;
import com.g1453012.btill.Shared.Menu;

import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.protocols.payments.PaymentProtocolException;
import org.bitcoinj.protocols.payments.PaymentSession;

/**
 * Created by Andy on 28/02/2015.
 */
public class PaymentRequestDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "PaymentDialogFragment";

    private Protos.PaymentRequest mRequest;
    private Menu mMenu;
    private Bill mBill;

    public Protos.PaymentRequest getRequest() {
        return mRequest;
    }

    public void setRequest(Protos.PaymentRequest mRequest) {
        this.mRequest = mRequest;
    }

    public void setBill(Bill bill) {
        mBill = bill;
    }

    public Menu getMenu() {
        return mMenu;
    }

    public void setMenu(Menu menu) {
        mMenu = menu;
    }

    public static PaymentRequestDialogFragment newInstance(Bill bill, Menu menu) {
            PaymentRequestDialogFragment paymentRequestDialogFragment = new PaymentRequestDialogFragment();
            paymentRequestDialogFragment.setBill(bill);
            paymentRequestDialogFragment.setRequest(bill.getRequest());
            paymentRequestDialogFragment.setMenu(menu);
            return paymentRequestDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog mPaymentDialog = new Dialog(getActivity());
        mPaymentDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mPaymentDialog.setContentView(R.layout.custom_payment_request_dialog);

        PaymentSession mSession = null;
        try {
            mSession = new PaymentSession(mRequest, false);
        } catch (PaymentProtocolException e) {
            Log.e(TAG, "Error creating Payment Session");
        }

        TextView mOrderID = (TextView) mPaymentDialog.findViewById(R.id.paymentDialogOrderID);
        mOrderID.setText(mBill.getDateAsString() + " - " + mBill.getOrderId());

        TextView mGBPAmount = (TextView) mPaymentDialog.findViewById(R.id.paymentDialogPriceAmount);
        mGBPAmount.setText(mBill.getGbpAmount().toString());

        TextView mBitcoinAmount = (TextView) mPaymentDialog.findViewById(R.id.paymentDialogBitcoinAmount);
        mBitcoinAmount.setText("Error");

        TextView mMessage = (TextView) mPaymentDialog.findViewById(R.id.paymentDialogMemo);
        mMessage.setText("Error Processing Payment");

        if (mSession != null) {

            mBitcoinAmount.setText(mSession.getValue().toFriendlyString());

            mMessage.setText(mSession.getMemo() + "\nfrom " + mSession.getPaymentUrl());
        }

        Button mSignButton = (Button) mPaymentDialog.findViewById(R.id.paymentDialogSignButton);
        mSignButton.setOnClickListener(this);
        Button mCancelButton = (Button) mPaymentDialog.findViewById(R.id.paymentDialogCancelButton);
        mCancelButton.setOnClickListener(this);

        mPaymentDialog.setCanceledOnTouchOutside(false);

        return mPaymentDialog;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.paymentDialogSignButton:
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                dismiss();
                break;
            case R.id.paymentDialogCancelButton:
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                dismiss();
                break;
            default:
                break;
        }
    }
}
