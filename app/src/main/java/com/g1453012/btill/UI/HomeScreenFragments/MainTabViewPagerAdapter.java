package com.g1453012.btill.UI.HomeScreenFragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.g1453012.btill.PersistentParameters;
import com.g1453012.btill.UI.HomeScreenFragments.Balance.BalanceFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Order.OrderFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Receipts.ReceiptFragment;

/**
 * Created by Andy on 16/03/2015.
 */
public class MainTabViewPagerAdapter extends FragmentPagerAdapter {

    private PersistentParameters params;
    private OrderFragment orderFragment;
    private ReceiptFragment receiptFragment;
    private BalanceFragment balanceFragment;

    public MainTabViewPagerAdapter(FragmentManager fm, PersistentParameters params) {
        super(fm);
        this.params = params;
        this.orderFragment = OrderFragment.newInstance(params);
        params.setOrderFragment(this.orderFragment);
        this.receiptFragment = ReceiptFragment.newInstance(params, false);
        params.setReceiptFragment(this.receiptFragment);
        this.balanceFragment = BalanceFragment.newInstance(params);
        params.setBalanceFragment(this.balanceFragment);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return orderFragment;
            case 1:
                return receiptFragment;
            default:
                return balanceFragment;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Order";
            case 1:
                return "Receipts";
            case 2:
                return "Balance";
            default:
                return "";
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }


}
