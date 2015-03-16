package com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.g1453012.btill.R;
import com.g1453012.btill.Shared.GBP;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.MenuItem;
import com.g1453012.btill.UI.TestFragmentActivity;

import java.util.ArrayList;

public class OrderDialogFragmentTest extends ActivityInstrumentationTestCase2<TestFragmentActivity> {

    private DialogFragment fragment = null;
    private Menu menu;


    public OrderDialogFragmentTest() {
        super(TestFragmentActivity.class);

    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ArrayList<MenuItem> list = new ArrayList<MenuItem>();
        list.add(new MenuItem("Coke", new GBP(150), "Drinks"));
        menu = new Menu(list);
        fragment = OrderDialogFragment.newInstance(menu);
    }

    private void startFragment(DialogFragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragment.show(transaction, "test");
        getInstrumentation().waitForIdleSync();
    }

    public void testMenu() throws Exception {
        ((OrderDialogFragment) fragment).setMenu(menu);
        assertNotNull(((OrderDialogFragment) fragment).getMenu());
    }

    public void testDialog() throws Exception {
        startFragment(fragment);

        ListView mOrderListView = (ListView) fragment.getDialog().findViewById(R.id.dialogListView);
        assertNotNull(mOrderListView);

        Button positiveButton = (Button) fragment.getDialog().findViewById(R.id.dialogConfirmButton);
        assertNotNull(positiveButton);

        Button negativeButton = (Button) fragment.getDialog().findViewById(R.id.dialogCancelButton);
        assertNotNull(negativeButton);

        TextView mOrderTotal = (TextView) fragment.getDialog().findViewById(R.id.dialogAmountText);
        assertNotSame("TotalPrice", mOrderTotal);

        fragment.dismiss();
    }
}