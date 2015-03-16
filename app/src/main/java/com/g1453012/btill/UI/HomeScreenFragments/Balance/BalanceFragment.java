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

    private PersistentParameters params;

    public PersistentParameters getParams() {
        return params;
    }

    public void setParams(PersistentParameters params) {
        this.params = params;
    }

    public static BalanceFragment newInstance(PersistentParameters params) {
        BalanceFragment balanceFragment = new BalanceFragment();
        balanceFragment.setParams(params);
        return balanceFragment;
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

        TextView mBalanceTotal = (TextView) getActivity().findViewById(R.id.balanceFragmentBalance);
        ImageView mBalanceQR = (ImageView) getActivity().findViewById(R.id.balanceDialogQR);
        TextView mBalanceAddress = (TextView) getActivity().findViewById(R.id.balanceFragmentAddress);

        if (params.getWallet() != null) {

            Log.d("Address:", params.getWallet().currentReceiveAddress().toString());
            mBalanceTotal.setText(params.getWallet().getBalance(Wallet.BalanceType.ESTIMATED).toFriendlyString());

            Bitmap mBitmap = BTillController.generateQR(params.getWallet());
            mBalanceQR.setImageBitmap(mBitmap);
            mBalanceQR.setVisibility(View.VISIBLE);

            mBalanceAddress.setText(params.getWallet().currentReceiveAddress().toString());
        }
        else {
            TextView mBalanceTitle = (TextView) getActivity().findViewById(R.id.balanceFragmentTitle);
            mBalanceTitle.setText(R.string.walletError);

            mBalanceTotal.setVisibility(View.GONE);
            mBalanceQR.setVisibility(View.GONE);
            mBalanceAddress.setVisibility(View.GONE);
        }
    }
}
