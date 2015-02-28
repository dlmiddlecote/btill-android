package com.g1453012.btill.UI.HomeScreenFragments.Order;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.g1453012.btill.BTillController;
import com.g1453012.btill.Bluetooth.ConnectThread;
import com.g1453012.btill.R;
import com.g1453012.btill.Shared.GBP;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.MenuItem;
import com.g1453012.btill.UI.HomeScreenFragments.AppStartup;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs.BalanceDialogFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs.LoadingDialogFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs.OrderDialogFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs.PaymentRequestDialogFragment;
import com.g1453012.btill.UI.OrderDialogAdapter;
import com.g1453012.btill.UI.UIBuilder;

import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.protocols.payments.PaymentProtocolException;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class OrderFragment extends Fragment implements View.OnClickListener {

    static final int ORDER_DIALOG = 1;
    static final int LOADING_DIALOG = 2;
    static final int PAYMENT_REQUEST_DIALOG = 3;

    private Menu mMenu;
    private BTillController mBTillController;
    private Activity mParentActivity;
    private Handler mHandler;
    private UIBuilder mUIBuilder = new UIBuilder(getActivity());
    private static final String TAG = "OrderFragment";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mParentActivity = activity;
        AppStartup screen = (AppStartup) mParentActivity;
        mBTillController = screen.getBTillController();
        mHandler = new Handler();
        /*Future<Menu> menuFuture = mBTillController.getMenuFuture();
        try {
            mMenu = menuFuture.get();
            mMenu.sortCategories();
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
        }*/
        /*
        mMenu = new Menu();
        mMenu.add(new MenuItem("Burrito", new GBP(600), "Mains"));
        mMenu.add(new MenuItem("Nachos", new GBP(500), "Mains"));
        mMenu.add(new MenuItem("Quesadilla", new GBP(400), "Mains"));
        mMenu.add(new MenuItem("Tacos", new GBP(600), "Mains"));
        mMenu.add(new MenuItem("Guacamole", new GBP(200), "Sides"));
        mMenu.add(new MenuItem("Spicy Rice", new GBP(300), "Sides"));
        mMenu.add(new MenuItem("Horchata", new GBP(350), "Drinks"));
        mMenu.add(new MenuItem("Mojito", new GBP(700), "Drinks"));
        mMenu.sortCategories();*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.order_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Future<Menu> menuFuture = mBTillController.getMenuFuture();
        try {
            mMenu = menuFuture.get(10, TimeUnit.SECONDS);
        }
        catch (TimeoutException tEx) {
            tEx.printStackTrace();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        if (mMenu!=null) {
            mMenu.sortCategories();
            final ViewPager pager = (ViewPager)getActivity().findViewById(R.id.categoryPager);
            final OrderFragmentPagerAdapter adapter = new OrderFragmentPagerAdapter(getFragmentManager(), mMenu);
            pager.setAdapter(adapter);
            pager.setPageTransformer(true, new CubeOutTransformer());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancelButton:
                //clear quantities
                break;

            case R.id.balanceButton:
                DialogFragment balanceDialogFragment = BalanceDialogFragment.newInstance(mBTillController.getWallet());
                balanceDialogFragment.show(getFragmentManager(), "BALANCE_DIALOG");
                break;

            case R.id.nextButton:
                DialogFragment orderDialogFragment = OrderDialogFragment.newInstance(mMenu);
                orderDialogFragment.setTargetFragment(this, ORDER_DIALOG);
                orderDialogFragment.show(getFragmentManager().beginTransaction(), "ORDER_DIALOG");
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ORDER_DIALOG:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        DialogFragment dialogFragment = LoadingDialogFragment.newInstance(mMenu);
                        dialogFragment.setTargetFragment(this, LOADING_DIALOG);
                        dialogFragment.show(getFragmentManager().beginTransaction(), "LOADING_DIALOG");
                        break;
                    default:
                        break;
                }
                break;
            case LOADING_DIALOG:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        DialogFragment dialogFragment = PaymentRequestDialogFragment.newInstance(mBTillController.getPaymentRequest());
                        dialogFragment.setTargetFragment(this, PAYMENT_REQUEST_DIALOG);
                        dialogFragment.show(getFragmentManager().beginTransaction(), "PAYMENT_REQUEST_DIALOG");
                        break;
                    default:
                        //Inform that sending order failed
                        break;
                }
                break;
            case PAYMENT_REQUEST_DIALOG:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //Send payment
                        //Would be really cool to have the loading dialog take a Future<Obj> and return when the object is not null
                        //This would allow reuse of the loading dialog
                        //Show success/failure
                        break;
                    default:
                        //Cancel transaction
                        break;
                }
                break;
            default:
                break;
        }
    }



    // TODO -- this!
    /*
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
    */
}
