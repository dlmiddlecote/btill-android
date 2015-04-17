package com.g1453012.btill.UI.HomeScreenFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.g1453012.btill.BTillController;
import com.g1453012.btill.Bluetooth.ConnectThread;
import com.g1453012.btill.PersistentParameters;
import com.g1453012.btill.R;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs.LoadingDialogFragment;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Andy on 16/03/2015.
 */
public class MainScreen extends Fragment implements View.OnClickListener {

    private static final String TAG = "MainScreen";

    private PersistentParameters params;

    public static MainScreen newInstance(PersistentParameters params) {
        MainScreen mainScreen = new MainScreen();
        params.setMainScreen(mainScreen);
        mainScreen.setParams(params);
        return mainScreen;
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
        View rootView = inflater.inflate(R.layout.activity_home_screen, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView restaurantName = (TextView) getActivity().findViewById(R.id.restaurant);
        restaurantName.setText("B-Till");

        ImageButton refreshButton = (ImageButton) getActivity().findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(this);

        final ViewPager pager = (ViewPager) getActivity().findViewById(R.id.mainScreenPager);
        pager.setAdapter(new MainTabViewPagerAdapter(getFragmentManager(), params));
        final PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs);
        tabStrip.setViewPager(pager);
        tabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //pager.setCurrentItem(position, true);
                tabStrip.notifyDataSetChanged();
                if (position == 1) {
                    params.getReceiptStore().refreshReceipts();
                    params.getReceiptFragment().refreshAdapter();

                }else if (position == 2) {
                    params.getBalanceFragment().refresh();
                    //pager.setOffscreenPageLimit();
                }
                pager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabStrip.setIndicatorColorResource(R.color.myDarkerBlue);

        /*pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pager.setCurrentItem(position, true);
                tabStrip.notifyDataSetChanged();
                if (position != 1) {
                    params.getReceiptStore().refreshReceipts();
                    params.getReceiptFragment().refreshAdapter();

                }
                if (position != 2) {
                    params.getBalanceFragment().refresh();
                    //pager.setOffscreenPageLimit();
                }
                pager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refreshButton:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DialogFragment loadingFragment = LoadingDialogFragment.newInstance();
                        loadingFragment.show(getFragmentManager().beginTransaction(), "LOADING_DIALOG");

                        ConnectThread mConnectThread = new ConnectThread();
                        Future<Boolean> connectFuture = mConnectThread.runFuture();

                        try {
                            if (connectFuture.get()) {
                                params.setSocket(mConnectThread.getSocket());
                                Future<Menu> menuFuture = BTillController.getMenuFuture(params.getSocket());
                                try {
                                    final Menu refreshedMenu = menuFuture.get();

                                    params.getOrderFragment().getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (refreshedMenu != null) {
                                                params.getOrderFragment().replaceMenu(refreshedMenu);
                                                TextView restaurant = (TextView) params.getMainScreen().getActivity()
                                                        .findViewById(R.id.restaurant);
                                                restaurant.setText(refreshedMenu.getRestaurantName());

                                                Toast.makeText(getActivity(), "Refreshed Menu", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), "Error Refreshing Menu", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    // params.getOrderFragment().getOrderFragmentPagerAdapter().notifyDataSetChanged();
                                    //Log.d(TAG, "Menu has been found : " + refreshedMenu.getRestaurantName());
                                } catch (ExecutionException ex) {
                                    Log.d(TAG, "There was an issue");
                                } catch (InterruptedException e) {
                                    Log.d(TAG, "There was an issue");
                                }
                            }
                        } catch (ExecutionException e) {
                            Log.d(TAG, "There was an issue in the connection future");
                        } catch (InterruptedException e) {
                            Log.d(TAG, "There was an issue in the connection future");
                        }

                        loadingFragment.dismiss();
                    }
                }).start();

                break;
            default:
                break;
        }
    }
}
