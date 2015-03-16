package com.g1453012.btill.UI.HomeScreenFragments.Receipts;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.g1453012.btill.R;
import com.g1453012.btill.ReceiptStore;
import com.g1453012.btill.Shared.Menu;
import com.g1453012.btill.Shared.Receipt;

import java.util.Set;

/**
 * Created by Andy on 16/03/2015.
 */
public class ReceiptListAdapter extends BaseAdapter{

    ReceiptStore receiptStore;
    LayoutInflater layoutInflater;

    public ReceiptListAdapter(ReceiptStore receiptStore, Context context) {
        this.receiptStore = receiptStore;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return receiptStore.getSize();
    }

    @Override
    public Object getItem(int position) {
        int ID = receiptStore.getID(position);

        return new Pair<Receipt, Menu>(receiptStore.getReceipt(ID), receiptStore.getMenu(ID));
    }

    @Override
    public long getItemId(int position) {
        return receiptStore.getID(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null)
        {
            rowView = layoutInflater.inflate(R.layout.receipt_list_item, parent, false);

            TextView title = (TextView)rowView.findViewById(R.id.receiptListItemTitle);
            TextView date = (TextView)rowView.findViewById(R.id.receiptListItemDate);
            TextView amount = (TextView)rowView.findViewById(R.id.receiptListItemAmount);

            title.setText("Dummy Restaurant"); //programmatic
            date.setText(receiptStore.getReceipt(receiptStore.getID(position)).getDate().toString());
            amount.setText(receiptStore.getReceipt(receiptStore.getID(position)).getGbp().toString());
        }

        return rowView;
    }
}
