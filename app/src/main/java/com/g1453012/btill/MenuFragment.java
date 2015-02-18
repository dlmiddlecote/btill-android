package com.g1453012.btill;


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
import android.widget.Button;
import android.widget.ListView;

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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.order_dialog_title)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBTillController.sendOrders(mOrderDialogAdapter.orders);
                    }
                });


        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Cancel Button Clicked");
            }
        });

        builder.setItems()

        AlertDialog dialog = builder.create();

        dialog.show();
    }

}