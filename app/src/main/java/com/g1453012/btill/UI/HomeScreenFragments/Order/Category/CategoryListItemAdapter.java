package com.g1453012.btill.UI.HomeScreenFragments.Order.Category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.g1453012.btill.R;
import com.g1453012.btill.Shared.MenuItem;

import java.util.ArrayList;

public class CategoryListItemAdapter extends BaseAdapter{
    private LayoutInflater mLayoutInflater;
    private ArrayList<MenuItem> mItems;
    private static final String TAG = "MenuAdapter";

    public ArrayList<MenuItem> getItems() {
        return mItems;
    }

    public CategoryListItemAdapter(Context context, ArrayList<MenuItem> items) {
        mLayoutInflater = LayoutInflater.from(context);
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null)
        {
            rowView = mLayoutInflater.inflate(R.layout.menu_list_item, parent, false);

            TextView title = (TextView)rowView.findViewById(R.id.itemTitle);
            title.setText(mItems.get(position).getName());

            final TextView quantity = (TextView)rowView.findViewById(R.id.quantity);
            quantity.setText(String.valueOf(mItems.get(position).getQuantity()));

            Button plusButton = (Button)rowView.findViewById(R.id.plusButton);
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Max amount is 9 to help with UI
                    // TODO Fix UI Here (Number overlaps with +)

                    mItems.get(position).setQuantity(Math.min(mItems.get(position).getQuantity() + 1, 99));
                    quantity.setText(String.valueOf(mItems.get(position).getQuantity()));
                }
            });

            Button minusButton = (Button)rowView.findViewById(R.id.minusButton);
            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItems.get(position).setQuantity(Math.max(mItems.get(position).getQuantity() - 1, 0));
                    quantity.setText(String.valueOf(mItems.get(position).getQuantity()));
                }
            });

            TextView price = (TextView)rowView.findViewById(R.id.price);
            price.setText(mItems.get(position).getPrice().toString());
        }

        return rowView;
    }

    public void updateMenuItem(MenuItem item, int position) {
        mItems.set(position, item);
        notifyDataSetChanged();
    }
}