package com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.g1453012.btill.BTillController;
import com.g1453012.btill.Bluetooth.ConnectThread;
import com.g1453012.btill.R;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.MenuItem;

import org.bitcoin.protocols.payments.Protos;

import java.util.concurrent.Future;


public class LoadingDialogFragment extends DialogFragment{

    private static final String TAG = "LoadingDialog";

    public Menu getMenu() {
        return mMenu;
    }

    public void setMenu(Menu mMenu) {
        this.mMenu = mMenu;
    }

    private Menu mMenu;

    public static LoadingDialogFragment newInstance(Menu menu) {
        LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();
        loadingDialogFragment.setMenu(menu);
        return loadingDialogFragment;
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

    @Override
    public void onStart() {
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BTillController mBTillController = new BTillController();
                    Protos.PaymentRequest request = null;
                    ConnectThread mConnectThread = new ConnectThread(mBTillController.getBluetoothAdapter());
                    Future<Boolean> connectionFuture = mConnectThread.runFuture();
                    if(connectionFuture.get()) {
                        mBTillController.setBluetoothSocket(mConnectThread.getSocket());
                        mBTillController.sendOrders(removeNonZero(mMenu));
                        Log.d(TAG, "Sent orders");
                        request = mBTillController.getPaymentRequest();
                        Log.d(TAG, "Got payment request");
                    }
                    final Protos.PaymentRequest receivedRequest = request;
                    mConnectThread.getSocket().close();
                    if (receivedRequest!=null)
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                    else
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                } catch (Exception e) {

                }

            }
        }).start();*/
    }

    //TODO Not sure about this class - needs to return when connectionFuture.get(). Hopefully using the onStart method to start the thread will work


}
