package com.g1453012.btill;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.g1453012.btill.Shared.GBP;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.MenuItem;

import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.protocols.payments.PaymentProtocolException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MenuFragment extends Fragment {

    private static final String TAG = "MenuFragment";

    private Activity mParentActivity;
    private BTillController mBTillController;

    // TODO -- make sure we need this
    Handler mHandler;

    public MenuFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mParentActivity = activity;
        HomeScreen screen = (HomeScreen) mParentActivity;
        mBTillController = screen.getBTillController();
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menu_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ListView listView = (ListView) getActivity().findViewById(R.id.listView);

        //Menu mMenu = mBTillController.getMenu();
        Future<Menu> menuFuture = mBTillController.getMenuFuture();
        Menu mMenu = null;
        try {
            mMenu = menuFuture.get();
        } catch (InterruptedException e) {
            Log.e(TAG, "Getting the Menu was interrupted");
        } catch (ExecutionException e) {
            Log.e(TAG, "Getting the Menu had an Execution Exception");
        }
        Log.d(TAG, "Gets Menu");
        try {
            mBTillController.getBluetoothSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listView.setAdapter(new MenuAdapter(getActivity(), mMenu));

        Button nextButton = (Button) getActivity().findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuAdapter adapter = (MenuAdapter) listView.getAdapter();
                // Launch Order dialog
                launchOrderDialog(adapter.getMenu());
            }
        });

        Button balanceButton = (Button) getActivity().findViewById(R.id.balanceButton);
        balanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBalanceDialog();
            }
        });

    }

    private void launchBalanceDialog() {

        final Dialog mBalanceDialog = new Dialog(getActivity());
        mBalanceDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mBalanceDialog.setContentView(R.layout.custom_balance_dialog);

        TextView mBalanceTotal = (TextView) mBalanceDialog.findViewById(R.id.balanceDialogBalance);
        mBalanceTotal.setText(mBTillController.getWallet().getBalance(Wallet.BalanceType.ESTIMATED).toFriendlyString());

        ImageView mBalanceQR = (ImageView) mBalanceDialog.findViewById(R.id.balanceDialogQR);
        Bitmap mBitmap = mBTillController.generateQR();
        mBalanceQR.setImageBitmap(mBitmap);
        mBalanceQR.setVisibility(View.VISIBLE);

        TextView mBalanceAddress = (TextView) mBalanceDialog.findViewById(R.id.balanceDialogAddress);
        mBalanceAddress.setText(mBTillController.getWallet().currentReceiveAddress().toString());

        Button mBalanceOKButton = (Button) mBalanceDialog.findViewById(R.id.balanceDialogButton);
        mBalanceOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBalanceDialog.dismiss();
            }
        });

        mBalanceDialog.show();

    }



    private void launchOrderDialog(Menu menu) {

        final Menu nonZeroMenu = removeNonZero(menu);


        final Dialog mOrderDialog = new Dialog(getActivity());
        mOrderDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mOrderDialog.setContentView(R.layout.custom_order_dialog);

        ListView mOrderListView = (ListView)mOrderDialog.findViewById(R.id.dialogListView);
        mOrderListView.setAdapter(new OrderDialogAdapter(getActivity(), nonZeroMenu));

        TextView mOrderTotal = (TextView)mOrderDialog.findViewById(R.id.dialogAmountText);
        //double mTotal = 0;
        GBP mTotal = new GBP(0);
        for (MenuItem item: nonZeroMenu)
        {
            mTotal = mTotal.plus(item.getPrice().times(item.getQuantity()));
        }
        mOrderTotal.setText(mTotal.toString());

        Button mConfirmButton = (Button)mOrderDialog.findViewById(R.id.dialogConfirmButton);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog(nonZeroMenu);
                mOrderDialog.dismiss();
            }
        });

        Button mCancelButton = (Button)mOrderDialog.findViewById(R.id.dialogCancelButton);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrderDialog.dismiss();
            }
        });

        mOrderDialog.show();

    }

    private void loadingDialog(final Menu nonZeroMenu) {

        final Dialog mLoadingDialog = new Dialog(getActivity());
        mLoadingDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mLoadingDialog.setContentView(R.layout.custom_loading_dialog);

        ProgressBar mProgressBar = (ProgressBar) mLoadingDialog.findViewById(R.id.loadingProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.show();

        //mBTillController.sendOrders(nonZeroMenu);
        /* TODO this will currently dismiss the dialog if the order doesn't send
         * Update this!
         */

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Thread.sleep(1000);
                    Protos.PaymentRequest request = null;
                    ConnectThread mConnectThread = new ConnectThread(mBTillController.getBluetoothAdapter());
                    Future<Boolean> connectionFuture = mConnectThread.runFuture();
                    if(connectionFuture.get()) {
                        mBTillController.setBluetoothSocket(mConnectThread.getSocket());
                        mBTillController.sendOrders(nonZeroMenu);
                        Log.d(TAG, "Sent orders");
                        request = mBTillController.getPaymentRequest();
                        Log.d(TAG, "Got payment request");
                    }
                    final Protos.PaymentRequest receivedRequest = request;
                    if (true) {
                        mLoadingDialog.dismiss();
                        Log.d(TAG, "Loading Dialog dismissed");
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                launchPaymentRequestDialog(receivedRequest);
                                try {
                                    mBTillController.getBluetoothSocket().close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                    }
                } catch (Exception e) {

                }

            }
        }).start();
    }



    // TODO -- this!
    private void launchPaymentRequestDialog(final Protos.PaymentRequest request) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Test Payment").setMessage("This is a Test Payment")
                .setPositiveButton("Sign", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Protos.Payment payment = null;
                        try {
                            payment = mBTillController.transactionSigner(request);
                            Log.d(TAG, "Transaction Signed");
                        } catch (PaymentProtocolException e) {
                            //TODO error
                        }


                        if (mBTillController.sendPayment(payment)) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

                            builder1.setTitle("Success!").setMessage("Payment Successful");

                            builder1.create().show();
                        } else {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

                            builder1.setTitle("Uh Oh!").setMessage("Payment Unsuccessful");

                            builder1.create().show();
                        }

                        //mBTillController.getMenu().resetQuantities();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Log.d(TAG, "Payment shown");
    }

    public Menu removeNonZero(Menu menu)
    {
        Menu retMenu = new Menu();
        for (MenuItem item: menu)
        {
            if (item.getQuantity()!=0)
                retMenu.add(item);
        }

        return retMenu;
    }
}