package com.g1453012.btill.UI.HomeScreenFragments.Order;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.g1453012.btill.BTillController;
import com.g1453012.btill.PersistentParameters;
import com.g1453012.btill.R;

import org.bitcoinj.core.Wallet;

/**
 * Created by dlmiddlecote on 10/03/15.
 */
public class ServerNotFoundFragment extends Fragment{

    private PersistentParameters params;

    public static ServerNotFoundFragment newInstance(PersistentParameters params) {
        ServerNotFoundFragment fragment = new ServerNotFoundFragment();
        fragment.params = params;
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.server_not_found_home, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView mBalanceTotal = (TextView) getActivity().findViewById(R.id.serverNotFoundBalanceAmount);
        mBalanceTotal.setText(params.getWallet().getBalance(Wallet.BalanceType.ESTIMATED).toFriendlyString());

        ImageView mBalanceQR = (ImageView) getActivity().findViewById(R.id.serverNotFoundQR);
        Bitmap mBitmap = BTillController.generateQR(params.getWallet());
        mBalanceQR.setImageBitmap(mBitmap);
        mBalanceQR.setVisibility(View.VISIBLE);

        TextView mWalletAddress = (TextView) getActivity().findViewById(R.id.serverNotFoundWalletAddress);
        mWalletAddress.setText(params.getWallet().currentReceiveAddress().toString());

        Button mServerNotFoundButton = (Button) getActivity().findViewById(R.id.serverNotFoundRetryButton);
        mServerNotFoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBluetoothEnabled();
                BluetoothAdapter.getDefaultAdapter().startDiscovery();
            }
        });

    }
}
