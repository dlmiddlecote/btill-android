package com.g1453012.btill;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import com.g1453012.btill.Shared.Receipt;

import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.protocols.payments.PaymentProtocolException;
import org.bitcoinj.protocols.payments.PaymentSession;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MenuFragment extends Fragment {

    private static final String TAG = "MenuFragment";

    private Activity mParentActivity;
    private BTillController mBTillController;

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
        mBalanceTotal.setText(mBTillController.getWallet().getBalance(Wallet.BalanceType.AVAILABLE).toFriendlyString());

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
        nonZeroMenu.setOrder_id(1);


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

    private void loadingDialog(Menu menu) {
        loadingDialogFull(menu, null, null, 0);
    }

    private void loadingDialog(Menu menu, Protos.PaymentRequest request, Coin amount) {
        loadingDialogFull(menu, request, amount, 1);
    }

    private void loadingDialogFull(@Nullable Menu nonZeroMenu, @Nullable Protos.PaymentRequest request, @Nullable Coin amount, int loadingCase) {

        final Dialog mLoadingDialog = new Dialog(getActivity());
        mLoadingDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mLoadingDialog.setContentView(R.layout.custom_loading_dialog);

        ProgressBar mProgressBar = (ProgressBar) mLoadingDialog.findViewById(R.id.loadingProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.show();

        switch (loadingCase) {
            case 0: sendOrdersThread(mLoadingDialog, nonZeroMenu).start();
                break;
            case 1: sendPaymentThread(mLoadingDialog, request, nonZeroMenu, amount).start();
                break;
        }


    }

    private Thread sendOrdersThread(final Dialog mLoadingDialog, final Menu nonZeroMenu) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
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
                    mLoadingDialog.dismiss();
                    Log.d(TAG, "Loading Dialog dismissed");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mBTillController.getBluetoothSocket().close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            launchPaymentRequestDialog(receivedRequest, nonZeroMenu);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error when connecting to server");
                }

            }
        });
    }

    private Thread sendPaymentThread(final Dialog mLoadingDialog, final Protos.PaymentRequest request, final Menu menu, final Coin amount) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Protos.Payment payment = null;
                try {
                    payment = mBTillController.transactionSigner(request);
                    Log.d(TAG, "Transaction Signed");
                } catch (PaymentProtocolException e) {
                    //TODO error
                }
                ConnectThread mConnectThread = new ConnectThread(mBTillController.getBluetoothAdapter());
                Future<Boolean> connectionFuture = mConnectThread.runFuture();
                try {
                    if (connectionFuture.get()) {
                        mBTillController.setBluetoothSocket(mConnectThread.getSocket());
                        if (mBTillController.sendPayment(payment)) {
                            Future<Receipt> receiptFuture = mBTillController.getReceipt();
                            final Receipt receipt = receiptFuture.get();

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mLoadingDialog.dismiss();
                                    launchReceiptDialog(menu, receipt);
                                }
                            });

                        } else {

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mLoadingDialog.dismiss();
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

                                    builder1.setTitle("Uh Oh!").setMessage("Payment Unsuccessful");

                                    builder1.create().show();
                                }
                            });
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    // TODO -- this!
    private void launchPaymentRequestDialog(final Protos.PaymentRequest request, final Menu menu) {

        final Dialog mPaymentDialog = new Dialog(getActivity());
        mPaymentDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mPaymentDialog.setContentView(R.layout.custom_payment_request_dialog);

        PaymentSession mSession = null;
        try {
            mSession = new PaymentSession(request, false);
        } catch (PaymentProtocolException e) {
            Log.e(TAG, "Error creating Payment Session");
        }

        TextView mOrderID = (TextView) mPaymentDialog.findViewById(R.id.paymentDialogOrderID);
        TextView mGBPAmount = (TextView) mPaymentDialog.findViewById(R.id.paymentDialogPriceAmount);
        TextView mBitcoinAmount = (TextView) mPaymentDialog.findViewById(R.id.paymentDialogBitcoinAmount);
        TextView mMessage = (TextView) mPaymentDialog.findViewById(R.id.paymentDialogMemo);

        if (mSession != null) {
            mOrderID.setText("" + menu.getOrder_id());

            GBP mTotal = new GBP(0);
            for (MenuItem item: menu)
            {
                mTotal = mTotal.plus(item.getPrice().times(item.getQuantity()));
            }
            mGBPAmount.setText(mTotal.toString());

            mBitcoinAmount.setText(mSession.getValue().toFriendlyString());

            mMessage.setText(mSession.getMemo() + "\nfrom " + mSession.getPaymentUrl());
        }
        else {
            mOrderID.setVisibility(View.GONE);
            mGBPAmount.setVisibility(View.GONE);
            mBitcoinAmount.setVisibility(View.GONE);
            mMessage.setVisibility(View.GONE);

            TextView mErrorTitle = (TextView) mPaymentDialog.findViewById(R.id.paymentDialogError);
            mErrorTitle.setVisibility(View.VISIBLE);

            TextView mOrderIDTitle = (TextView) mPaymentDialog.findViewById(R.id.paymentDialogIDTitle);
            TextView mGBPAmountTitle = (TextView) mPaymentDialog.findViewById(R.id.paymentDialogPriceTitle);
            TextView mBitcoinAmountTitle = (TextView) mPaymentDialog.findViewById(R.id.paymentDialogBitcoinTitle);
            TextView mMessageTitle = (TextView) mPaymentDialog.findViewById(R.id.paymentDialogMemoTitle);

            mOrderIDTitle.setVisibility(View.GONE);
            mGBPAmountTitle.setVisibility(View.GONE);
            mBitcoinAmountTitle.setVisibility(View.GONE);
            mMessageTitle.setVisibility(View.GONE);
        }

        final PaymentSession mNewSession = mSession;

        Button mSignButton = (Button) mPaymentDialog.findViewById(R.id.paymentDialogSignButton);
        mSignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPaymentDialog.dismiss();
                loadingDialog(menu, request, mNewSession.getValue());
            }
        });

        Button mCancelButton = (Button) mPaymentDialog.findViewById(R.id.paymentDialogCancelButton);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPaymentDialog.dismiss();
            }
        });

        mPaymentDialog.setCanceledOnTouchOutside(false);
        mPaymentDialog.show();
        Log.d(TAG, "Payment shown");

    }

    private void launchReceiptDialog(Menu menu, Receipt receipt) {
        final Dialog mReceiptDialog = new Dialog(getActivity());
        mReceiptDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mReceiptDialog.setContentView(R.layout.custom_receipt_dialog);

        TextView mOrderID = (TextView) mReceiptDialog.findViewById(R.id.receiptDialogOrderID);
        TextView mGBPAmount = (TextView) mReceiptDialog.findViewById(R.id.receiptDialogPriceAmount);
        TextView mBitcoinAmount = (TextView) mReceiptDialog.findViewById(R.id.receiptDialogBitcoinAmount);
        ListView mListView = (ListView) mReceiptDialog.findViewById(R.id.receiptDialogList);
        mListView.setAdapter(new OrderDialogAdapter(getActivity(), menu));

        mOrderID.setText("" + menu.getOrder_id());

        /*GBP mTotal = new GBP(0);
        for (MenuItem item: menu)
        {
            mTotal = mTotal.plus(item.getPrice().times(item.getQuantity()));
        }*/
        if (receipt != null) {
            mGBPAmount.setText(receipt.getGbp().toString());

            mBitcoinAmount.setText(receipt.getBitcoins().toFriendlyString());
        }
        Button mOKButton = (Button) mReceiptDialog.findViewById(R.id.receiptDialogButton);
        mOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReceiptDialog.dismiss();
            }
        });
        mReceiptDialog.show();
        Log.d(TAG, "Receipt Dialog shown");

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