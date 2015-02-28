package com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.g1453012.btill.BTillController;
import com.g1453012.btill.R;

import org.bitcoin.protocols.payments.Protos;

/**
 * Created by Andy on 28/02/2015.
 */
public class PaymentRequestDialogFragment extends DialogFragment implements View.OnClickListener{

    private Protos.PaymentRequest mRequest;

    public Protos.PaymentRequest getRequest() {
        return mRequest;
    }

    public void setRequest(Protos.PaymentRequest mRequest) {
        this.mRequest = mRequest;
    }

    public static PaymentRequestDialogFragment newInstance(Protos.PaymentRequest request) {
            PaymentRequestDialogFragment paymentRequestDialogFragment = new PaymentRequestDialogFragment();
            paymentRequestDialogFragment.setRequest(request);
            return paymentRequestDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Test Payment").setMessage("This is a Test Payment");
        AlertDialog dialog = builder.create();

        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case AlertDialog.BUTTON_POSITIVE:
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                dismiss();
                break;
            case AlertDialog.BUTTON_NEGATIVE:
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                dismiss();
                break;
            default:
                break;
        }
    }
}
