package com.g1453012.btill.UI.HomeScreenFragments.Order.Category;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.g1453012.btill.R;
import com.g1453012.btill.Shared.Menu;

public class CategoryFragment extends Fragment {

    public CategoryFragment() {

    }

    private String category;
    private Menu mMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.category_fragment, container, false);
        rootView.setBackgroundColor(getResources().getColor(R.color.blue));
        ListView listView = (ListView) rootView.findViewById(R.id.categoryItemsList);

        listView.setAdapter(new CategoryListItemAdapter(getActivity(), mMenu, category));

        TextView textView = (TextView) rootView.findViewById(R.id.categoryTitle);
        textView.setText(category);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public Menu getMenu() {
        return mMenu;
    }

    public void setMenu(Menu menu) {
        this.mMenu = menu;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
