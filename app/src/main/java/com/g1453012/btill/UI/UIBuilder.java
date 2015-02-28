package com.g1453012.btill.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.g1453012.btill.BTillController;
import com.g1453012.btill.Bluetooth.ConnectThread;
import com.g1453012.btill.R;
import com.g1453012.btill.Shared.GBP;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.MenuItem;

import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.protocols.payments.PaymentProtocolException;

import java.io.IOException;
import java.util.concurrent.Future;

public class UIBuilder {

    private static final String TAG = "UIBuilder";
    private Context context = null;

    public UIBuilder(Context context) {
        this.context = context;
    };

    private AlertDialog paymentRequestDialog(final Protos.PaymentRequest request, final Wallet wallet) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Test Payment").setMessage("This is a Test Payment")
                .setPositiveButton("Sign", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Protos.Payment payment = null;
                        try {
                            payment = BTillController.transactionSigner(request, wallet);
                            Log.d(TAG, "Transaction Signed");
                        } catch (PaymentProtocolException e) {
                            //TODO error
                        }


                        if (BTillController.sendPayment(payment)) {
                            successfulPaymentDialog().show();
                        } else {
                            unsuccessfulPaymentDialog().show();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        Log.d(TAG, "Builder returned");
        return builder.create();
    }

    public AlertDialog successfulPaymentDialog() {
        AlertDialog.Builder resultBuilder = new AlertDialog.Builder(context);
        resultBuilder.setTitle("Success!").setMessage("Payment Successful");
        return resultBuilder.create();
    }

    public AlertDialog unsuccessfulPaymentDialog() {
        AlertDialog.Builder resultBuilder = new AlertDialog.Builder(context);
        resultBuilder.setTitle("Uh Oh!").setMessage("Payment Unsuccessful");
        return resultBuilder.create();
    }

    public static Dialog balanceDialog(Context context, Wallet wallet) {

        final Dialog mBalanceDialog = new Dialog(context);
        mBalanceDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mBalanceDialog.setContentView(R.layout.custom_balance_dialog);

        TextView mBalanceTotal = (TextView) mBalanceDialog.findViewById(R.id.balanceDialogBalance);
        mBalanceTotal.setText(wallet.getBalance(Wallet.BalanceType.ESTIMATED).toFriendlyString());

        ImageView mBalanceQR = (ImageView) mBalanceDialog.findViewById(R.id.balanceDialogQR);
        Bitmap mBitmap = BTillController.generateQR(wallet);
        mBalanceQR.setImageBitmap(mBitmap);
        mBalanceQR.setVisibility(View.VISIBLE);

        TextView mBalanceAddress = (TextView) mBalanceDialog.findViewById(R.id.balanceDialogAddress);
        mBalanceAddress.setText(wallet.currentReceiveAddress().toString());

        Button mBalanceOKButton = (Button) mBalanceDialog.findViewById(R.id.balanceDialogButton);
        mBalanceOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBalanceDialog.dismiss();
            }
        });

        return mBalanceDialog;
    }

    private Dialog launchOrderDialog(Context context, Menu menu, Fragment callback) {

        final Menu nonZeroMenu = removeNonZero(menu);

        final Dialog mOrderDialog = new Dialog(context);
        mOrderDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mOrderDialog.setContentView(R.layout.custom_order_dialog);

        ListView mOrderListView = (ListView)mOrderDialog.findViewById(R.id.dialogListView);
        mOrderListView.setAdapter(new OrderDialogAdapter(context, nonZeroMenu));

        TextView mOrderTotal = (TextView)mOrderDialog.findViewById(R.id.dialogAmountText);
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
                mOrderDialog.getOwnerActivity().
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

    private Dialog loadingDialog(Context context, final Menu nonZeroMenu) {

        final Dialog mLoadingDialog = new Dialog(context);
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
                    ConnectThread mConnectThread = new ConnectThread(BluetoothAdapter.getDefaultAdapter());
                    Future<Boolean> connectionFuture = mConnectThread.runFuture();
                    if(connectionFuture.get()) {
                        BTillController.setBluetoothSocket(mConnectThread.getSocket());
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

    private static Menu removeNonZero(Menu menu)
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

