package com.g1453012.btill;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MenuAdapter extends ArrayAdapter<Order>{

    Context context;
    Order[] orders;

    public MenuAdapter(Context context, Order[] orders) {
        super(context, R.layout.menu_list_item, orders);
        this.context = context;
        this.orders = orders;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.menu_list_item, parent, false);

            TextView title = (TextView)rowView.findViewById(R.id.itemTitle);
            title.setText(orders[position].getTitle());

            final TextView quantity = (TextView)rowView.findViewById(R.id.quantity);
            quantity.setText(String.valueOf(orders[position].getQuantity()));

            Button plusButton = (Button)rowView.findViewById(R.id.plusButton);
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orders[position].setQuantity(orders[position].getQuantity() + 1);
                    quantity.setText(String.valueOf(orders[position].getQuantity()));
                }
            });

            Button minusButton = (Button)rowView.findViewById(R.id.minusButton);
            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orders[position].setQuantity(orders[position].getQuantity() - 1);
                    quantity.setText(String.valueOf(orders[position].getQuantity()));
                }
            });

            TextView price = (TextView)rowView.findViewById(R.id.price);
            String priceString = String.format("%.2f", orders[position].getPrice());
            price.setText("£" + priceString);
        }

        return rowView;
    }
}
