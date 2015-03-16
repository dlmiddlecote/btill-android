package com.g1453012.btill.UI;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.g1453012.btill.R;
import com.g1453012.btill.Shared.Menu;

public class MenuAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private Menu mMenu;
    private static final String TAG = "MenuAdapter";

    public Menu getMenu() {
        return mMenu;
    }

    public MenuAdapter(Context context, Menu menu) {
        mLayoutInflater = LayoutInflater.from(context);
        mMenu = menu;
    }

    @Override
    public int getCount() {
        Log.d(TAG, mMenu.toString());
        return mMenu.size();
    }

    @Override
    public Object getItem(int position) {
        return mMenu.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null)
        {
            rowView = mLayoutInflater.inflate(R.layout.menu_list_item, parent, false);

            TextView title = (TextView)rowView.findViewById(R.id.itemTitle);
            title.setText(mMenu.get(position).getName());

            final TextView quantity = (TextView)rowView.findViewById(R.id.quantity);
            quantity.setText(String.valueOf(mMenu.get(position).getQuantity()));

            Button plusButton = (Button)rowView.findViewById(R.id.plusButton);
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Max amount is 9 to help with UI
                    // TODO Fix UI Here (Number overlaps with +)
                    mMenu.get(position).setQuantity(Math.min(mMenu.get(position).getQuantity() + 1, 9));
                    quantity.setText(String.valueOf(mMenu.get(position).getQuantity()));
                }
            });

            Button minusButton = (Button)rowView.findViewById(R.id.minusButton);
            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMenu.get(position).setQuantity(Math.max(mMenu.get(position).getQuantity() - 1, 0));
                    quantity.setText(String.valueOf(mMenu.get(position).getQuantity()));
                }
            });

            TextView price = (TextView)rowView.findViewById(R.id.price);
            price.setText(mMenu.get(position).getPrice().toString());
        }

        return rowView;
    }
}
