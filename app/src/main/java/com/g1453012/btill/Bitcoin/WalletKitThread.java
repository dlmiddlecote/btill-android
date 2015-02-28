package com.g1453012.btill.Bitcoin;

import android.content.Context;
import android.util.Log;

import com.g1453012.btill.BTillController;

import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;

import java.io.File;

/**
 * Created by dlmiddlecote on 20/02/15.
 */
public class WalletKitThread extends Thread {

    private final static String TAG = "WalletKitThread";

    private WalletAppKit mWalletAppKit;

    private final String filePrefix = "Bitcoin-test";
    private final TestNet3Params mNetParams = TestNet3Params.get();
    private final Context mContext;
    private final File mFile;

    public WalletKitThread(Context context, File file) {
        mContext = context;
        mFile = file;
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
                //mWalletAppKit.wallet().autosaveToFile(mFile, 1, TimeUnit.MINUTES, null);
            }
        };

        //mWalletAppKit.setCheckpoints(mCheckpointStream);
        mWalletAppKit.startAsync();
        Log.d(TAG, "WalletAppKit has started");
        mWalletAppKit.awaitRunning();
        Log.d(TAG, "WalletAppKit is running");

    }





}

