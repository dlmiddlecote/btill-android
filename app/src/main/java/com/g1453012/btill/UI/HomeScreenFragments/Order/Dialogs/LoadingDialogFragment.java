package com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.g1453012.btill.R;

public class LoadingDialogFragment extends DialogFragment {

    private static final String TAG = "LoadingDialog";

    public static LoadingDialogFragment newInstance() {
        return new LoadingDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog mLoadingDialog = new Dialog(getActivity());
        mLoadingDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mLoadingDialog.setContentView(R.layout.custom_loading_dialog);

        ProgressBar mProgressBar = (ProgressBar) mLoadingDialog.findViewById(R.id.loadingProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        mLoadingDialog.setCanceledOnTouchOutside(false);
        return mLoadingDialog;
    }
}
