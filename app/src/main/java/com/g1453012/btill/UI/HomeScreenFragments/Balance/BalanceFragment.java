package com.g1453012.btill.UI.HomeScreenFragments.Balance;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.g1453012.btill.BTillController;
import com.g1453012.btill.PersistentParameters;
import com.g1453012.btill.R;

import org.bitcoinj.core.Wallet;

/**
 * Created by Andy on 16/03/2015.
 */
public class BalanceFragment extends Fragment {

    private final static String TAG = "BalanceFragment";

    private PersistentParameters params;
    private TextView mBalanceTotal;
    private ImageView mBalanceQR;
    private TextView mBalanceAddress;
    private TextView mBalanceTitle;
    private boolean balanceLoaded;
    private boolean needsUpdating;

    public static BalanceFragment newInstance(PersistentParameters params) {
        BalanceFragment balanceFragment = new BalanceFragment();
        balanceFragment.setParams(params);
        balanceFragment.setBalanceLoaded(false);
        balanceFragment.setNeedsUpdating(false);
        return balanceFragment;
    }

    public PersistentParameters getParams() {
        return params;
    }

    public void setParams(PersistentParameters params) {
        this.params = params;
    }

    public void setBalanceLoaded(boolean balanceLoaded) {
        this.balanceLoaded = balanceLoaded;
    }

    private void setNeedsUpdating(boolean needsUpdating) {
        this.needsUpdating = needsUpdating;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.balance_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBalanceTotal = (TextView) getActivity().findViewById(R.id.balanceFragmentBalance);
        mBalanceQR = (ImageView) getActivity().findViewById(R.id.balanceDialogQR);
        mBalanceAddress = (TextView) getActivity().findViewById(R.id.balanceFragmentAddress);

        if (params.getWallet() != null) {

            Log.d("Address:", params.getWallet().currentReceiveAddress().toString());
            mBalanceTotal.setText(params.getWallet().getBalance(Wallet.BalanceType.ESTIMATED).toFriendlyString());

            Bitmap mBitmap = BTillController.generateQR(params.getWallet());
            mBalanceQR.setImageBitmap(mBitmap);
            mBalanceQR.setVisibility(View.VISIBLE);

            mBalanceAddress.setText(params.getWallet().currentReceiveAddress().toString());

            balanceLoaded = true;
        } else {
            mBalanceTitle = (TextView) getActivity().findViewById(R.id.balanceFragmentTitle);
            mBalanceTitle.setText(R.string.walletError);

            mBalanceTotal.setVisibility(View.GONE);
            mBalanceQR.setVisibility(View.GONE);
            mBalanceAddress.setVisibility(View.GONE);
        }
    }

    public void needsUpdating() {
        needsUpdating = true;
    }

    public void refresh() {
        if (balanceLoaded && needsUpdating) {
            //getActivity().runOnUiThread(new Runnable() {
            //    @Override
            //    public void run() {
                    if (mBalanceTotal != null) {
                        if (params.getWallet() != null) {

                            mBalanceTotal.setText(params.getWallet().getBalance(Wallet.BalanceType.ESTIMATED).toFriendlyString());

                            Bitmap mBitmap = BTillController.generateQR(params.getWallet());
                            mBalanceQR.setImageBitmap(mBitmap);
                            mBalanceQR.setVisibility(View.VISIBLE);

                            mBalanceAddress.setText(params.getWallet().currentReceiveAddress().toString());
                            Log.d(TAG, "Refreshed Balance");
                        } else {
                            mBalanceTitle.setText(R.string.walletError);

                            mBalanceTotal.setVisibility(View.GONE);
                            mBalanceQR.setVisibility(View.GONE);
                            mBalanceAddress.setVisibility(View.GONE);
                        }
                    }
            needsUpdating = false;
            //    }
            //});
        }
        /*if (mBalanceTotal != null) {
            if (params.getWallet() != null) {

                mBalanceTotal.setText(params.getWallet().getBalance(Wallet.BalanceType.ESTIMATED).toFriendlyString());

                Bitmap mBitmap = BTillController.generateQR(params.getWallet());
                mBalanceQR.setImageBitmap(mBitmap);
                mBalanceQR.setVisibility(View.VISIBLE);

                mBalanceAddress.setText(params.getWallet().currentReceiveAddress().toString());
            } else {
                mBalanceTitle.setText(R.string.walletError);

                mBalanceTotal.setVisibility(View.GONE);
                mBalanceQR.setVisibility(View.GONE);
                mBalanceAddress.setVisibility(View.GONE);
            }
        }*/
    }
}
