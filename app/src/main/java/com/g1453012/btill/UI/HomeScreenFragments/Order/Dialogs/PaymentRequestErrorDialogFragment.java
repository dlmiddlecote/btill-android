package com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.g1453012.btill.R;

/**
 * Created by dlmiddlecote on 15/03/15.
 */
public class PaymentRequestErrorDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "PaymentRequestErrorFragment";

    public static PaymentRequestErrorDialogFragment newInstance() {
        return new PaymentRequestErrorDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog mPaymentRequestErrorDialog = new Dialog(getActivity());
        mPaymentRequestErrorDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mPaymentRequestErrorDialog.setContentView(R.layout.custom_payment_request_error_dialog);

        Button resendButton = (Button) mPaymentRequestErrorDialog.findViewById(R.id.paymentRequestErrorDialogOKButton);
        resendButton.setOnClickListener(this);

        Button cancelButton = (Button) mPaymentRequestErrorDialog.findViewById(R.id.paymentRequestErrorDialogCancelButton);
        cancelButton.setOnClickListener(this);

        mPaymentRequestErrorDialog.setCanceledOnTouchOutside(false);

        return mPaymentRequestErrorDialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.paymentRequestErrorDialogOKButton:
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                dismiss();
                break;
            case R.id.paymentRequestErrorDialogCancelButton:
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                dismiss();
                break;
        }
    }
}
