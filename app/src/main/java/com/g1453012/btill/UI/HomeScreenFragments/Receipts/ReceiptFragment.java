package com.g1453012.btill.UI.HomeScreenFragments.Receipts;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.g1453012.btill.PersistentParameters;
import com.g1453012.btill.R;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs.ReceiptDialogFragment;

/**
 * Created by Andy on 16/03/2015.
 */
public class ReceiptFragment extends Fragment {

    private final static String TAG = "ReceiptFragment";

    private PersistentParameters params;

    //private ReceiptListAdapter adapter;
    private NewReceiptListAdapter adapter = null;

    private ListView listView = null;

    public PersistentParameters getParams() {
        return params;
    }

    public void setParams(PersistentParameters params) {
        this.params = params;
    }

    public static ReceiptFragment newInstance(PersistentParameters params) {
        ReceiptFragment receiptFragment = new ReceiptFragment();
        receiptFragment.setParams(params);
        return receiptFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.receipt_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView = (ListView) getActivity().findViewById(R.id.receiptListView);
        //adapter = new ReceiptListAdapter(params.getReceiptStore(), getActivity());
        adapter = new NewReceiptListAdapter(params.getNewReceiptStore(), getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReceiptDialogFragment receiptDialogFragment = ReceiptDialogFragment.newInstance(params, (int) id, true);
                receiptDialogFragment.show(getFragmentManager().beginTransaction(), "RECEIPT");
            }
        });
    }

    public void refreshAdapter() {
        Log.d(TAG, "Refreshing view");
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView = (ListView) getActivity().findViewById(R.id.receiptListView);
                //adapter = new ReceiptListAdapter(params.getReceiptStore(), getActivity());
                adapter = new NewReceiptListAdapter(params.getNewReceiptStore(), getActivity());
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ReceiptDialogFragment receiptDialogFragment = ReceiptDialogFragment.newInstance(params, (int) id, true);
                        receiptDialogFragment.show(getFragmentManager().beginTransaction(), "RECEIPT");
                    }
                });
            }
        });

        /*if (listView != null && listView.getAdapter() != null) {
            Log.d(TAG, "Refreshing view");
            params.getNewReceiptStore().refreshReceipts();
            ((NewReceiptListAdapter) listView.getAdapter()).notifyDataSetChanged();
            //adapter.notifyDataSetChanged();
            //adapter.setReceiptStore(params.getReceiptStore().refreshReceipts());
            //((ReceiptListAdapter) listView.getAdapter()).notifyDataSetChanged();
            //adapter = new ReceiptListAdapter(params.getReceiptStore().refreshReceipts(), getActivity());
            //listView.setAdapter(adapter);

        }*/
    }
}
