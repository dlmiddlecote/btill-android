package com.g1453012.btill.UI.HomeScreenFragments.Order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.g1453012.btill.BTillController;
import com.g1453012.btill.Bluetooth.ConnectThread;
import com.g1453012.btill.PersistentParameters;
import com.g1453012.btill.R;
import com.g1453012.btill.Shared.Bill;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.MenuItem;
import com.g1453012.btill.Shared.OrderConfirmation;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Category.CategoryFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs.InsufficientFundsDialogFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs.LoadingDialogFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs.OrderConfirmationDialogFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs.OrderDialogFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs.PaymentRequestDialogFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs.PaymentRequestErrorDialogFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs.ReceiptDialogFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs.ReceiptErrorDialogFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs.WelcomeDialogFragment;

import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.InsufficientMoneyException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class OrderFragment extends Fragment implements View.OnClickListener {

    static final int ORDER_DIALOG = 1;
    static final int PAYMENT_REQUEST_DIALOG = 2;
    static final int PAYMENT_REQUEST_ERROR_DIALOG = 3;
    static final int RECEIPT_DIALOG = 4;
    static final int INSUFFICIENT_FUNDS = 5;
    static final int RECEIPT_ERROR_DIALOG = 6;
    static final int ORDER_CONFIRMATION_DIALOG = 7;
    static final int WELCOME_DIALOG = 8;
    private static final String TAG = "OrderFragment";
    private static Handler mHandler = new Handler();
    private PersistentParameters params;
    private Menu mMenu;
    private OrderFragmentPagerAdapter mOrderFragmentPagerAdapter;
    private Activity mParentActivity;
    private Fragment mainFragment = this;

    public static OrderFragment newInstance(PersistentParameters parameters) {
        OrderFragment orderFragment = new OrderFragment();
        orderFragment.setParams(parameters);

        Future<Menu> menuFuture = BTillController.getMenuFuture(parameters.getSocket());
        try {
            orderFragment.setMenu(menuFuture.get(10, TimeUnit.SECONDS));
        } catch (TimeoutException tEx) {
            tEx.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return orderFragment;
    }

    //Goes through the category fragments in the adapter and adds up their menus into a new one
    //This is used to pull the orders and post them to the server
    private static Menu getOrdersFromAdapter(OrderFragmentPagerAdapter adapter, String restaurant) {
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();
        for (CategoryFragment fragment : adapter.getCategoryFragments()) {
            items.addAll(fragment.getItems());
        }
        Menu menu = new Menu(restaurant, items);
        menu.sortCategories();
        return menu;
    }

    private static boolean isOrderValid(OrderFragmentPagerAdapter adapter) {
        Menu menu = getOrdersFromAdapter(adapter, null);
        return menu.totalPence() > 0;

    }

    public PersistentParameters getParams() {
        return params;
    }

    public void setParams(PersistentParameters params) {
        this.params = params;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.order_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button nextButton = (Button) getActivity().findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);
        ImageButton cartButton = (ImageButton) getActivity().findViewById(R.id.add_to_cart_button);
        cartButton.setOnClickListener(this);
        ImageButton cancelButton = (ImageButton) getActivity().findViewById(R.id.clear_cart_button);
        cancelButton.setOnClickListener(this);

        if (mMenu != null) {

            TextView restaurantName = (TextView) params.getMainScreen().getActivity().findViewById(R.id.restaurant);
            restaurantName.setText(mMenu.getRestaurantName());

            mMenu.sortCategories();
            final ViewPager pager = (ViewPager) getActivity().findViewById(R.id.categoryPager);
            final OrderFragmentPagerAdapter adapter = new OrderFragmentPagerAdapter(getFragmentManager(), mMenu);
            mOrderFragmentPagerAdapter = adapter;
            pager.setAdapter(adapter);

            DialogFragment welcomeDialogFragment = WelcomeDialogFragment.newInstance(mMenu.getRestaurantName());
            welcomeDialogFragment.setTargetFragment(mainFragment, WELCOME_DIALOG);
            welcomeDialogFragment.show(getFragmentManager().beginTransaction(), "WELCOME_DIALOG");
        } else {
            TextView restaurantName = (TextView) params.getMainScreen().getActivity().findViewById(R.id.restaurant);
            restaurantName.setText("THERE IS AN ISSUE");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_cart_button:
                resetMenu();
                Toast.makeText(getActivity(), "Menu reset", Toast.LENGTH_SHORT).show();
                break;

            case R.id.add_to_cart_button:
                if (isOrderValid(mOrderFragmentPagerAdapter)) {
                    DialogFragment orderDialogFragment = OrderDialogFragment.newInstance(mMenu);
                    orderDialogFragment.setTargetFragment(mainFragment, ORDER_DIALOG);
                    orderDialogFragment.show(getFragmentManager().beginTransaction(), "ORDER_DIALOG");
                } else {
                    Toast.makeText(getActivity(), "Please add items to order!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ORDER_DIALOG:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //Pulls orders out of the adapter and sorts out non zero
                        Menu orders = getOrdersFromAdapter(mOrderFragmentPagerAdapter, mMenu.getRestaurantName());
                        orders = Menu.removeNonZero(orders);
                        loadingDialogForSendingOrder(orders).start();
                        break;
                    default:
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
                        Menu orders = getOrdersFromAdapter(mOrderFragmentPagerAdapter, mMenu.getRestaurantName());
                        orders = Menu.removeNonZero(orders);
                        loadingDialogForSendingPayment(params.getBill(), orders).start();
                        break;
                    default:
                        Log.d(TAG, "CANCELLED");
                        //Cancel transaction
                        break;
                }
                break;
            case PAYMENT_REQUEST_ERROR_DIALOG:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Menu orders = getOrdersFromAdapter(mOrderFragmentPagerAdapter, mMenu.getRestaurantName());
                        orders = Menu.removeNonZero(orders);
                        loadingDialogForSendingOrder(orders).start();
                        break;
                    default:
                        break;
                }
                break;
            case RECEIPT_DIALOG:
                params.getReceiptFragment().refreshAdapter();
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        params.resetBill();
                        resetMenu();
                    default:
                        break;
                }
                break;
            case RECEIPT_ERROR_DIALOG:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Menu orders = getOrdersFromAdapter(mOrderFragmentPagerAdapter, mMenu.getRestaurantName());
                        orders = Menu.removeNonZero(orders);
                        loadingDialogForSendingPayment(params.getBill(), orders).start();
                        break;
                    default:
                        Log.d(TAG, "CANCELLED");
                        //Cancel transaction
                        break;
                }
                break;
            case ORDER_CONFIRMATION_DIALOG:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        params.resetBill();
                        resetMenu();
                        break;
                    default:
                        ReceiptDialogFragment receiptDialogFragment = ReceiptDialogFragment.newInstance(params, resultCode, false);
                        receiptDialogFragment.show(getFragmentManager().beginTransaction(), "RECEIPT");
                        break;
                }
        }
    }

    private void resetMenu() {
        mMenu.resetQuantities();
        mMenu.sortCategories();
        final ViewPager pager = (ViewPager) getActivity().findViewById(R.id.categoryPager);
        final OrderFragmentPagerAdapter adapter = new OrderFragmentPagerAdapter(getFragmentManager(), mMenu);
        mOrderFragmentPagerAdapter = adapter;
        pager.setAdapter(adapter);
    }

    private Thread loadingDialogForSendingOrder(final Menu orders) {

        return new Thread(new Runnable() {
            @Override
            public void run() {

                DialogFragment loadingFragment = LoadingDialogFragment.newInstance();
                loadingFragment.show(getFragmentManager().beginTransaction(), "LOADING_DIALOG");

                ConnectThread mConnectThread = new ConnectThread();
                Future<Boolean> connectFuture = mConnectThread.runFuture();
                try {
                    if (connectFuture.get()) {
                        params.setSocket(mConnectThread.getSocket());
                        Future<Bill> requestFuture = BTillController.processOrders(orders, params.getSocket());
                        Bill bill = null;
                        //Protos.PaymentRequest request = null;
                        try {
                            //Blocks here until the request is returned
                            bill = requestFuture.get();
                            Log.d(TAG, "Retrieved request");
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Getting the Request was interrupted");
                        } catch (ExecutionException e) {
                            Log.e(TAG, "Getting the Request had an Execution Exception");
                        }
                        if (bill != null) {
                            params.setBill(bill);
                            loadingFragment.dismiss();
                            //final Protos.PaymentRequest finalRequest = params.getRequest();
                            final Bill finalBill = params.getBill();
                            final Menu finalOrders = orders;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //Launches a paymentconfirmation with the new Request
                                    DialogFragment paymentFragment = PaymentRequestDialogFragment.newInstance(finalBill, finalOrders);
                                    paymentFragment.setTargetFragment(mainFragment, PAYMENT_REQUEST_DIALOG);
                                    paymentFragment.show(getFragmentManager().beginTransaction(), "PAYMENT_REQUEST_DIALOG");

                                }
                            });
                        } else {
                            loadingFragment.dismiss();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    DialogFragment paymentErrorFragment = PaymentRequestErrorDialogFragment.newInstance();
                                    paymentErrorFragment.setTargetFragment(mainFragment, PAYMENT_REQUEST_ERROR_DIALOG);
                                    paymentErrorFragment.show(getFragmentManager().beginTransaction(), "PAYMENT_REQUEST_ERROR_DIALOG");

                                }
                            });
                        }

                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "Getting the Connection was interrupted");
                } catch (ExecutionException e) {
                    Log.e(TAG, "Getting the Connection had an Execution Exception");
                }
            }
        });
    }

    private Thread loadingDialogForSendingPayment(final Bill bill, final Menu menu) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                //Creates new loading dialog
                DialogFragment loadingFragment = LoadingDialogFragment.newInstance();
                loadingFragment.show(getFragmentManager().beginTransaction(), "LOADING_DIALOG");

                Protos.Payment payment = null;
                try {
                    payment = BTillController.transactionSigner(bill.getRequest(), params);
                } catch (InsufficientMoneyException e) {
                    loadingFragment.dismiss();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            DialogFragment insufficientFundsFragment = InsufficientFundsDialogFragment.newInstance();
                            insufficientFundsFragment.setTargetFragment(mainFragment, INSUFFICIENT_FUNDS);
                            insufficientFundsFragment.show(getFragmentManager().beginTransaction(), "INSUFFICIENT FUNDS");
                        }
                    });
                    return;
                } catch (Exception e) {
                    Log.e(TAG, "Error Signing Payment");
                }

                ConnectThread mConnectThread = new ConnectThread();
                Future<Boolean> connectFuture = mConnectThread.runFuture();
                try {
                    if (connectFuture.get()) {
                        params.setSocket(mConnectThread.getSocket());
                        Future<OrderConfirmation> confirmationFuture = BTillController.processPayment(bill.getOrderId(), payment, bill.getGbpAmount(), bill.getCoinAmount(), params.getLocationData(), params.getSocket());
                        OrderConfirmation orderConfirmation = null;
                        final int nextID = params.getReceiptStore().next();

                        try {
                            orderConfirmation = confirmationFuture.get();
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Getting the Receipt was interrupted");
                        } catch (ExecutionException e) {
                            Log.e(TAG, "Getting the Receipt had an Execution Exception");
                        }

                        if (orderConfirmation != null) {
                            params.getReceiptStore().add(nextID, menu.getRestaurantName(), orderConfirmation.getReceipt(), menu);
                            params.getReceiptFragment().needsUpdating();
                            // TODO uncomment below
                            params.getWallet().commitTx(params.getTx());
                            params.resetTx();
                            loadingFragment.dismiss();
                            final OrderConfirmation finalOrderConfirmation = orderConfirmation;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    /*DialogFragment receiptFragment = ReceiptDialogFragment.newInstance(params, nextID, false);
                                    receiptFragment.setTargetFragment(mainFragment, RECEIPT_DIALOG);
                                    receiptFragment.show(getFragmentManager().beginTransaction(), "RECEIPT_DIALOG");*/

                                    DialogFragment orderConfirmationFragment = OrderConfirmationDialogFragment.newInstance(params, finalOrderConfirmation, nextID);
                                    orderConfirmationFragment.setTargetFragment(mainFragment, ORDER_CONFIRMATION_DIALOG);
                                    orderConfirmationFragment.show(getFragmentManager().beginTransaction(), "ORDER_CONFIRMATION_DIALOG");
                                }
                            });
                        } else {
                            loadingFragment.dismiss();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    DialogFragment receiptErrorFragment = ReceiptErrorDialogFragment.newInstance();
                                    receiptErrorFragment.setTargetFragment(mainFragment, RECEIPT_ERROR_DIALOG);
                                    receiptErrorFragment.show(getFragmentManager().beginTransaction(), "RECEIPT_ERROR_DIALOG");
                                }
                            });
                        }
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "Getting the Connection was interrupted");
                } catch (ExecutionException e) {
                    Log.e(TAG, "Getting the Connection had an Execution Exception");
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public Menu getMenu() {
        return mMenu;
    }

    public void setMenu(Menu mMenu) {
        this.mMenu = mMenu;
    }

    public void replaceMenu(Menu menu) {
        mMenu = menu;
        mMenu.sortCategories();
        final ViewPager pager = (ViewPager) getActivity().findViewById(R.id.categoryPager);
        final OrderFragmentPagerAdapter adapter = new OrderFragmentPagerAdapter(getFragmentManager(), mMenu);
        mOrderFragmentPagerAdapter = adapter;
        pager.setAdapter(adapter);
    }
}