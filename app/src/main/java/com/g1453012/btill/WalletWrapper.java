package com.g1453012.btill;

import android.util.Log;

import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.VersionMessage;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.MemoryBlockStore;

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
    private BlockChain mChain = null;
    private PeerGroup mPeerGroup = null;


    Peer mPeer = null;

    public WalletWrapper(Wallet wallet) {
        mWallet = wallet;
        Log.d(TAG, "Loaded Wallet");
        try {
            mChain = new BlockChain(mNetParams, mWallet, new MemoryBlockStore(mNetParams));
            Log.d(TAG, "Loaded Blockchain");
        } catch (BlockStoreException e) {
            Log.e(TAG, "Error creating blockchain");
        }
        mPeerGroup = new PeerGroup(mNetParams,mChain);
        Log.d(TAG, "Loaded Peergroup");
        mPeerGroup.addWallet(mWallet);
        Log.d(TAG, "Added wallet");
        mPeerGroup.startAsync();
        Log.d(TAG, "Started Peergroup");
        Log.d(TAG, mWallet.toString());
        Log.d(TAG, "New address: " + mWallet.freshReceiveAddress().toString());
        //mWallet.importKey(new org.bitcoinj.core.ECKey());

        //Log.d(TAG, "Imported Keys: " + mWallet.getImportedKeys().get(0).toAddress(mNetParams).toString());

    }









}
