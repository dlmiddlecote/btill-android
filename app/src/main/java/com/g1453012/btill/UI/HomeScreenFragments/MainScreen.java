package com.g1453012.btill.UI.HomeScreenFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.g1453012.btill.PersistentParameters;
import com.g1453012.btill.R;

/**
 * Created by Andy on 16/03/2015.
 */
public class MainScreen extends Fragment {

    private PersistentParameters params;

    public PersistentParameters getParams() {
        return params;
    }

    public void setParams(PersistentParameters params) {
        this.params = params;
    }

    public static MainScreen newInstance(PersistentParameters params) {
        MainScreen mainScreen = new MainScreen();
        mainScreen.setParams(params);
        return mainScreen;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_home_screen, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ViewPager pager = (ViewPager) getActivity().findViewById(R.id.mainScreenPager);
        pager.setAdapter(new MainTabViewPagerAdapter(getFragmentManager(), params));
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip)getActivity().findViewById(R.id.tabs);
        tabStrip.setViewPager(pager);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pager.setCurrentItem(position, true);
                if (position == 1) {
                    params.getReceiptFragment().refreshAdapter();
                }
                else if (position == 1) {
                    params.getBalanceFragment().refresh();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }



}
