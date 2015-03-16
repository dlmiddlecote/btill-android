package com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.g1453012.btill.R;
import com.g1453012.btill.UI.TestFragmentActivity;

import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;

public class BalanceDialogFragmentTest extends ActivityInstrumentationTestCase2<TestFragmentActivity> {

    private DialogFragment fragment = null;
    private Wallet wallet;

    public BalanceDialogFragmentTest() {
        super(TestFragmentActivity.class);

    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        wallet = new Wallet(TestNet3Params.get());
        fragment = BalanceDialogFragment.newInstance(wallet);
    }

    private void startFragment(DialogFragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragment.show(transaction, "test");
        getInstrumentation().waitForIdleSync();
    }

    public void testSetWallet() throws Exception {
        ((BalanceDialogFragment) fragment).setWallet(wallet);
        assertNotNull(((BalanceDialogFragment) fragment).getWallet());
    }

    public void testDialog() throws Exception {
        startFragment(fragment);

        TextView mBalanceAddress = (TextView) fragment.getDialog().findViewById(R.id.balanceFragmentAddress);
        assertNotSame("Address", mBalanceAddress);

        TextView mBalanceTotal = (TextView) fragment.getDialog().findViewById(R.id.balanceFragmentBalance);
        assertNotSame("Balance", mBalanceTotal);

        ImageView mBalanceQR = (ImageView) fragment.getDialog().findViewById(R.id.balanceDialogQR);
        assertNotNull(mBalanceQR);

        Button dismissButton = (Button) fragment.getDialog().findViewById(R.id.balanceDialogButton);
        assertNotNull(dismissButton);

        assertTrue(dismissButton.callOnClick());

        fragment.dismiss();
    }
}