package com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs;

import android.app.Activity;
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

import org.bitcoinj.core.Wallet;

public class BalanceDialogFragment extends DialogFragment implements View.OnClickListener{

    public Wallet getWallet() {
        return mWallet;
    }

    public void setWallet(Wallet wallet) {
        this.mWallet = wallet;
    }

    private Wallet mWallet;

    public static BalanceDialogFragment newInstance(Wallet wallet) {
        BalanceDialogFragment balanceDialogFragment = new BalanceDialogFragment();
        balanceDialogFragment.setWallet(wallet);
        return balanceDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog mBalanceDialog = new Dialog(getActivity());
        mBalanceDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mBalanceDialog.setContentView(R.layout.custom_balance_dialog);

        TextView mBalanceTotal = (TextView) mBalanceDialog.findViewById(R.id.balanceDialogBalance);
        mBalanceTotal.setText(mWallet.getBalance(Wallet.BalanceType.ESTIMATED).toFriendlyString());

        ImageView mBalanceQR = (ImageView) mBalanceDialog.findViewById(R.id.balanceDialogQR);
        Bitmap mBitmap = BTillController.generateQR(mWallet);
        mBalanceQR.setImageBitmap(mBitmap);
        mBalanceQR.setVisibility(View.VISIBLE);

        TextView mBalanceAddress = (TextView) mBalanceDialog.findViewById(R.id.balanceDialogAddress);
        mBalanceAddress.setText(mWallet.currentReceiveAddress().toString());
        return mBalanceDialog;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.balanceDialogButton:
                dismiss();
                break;
        }
    }
}
