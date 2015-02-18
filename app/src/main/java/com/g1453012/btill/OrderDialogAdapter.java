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

import java.util.ArrayList;

/**
 * Created by dlmiddlecote on 18/02/15.
 */
public class OrderDialogAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private ArrayList<Order> mOrders;

    public OrderDialogAdapter(Context context, ArrayList<Order> orders) {
        mLayoutInflater = LayoutInflater.from(context);
        mOrders = orders;
    }

    @Override
    public int getCount() {
        return mOrders.size();
    }

    @Override
    public Object getItem(int position) {
        return mOrders.get(position);
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
            mDialogTitle.setText(mOrders.get(position).getTitle());

            TextView mDialogQuantity = (TextView)rowView.findViewById(R.id.orderDialogQuantity);
            mDialogQuantity.setText("x" + mOrders.get(position).getQuantity());
        }

        return rowView;
    }
}
