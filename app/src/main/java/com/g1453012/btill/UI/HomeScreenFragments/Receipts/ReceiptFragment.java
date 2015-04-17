package com.g1453012.btill.UI.HomeScreenFragments.Receipts;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.g1453012.btill.PersistentParameters;
import com.g1453012.btill.R;
import com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs.ReceiptDialogFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Order.ServerNotFoundFragment;

/**
 * Created by Andy on 16/03/2015.
 */
public class ReceiptFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = "ReceiptFragment";

    private PersistentParameters params;
    private ReceiptListAdapter adapter = null;
    private boolean backButtonRequired;
    private boolean needsUpdating;

    private ListView listView = null;

    public static ReceiptFragment newInstance(PersistentParameters params, boolean backButton) {
        ReceiptFragment receiptFragment = new ReceiptFragment();
        receiptFragment.setParams(params);
        receiptFragment.setBackButton(backButton);
        receiptFragment.setNeedsUpdating(false);
        return receiptFragment;
    }

    public PersistentParameters getParams() {
        return params;
    }

    public void setParams(PersistentParameters params) {
        this.params = params;
    }

    public void setBackButton(boolean backButton) {
        this.backButtonRequired = backButton;
    }

    private void setNeedsUpdating(boolean needsUpdating) {
        this.needsUpdating = needsUpdating;
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
        adapter = new ReceiptListAdapter(params.getReceiptStore(), getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReceiptDialogFragment receiptDialogFragment = ReceiptDialogFragment.newInstance(params, (int) id, !backButtonRequired);
                receiptDialogFragment.show(getFragmentManager().beginTransaction(), "RECEIPT");
            }
        });

        ImageButton backButton = (ImageButton) getActivity().findViewById(R.id.backButton);
        if (backButtonRequired) {
            backButton.setOnClickListener(this);
            backButton.setVisibility(View.VISIBLE);
        } else {
            backButton.setVisibility(View.GONE);
        }
    }

    public void needsUpdating() {
        needsUpdating = true;
    }

    public void refreshAdapter() {
        if (needsUpdating) {
            Log.d(TAG, "Refreshing view");
            this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ListView listView = (ListView) getActivity().findViewById(R.id.receiptListView);
                    adapter = new ReceiptListAdapter(params.getReceiptStore(), getActivity());
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
            needsUpdating = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment fragment = ServerNotFoundFragment.newInstance(params);
                transaction.replace(R.id.appStartupFragmentFrame, fragment);
                transaction.commit();
                break;
            default:
                break;
        }
    }
}
