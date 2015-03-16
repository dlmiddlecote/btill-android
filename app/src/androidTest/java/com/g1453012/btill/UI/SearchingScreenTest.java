package com.g1453012.btill.UI;


import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.g1453012.btill.R;
import com.g1453012.btill.UI.AppStartup.AppStartup;
import com.g1453012.btill.UI.HomeScreenFragments.Order.SearchingForShopFragment;

/**
 * Created by luke on 10/03/2015.
 */

public class SearchingScreenTest extends ActivityInstrumentationTestCase2<AppStartup> {

    private SearchingForShopFragment fragment;

    public SearchingScreenTest(){
        super(AppStartup.class);
    }

    @Override
    public void setUp() throws Exception {
        //super.setUp();
        fragment = SearchingForShopFragment.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.appStartupFragmentFrame, fragment);
        transaction.commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
    }

    public void testLogo(){
        ImageView logo = (ImageView) getActivity().findViewById(R.id.appIcon);
        assertEquals(true, logo.getDrawable().isVisible());
    }

    public void testText(){
        TextView text = (TextView) getActivity().findViewById(R.id.initialSearchingTextView);
        assertEquals(getActivity().getText(R.string.searching), text.getText());

    }

    public void testProgressBar(){
        ProgressBar bar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        assertEquals(true, bar.isIndeterminate());
    }

}
