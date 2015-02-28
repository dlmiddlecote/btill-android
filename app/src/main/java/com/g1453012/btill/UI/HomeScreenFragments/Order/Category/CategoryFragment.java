package com.g1453012.btill.UI.HomeScreenFragments.Order.Category;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.g1453012.btill.R;
import com.g1453012.btill.Shared.MenuItem;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {

    public CategoryFragment() {
    }
    private ArrayList<MenuItem> mItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.category_fragment, container, false);

        final ListView listView = (ListView) rootView.findViewById(R.id.categoryItemsList);
        //Creates a new adapter with the items for this category
        final CategoryListItemAdapter adapter = new CategoryListItemAdapter(getActivity(), mItems);
        listView.setAdapter(adapter);
        //On click listener updates the mItems in the fragment and alerts the adapter that the data has been updated to update view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (view.getId()) {
                    case R.id.plusButton:
                        //if the plus button was clicked, update the quantity on that item
                        mItems.get(position).incrementQuantity();
                        adapter.updateMenuItem(mItems.get(position), position);
                        break;
                    case R.id.minusButton:
                        //if the minus button was clicked, update the quantity on that item
                        mItems.get(position).decrementQuantity();
                        adapter.updateMenuItem(mItems.get(position), position);
                        break;
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public ArrayList<MenuItem> getItems() {
        return mItems;
    }

    public void setItems(ArrayList<MenuItem> mItems) {
        this.mItems = mItems;
    }
}