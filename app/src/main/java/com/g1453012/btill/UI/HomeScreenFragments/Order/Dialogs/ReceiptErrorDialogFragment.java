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
public class ReceiptErrorDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "ReceiptErrorFragment";

    public static ReceiptErrorDialogFragment newInstance() {
        return new ReceiptErrorDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog mReceiptErrorDialog = new Dialog(getActivity());
        mReceiptErrorDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mReceiptErrorDialog.setContentView(R.layout.custom_receipt_error_dialog);

        Button resendButton = (Button) mReceiptErrorDialog.findViewById(R.id.receiptErrorDialogOKButton);
        resendButton.setOnClickListener(this);

        Button cancelButton = (Button) mReceiptErrorDialog.findViewById(R.id.receiptErrorDialogCancelButton);
        cancelButton.setOnClickListener(this);

        mReceiptErrorDialog.setCanceledOnTouchOutside(false);

        return mReceiptErrorDialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.receiptErrorDialogOKButton:
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                dismiss();
                break;
            case R.id.receiptErrorDialogCancelButton:
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                dismiss();
                break;
        }
    }
}
