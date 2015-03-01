package com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.g1453012.btill.R;
import com.g1453012.btill.Shared.GBP;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.MenuItem;
import com.g1453012.btill.UI.OrderDialogAdapter;

public class OrderDialogFragment extends DialogFragment implements View.OnClickListener{

    public Menu getMenu() {
        return mMenu;
    }

    public void setMenu(Menu mMenu) {
        this.mMenu = mMenu;
    }

    private Menu mMenu;

    public static OrderDialogFragment newInstance(Menu menu) {
        menu = removeNonZero(menu);
        OrderDialogFragment orderDialogFragment = new OrderDialogFragment();
        orderDialogFragment.setMenu(menu);
        return orderDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog mOrderDialog = new Dialog(getActivity());
        mOrderDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mOrderDialog.setContentView(R.layout.custom_order_dialog);

        ListView mOrderListView = (ListView)mOrderDialog.findViewById(R.id.dialogListView);
        mOrderListView.setAdapter(new OrderDialogAdapter(getActivity(), mMenu));

        Button positiveButton = (Button)mOrderDialog.findViewById(R.id.dialogConfirmButton);
        Button negativeButton = (Button)mOrderDialog.findViewById(R.id.dialogCancelButton);

        positiveButton.setOnClickListener(this);
        negativeButton.setOnClickListener(this);


        TextView mOrderTotal = (TextView)mOrderDialog.findViewById(R.id.dialogAmountText);
        GBP mTotal = new GBP(0);
        for (MenuItem item: mMenu) {
            mTotal = mTotal.plus(item.getPrice().times(item.getQuantity()));
        }
        mOrderTotal.setText(mTotal.toString());

        return mOrderDialog;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogConfirmButton:
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                getDialog().dismiss();
                break;
            case R.id.dialogCancelButton:
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                dismiss();
                break;
        }
    }

    private static Menu removeNonZero(Menu menu) {
        Menu retMenu = new Menu();
        for (MenuItem item: menu)
        {
            if (item.getQuantity()!=0)
                retMenu.add(item);
        }

        return retMenu;
    }
}
