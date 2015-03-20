package com.g1453012.btill.UI.HomeScreenFragments.Receipts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.g1453012.btill.NewReceiptStore;
import com.g1453012.btill.R;


/**
 * Created by dlmiddlecote on 20/03/15.
 */
public class NewReceiptListAdapter extends BaseAdapter {

    private static final String TAG = "ReceiptListAdapter";

    NewReceiptStore receiptStore;
    LayoutInflater layoutInflater;

    public NewReceiptListAdapter(NewReceiptStore receiptStore, Context context) {
        this.receiptStore = receiptStore;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return receiptStore.getSize();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return receiptStore.getID(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null)
        {
            rowView = layoutInflater.inflate(R.layout.receipt_list_item, parent, false);

            TextView title = (TextView) rowView.findViewById(R.id.receiptListItemTitle);
            TextView date = (TextView )rowView.findViewById(R.id.receiptListItemDate);
            TextView amount = (TextView) rowView.findViewById(R.id.receiptListItemAmount);
            if (receiptStore.getSize() > 0) {
                title.setText(receiptStore.getRestaurant(receiptStore.getID(position))); //programmatic
                date.setText(receiptStore.getReceipt(receiptStore.getID(position)).getDateAsString());
                amount.setText(receiptStore.getReceipt(receiptStore.getID(position)).getGbp().toString());
            }
        }

        return rowView;
    }

}
