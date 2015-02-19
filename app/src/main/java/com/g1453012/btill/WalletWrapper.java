package com.g1453012.btill;

import android.util.Log;

import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.VersionMessage;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.MemoryBlockStore;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.interfaces.ECKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;

/**
 * Created by dlmiddlecote on 19/02/15.
 */
public class WalletWrapper {

    private final static String TAG = "WalletWrapper";

    private Wallet mWallet = null;
    private final TestNet3Params mNetParams = TestNet3Params.get();

    private final WalletAppKit mWalletAppKit;




    Peer mPeer = null;

    public WalletWrapper(Wallet wallet, File file) {
        mWallet = wallet;
        Log.d(TAG, "Loaded Wallet");


        mWalletAppKit = new WalletAppKit(mNetParams, file, "bitcoinj-test") {
            @Override
            protected void onSetupCompleted() {
                super.onSetupCompleted();
                if (wallet().getKeychainSize() < 1)
                    wallet().importKey(new org.bitcoinj.core.ECKey());
            }
        };

        Log.d(TAG, "Wallet App Kit");

        //mWalletAppKit.startAndWait();
        Log.d(TAG, "Started");
    }









}
