package com.g1453012.btill.UI.HomeScreenFragments.Order;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Category.CategoryFragment;

import java.util.ArrayList;

public class OrderFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<CategoryFragment> categoryFragments = new ArrayList<CategoryFragment>();
    private static final String TAG = "CategoryPageAdapter";

    public OrderFragmentPagerAdapter(FragmentManager fragmentManager, Menu menu) {
        super(fragmentManager);
        for (String category: menu.getCategories()) {
            CategoryFragment fragment = new CategoryFragment();
            Log.d(TAG, "Creating fragment for category: " + category);
            //Create a new fragment and get the items that fit into that category
            fragment.setItems(menu.getCategoryItems(category));
            //Add it to the list as we need it for later
            categoryFragments.add(fragment);
        }
    }

    public ArrayList<CategoryFragment> getCategoryFragments() {
        return categoryFragments;
    }

    @Override
    public CategoryFragment getItem(int position) {
        //Use this list as a store as we need persistent fragments without recycling (hopefully)
        return categoryFragments.get(position);
    }

    @Override
    public int getCount() {
        return categoryFragments.size();
    }

}