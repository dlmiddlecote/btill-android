package com.g1453012.btill.UI.HomeScreenFragments.Order;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Category.CategoryFragment;

public class OrderFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private Menu mMenu;
    private static final String TAG = "CategoryPageAdapter";

    public OrderFragmentPagerAdapter(FragmentManager fragmentManager, Menu menu) {
        super(fragmentManager);
        mMenu = menu;
    }

    @Override
    public CategoryFragment getItem(int position) {
        CategoryFragment fragment = new CategoryFragment();
        fragment.setMenu(mMenu);
        Log.d(TAG, "Creating fragment for category: " + mMenu.getCategories().get(position));
        fragment.setCategory(mMenu.getCategories().get(position));
        return fragment;
    }

    @Overridev
    public int getCount() {
        return mMenu.getCategories().size();
    }

}
