package com.g1453012.btill;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MenuFragment extends Fragment {

    private static final String TAG = "MenuFragment";

    private Order[] orders;
    private Activity mParentActivity;
    private BTillController mBTillController;

    public MenuFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mParentActivity = activity;
        HomeScreen screen = (HomeScreen) mParentActivity;
        mBTillController = screen.getBTillController();
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
        final ListView listView = (ListView)getActivity().findViewById(R.id.listView);
        this.orders = new Order[4];// = getOrders();
        this.orders[0] = new Order("Chicken", 2);
        this.orders[1] = new Order("Burger", 1);
        this.orders[2] = new Order("Onion Rings", 1);
        this.orders[3] = new Order("Coca-cola", 3);



        listView.setAdapter(new MenuAdapter(getActivity(), this.orders));

        Button nextButton = (Button)getActivity().findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuAdapter adapter = (MenuAdapter)listView.getAdapter();

                launchOrderDialog(adapter.orders, adapter);
            }
        });


    }

    private void launchOrderDialog(Order[] orders, MenuAdapter adapter) {

        final MenuAdapter mOrderDialogAdapter = adapter;

        ArrayList<Order> mOrderArrayList = new ArrayList<Order>();

        for (Order order: orders)
        {
            if (order.getQuantity()!=0)
                mOrderArrayList.add(order);
        }

        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //builder.setTitle(R.string.order_dialog_title)

        final Dialog mOrderDialog = new Dialog(getActivity());
        mOrderDialog.setContentView(R.layout.custom_order_dialog);
        mOrderDialog.setTitle(R.string.order_dialog_title);

        ListView mOrderListView = (ListView)mOrderDialog.findViewById(R.id.dialogListView);
        mOrderListView.setAdapter(new OrderDialogAdapter(getActivity(), mOrderArrayList));

        TextView mOrderTotal = (TextView)mOrderDialog.findViewById(R.id.dialogAmountText);
        double mTotal = 0;
        for (Order order: mOrderArrayList)
        {
            if (order.getQuantity()!=0) {
                mTotal += order.getPrice()*order.getQuantity();
            }
        }
        mOrderTotal.setText("£"+String.format("%.2f", mTotal));

        Button mConfirmButton = (Button)mOrderDialog.findViewById(R.id.dialogConfirmButton);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBTillController.sendOrders(mOrderDialogAdapter.orders);
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



}