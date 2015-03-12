package com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.g1453012.btill.R;

/**
 * Created by dlmiddlecote on 12/03/15.
 */
public class InsufficientFundsDialogFragment extends DialogFragment {

    private static final String TAG = "InsufficientFundsDialog";

    public static InsufficientFundsDialogFragment newInstance() {
        return new InsufficientFundsDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog mFundsDialog = new Dialog(getActivity());
        mFundsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mFundsDialog.setContentView(R.layout.custom_insufficient_funds_dialog);

        Button closeButton = (Button) mFundsDialog.findViewById(R.id.insufficientFundsButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFundsDialog.dismiss();
            }
        });

        mFundsDialog.setCanceledOnTouchOutside(false);

        return mFundsDialog;
    }
}
