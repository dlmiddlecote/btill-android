package com.g1453012.btill;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.g1453012.btill.Shared.Menu;

import java.util.ArrayList;

/**
 * Created by dlmiddlecote on 18/02/15.
 */
public class OrderDialogAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private Menu mMenu;

    public OrderDialogAdapter(Context context, Menu menu) {
        mLayoutInflater = LayoutInflater.from(context);
        mMenu = menu;
    }

    @Override
    public int getCount() {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null)
        {

            rowView = mLayoutInflater.inflate(R.layout.order_dialog_item, parent, false);
            TextView mDialogTitle = (TextView)rowView.findViewById(R.id.orderDialogTitle);
            mDialogTitle.setText(mMenu.get(position).getName());

            TextView mDialogQuantity = (TextView)rowView.findViewById(R.id.orderDialogQuantity);
            mDialogQuantity.setText("x" + mMenu.get(position).getQuantity());
        }

        return rowView;
    }
}
