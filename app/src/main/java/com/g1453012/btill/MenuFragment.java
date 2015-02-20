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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.g1453012.btill.Shared.GBP;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {

    private static final String TAG = "MenuFragment";

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
        ArrayList<MenuItem> mMenuItems = new ArrayList<MenuItem>();
        //Menu mMenu = getOrders();
        mMenuItems.add(new MenuItem("Chicken", new GBP(200)));
        mMenuItems.add(new MenuItem("More Chicken", new GBP(100)));
        mMenuItems.add(new MenuItem("Hot Wings", new GBP(250)));
        mMenuItems.add(new MenuItem("Chicken Burger", new GBP(300)));
        mMenuItems.add(new MenuItem("Popcorn Chicken", new GBP(150)));
        Menu mMenu = new Menu(mMenuItems);



        listView.setAdapter(new MenuAdapter(getActivity(), mMenu));

        Button nextButton = (Button)getActivity().findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuAdapter adapter = (MenuAdapter)listView.getAdapter();
                // Launch Order dialog
                launchOrderDialog(adapter.getMenu());
            }
        });




    }

    private void launchOrderDialog(Menu menu) {

        final Menu nonZeroMenu = removeNonZero(menu);


        final Dialog mOrderDialog = new Dialog(getActivity());
        mOrderDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mOrderDialog.setContentView(R.layout.custom_order_dialog);

        ListView mOrderListView = (ListView)mOrderDialog.findViewById(R.id.dialogListView);
        mOrderListView.setAdapter(new OrderDialogAdapter(getActivity(), nonZeroMenu));

        TextView mOrderTotal = (TextView)mOrderDialog.findViewById(R.id.dialogAmountText);
        double mTotal = 0;
        for (MenuItem item: nonZeroMenu)
        {
            mTotal += item.getPrice().getPence()*item.getQuantity()/100;
        }
        mOrderTotal.setText("£"+String.format("%.2f", mTotal));

        Button mConfirmButton = (Button)mOrderDialog.findViewById(R.id.dialogConfirmButton);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Set connection to server
                /*ConnectThread mConnectThread = new ConnectThread();
                mConnectThread.start();
                mBTillController.setBluetoothSocket(mConnectThread.getSocket());*/
                mBTillController.sendOrders(nonZeroMenu);
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