package com.g1453012.btill.UI.HomeScreenFragments.Order;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.g1453012.btill.R;

/**
 * Created by dlmiddlecote on 10/03/15.
 */
public class SearchingForShopFragment extends Fragment {

    public static SearchingForShopFragment newInstance() {
        SearchingForShopFragment fragment = new SearchingForShopFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.searching_for_shop, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
