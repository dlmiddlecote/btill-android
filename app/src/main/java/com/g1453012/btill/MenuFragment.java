package com.g1453012.btill;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.protocols.payments.PaymentProtocolException;

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

        Menu mMenu = mBTillController.getMenu();

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
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode("bitcoin:" + mBTillController.getWallet().currentReceiveAddress().toString(), BarcodeFormat.QR_CODE, 512, 512);
            Bitmap mBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565);
            for (int x = 0; x < 512; x++) {
                for (int y = 0; y < 512; y++) {
                    if (bitMatrix.get(x, y))
                        mBitmap.setPixel(x, y, Color.BLACK);
                    else
                        mBitmap.setPixel(x, y, Color.WHITE);
                }
            }
            mBalanceQR.setImageBitmap(mBitmap);
            mBalanceQR.setVisibility(View.VISIBLE);
        } catch (WriterException e) {
            Log.e(TAG, "QR Error");
        }

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
                //mBTillController.sendOrders(nonZeroMenu);
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

    private void loadingDialog(Menu nonZeroMenu) {

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
                    Thread.sleep(1000);
                    if (true) {
                        mLoadingDialog.dismiss();
                        Log.d(TAG, "Loading Dialog dismissed");
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                launchPaymentRequestDialog();
                            }
                        });


                    }
                } catch (Exception e) {

                }

            }
        }).start();
    }


    // TODO -- this!
    private void launchPaymentRequestDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Test Payment").setMessage("This is a Test Payment")
                .setPositiveButton("Sign", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            /*AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

                            builder1.setTitle("Success!").setMessage("Payment Successful");

                            builder1.create().show();*/

                        Protos.PaymentRequest request = mBTillController.getRequest("bitcoin:mhKuHFtbzF5khjNSDDbM8z6x18avzt4EgY?amount=0.001&r=http://www.b-till.com");
                        try {
                            Protos.Payment payment = mBTillController.transactionSigner(request);
                            Log.d(TAG, "Transaction Signed");
                        } catch (PaymentProtocolException e) {
                            //TODO error
                        }

                        ConnectedThread mConnectedThread = new ConnectedThread(mBTillController.getBluetoothSocket());
                        mConnectedThread.start();

                        /*if (mConnectedThread.write(payment)) {

                        } else {

                        }*/

                        //mBTillController.getMenu().resetQuantities();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();

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