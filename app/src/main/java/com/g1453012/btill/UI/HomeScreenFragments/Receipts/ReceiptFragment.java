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

    private ReceiptListAdapter adapter;

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

        ListView listView = (ListView)getActivity().findViewById(R.id.receiptListView);
        adapter = new ReceiptListAdapter(params.getReceiptStore(), getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReceiptDialogFragment receiptDialogFragment = ReceiptDialogFragment.newInstance(params, (int)id);
                receiptDialogFragment.show(getFragmentManager().beginTransaction(), "RECEIPT");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume Called");
        params.refreshReceiptStore();
        listView = (ListView) getActivity().findViewById(R.id.receiptListView);
        listView.setAdapter(new ReceiptListAdapter(params.getReceiptStore(), getActivity()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReceiptDialogFragment receiptDialogFragment = ReceiptDialogFragment.newInstance(params, (int)id);
                receiptDialogFragment.show(getFragmentManager().beginTransaction(), "RECEIPT");
            }
        });
    }

    public void refreshAdapter() {
        if (listView != null) {
            listView.setAdapter(new ReceiptListAdapter(params.getReceiptStore(), getActivity()));
            ReceiptListAdapter receiptListAdapter = (ReceiptListAdapter) listView.getAdapter();
            receiptListAdapter.notifyDataSetChanged();
        }
    }
}
