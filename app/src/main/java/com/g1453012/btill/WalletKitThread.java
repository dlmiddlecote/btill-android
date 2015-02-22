package com.g1453012.btill;

import android.content.Context;
import android.util.Log;

import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;

/**
 * Created by dlmiddlecote on 20/02/15.
 */
public class WalletKitThread extends Thread {

    private final static String TAG = "WalletKitThread";

    private WalletAppKit mWalletAppKit;

    private BTillController mBTillController;

    private final String filePrefix = "Bitcoin-test";
    private final TestNet3Params mNetParams = TestNet3Params.get();
    private final Context mContext;

    public WalletKitThread(Context context, BTillController bTillController) {
        mContext = context;
        mBTillController = bTillController;
    }

    public WalletAppKit getWalletAppKit() {
        return mWalletAppKit;
    }

    @Override
    public void run() {

        Log.d(TAG, "WalletKitThread has started");

        mWalletAppKit = new WalletAppKit(mNetParams, mContext.getExternalFilesDir("/wallet/"), filePrefix) {
            @Override
            protected void onSetupCompleted() {
                // Allow spending unconfirmed transactions
                Log.d(TAG, "Inside WalletKit OnSetupComplete");
                mWalletAppKit.wallet().allowSpendingUnconfirmedTransactions();
                mWalletAppKit.peerGroup().setBloomFilterFalsePositiveRate(0.0001);
                mWalletAppKit.peerGroup().setMaxConnections(11);
                mWalletAppKit.peerGroup().setFastCatchupTimeSecs(mWalletAppKit.wallet().getEarliestKeyCreationTime());
            }
        };

        //mWalletAppKit.setCheckpoints(mCheckpointStream);
        mWalletAppKit.startAsync();
        Log.d(TAG, "WalletAppKit has started");
        mWalletAppKit.awaitRunning();
        Log.d(TAG, "WalletAppKit is running");
        //mBTillController.setWalletAppKit(mWalletAppKit);

    }





}

