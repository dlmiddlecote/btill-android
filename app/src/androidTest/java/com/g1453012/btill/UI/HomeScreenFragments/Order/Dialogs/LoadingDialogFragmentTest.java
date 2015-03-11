package com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ProgressBar;

import com.g1453012.btill.R;
import com.g1453012.btill.UI.TestFragmentActivity;

public class LoadingDialogFragmentTest extends ActivityInstrumentationTestCase2<TestFragmentActivity> {

    private DialogFragment fragment = null;

    public LoadingDialogFragmentTest() {
        super(TestFragmentActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        fragment = LoadingDialogFragment.newInstance();
    }

    private void startFragment(DialogFragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragment.show(transaction, "test");
        getInstrumentation().waitForIdleSync();
    }

    public void testProgressBar() throws Exception {
        startFragment(fragment);

        ProgressBar mProgressBar = (ProgressBar) fragment.getDialog().findViewById(R.id.loadingProgressBar);
        assertTrue(mProgressBar.isIndeterminate());

        fragment.dismiss();
    }
}