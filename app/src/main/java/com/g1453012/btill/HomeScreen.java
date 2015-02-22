package com.g1453012.btill;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.bitcoinj.core.Wallet;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.UnreadableWalletException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class HomeScreen extends Activity {

    private final static String TAG = "HomeScreen";

    private WalletKitThread mWalletKitThread;

    private BTillController mBTillController = null;

    private File mFile;

    private File mCheckpointFile;

    FileInputStream mCheckpointStream;

    private WalletAppKit mWalletAppKit;
    private final String filePrefix = "Bitcoin-test";
    private final TestNet3Params mNetParams = TestNet3Params.get();

    //private final Handler mHandler = new Handler();

    public BTillController getBTillController() {
        return mBTillController;
    }

    /*private class WalletKitThread extends Thread {

        public WalletKitThread() {
        }

        @Override
        public void run() {

            mWalletAppKit = new WalletAppKit(mNetParams, getApplicationContext().getExternalFilesDir("/wallet/"), filePrefix) {
                @Override
                protected void onSetupCompleted() {
                    // Allow spending unconfirmed transactions
                    Log.d(TAG, "Inside WalletKit OnSetupComplete");
                    mWalletAppKit.wallet().allowSpendingUnconfirmedTransactions();
                    mWalletAppKit.peerGroup().setBloomFilterFalsePositiveRate(0.0001);
                    mWalletAppKit.peerGroup().setMaxConnections(11);
                    //mWalletAppKit.peerGroup().setFastCatchupTimeSecs(mWalletAppKit.wallet().getEarliestKeyCreationTime());


                }
            };

            //mWalletAppKit.setCheckpoints(mCheckpointStream);
            //mBTillController.setWalletAppKit(mWalletAppKit);
            mWalletAppKit.startAsync();
            //mWalletAppKit.startAndWait();
            Log.d(TAG, "After started");
            mBTillController.setWalletAppKit(mWalletAppKit);
            try {
                mWalletAppKit.awaitRunning(10, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                Log.d(TAG, "Timed out");
            }
            mWalletAppKit.peerGroup().awaitRunning();

           // mWalletAppKit.awaitRunning();

            Log.d(TAG, mWalletAppKit.peerGroup().getConnectedPeers().toString());
           // mWalletAppKit.peerGroup().setFastCatchupTimeSecs(mBTillController.getWallet().getEarliestKeyCreationTime());
            Log.d(TAG, "After waiting");
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mWalletKitThread.start();
        mWalletKitThread = new WalletKitThread(getApplicationContext(), mBTillController);
        mWalletKitThread.start();

        setContentView(R.layout.activity_home_screen);
        if (savedInstanceState==null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Create new Controller when app loads
            mBTillController = new BTillController();
            ConnectThread mConnectThread = new ConnectThread();
            mConnectThread.start();
            mBTillController.setBluetoothSocket(mConnectThread.getSocket());
            mFile = new File(this.getExternalFilesDir("/wallet/"), filePrefix + ".wallet");
            mCheckpointFile = new File(this.getExternalFilesDir("/wallet/"), "checkpoints");
            try {
                mCheckpointStream = new FileInputStream(mCheckpointFile);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Couldn't find checkpoint file");
            }

            try {
                mBTillController.setWallet(Wallet.loadFromFile(mFile));
                Log.d(TAG, "Loaded Wallet");
                Log.d(TAG, "Wallet Address: " + mBTillController.getWallet().currentReceiveAddress().toString());
                Log.d(TAG, "Wallet Balance: " + mBTillController.getWallet().getBalance().toFriendlyString());
                Log.d(TAG, "Wallet: " + mBTillController.getWallet().toString());
            } catch (UnreadableWalletException e) {
                Log.d(TAG, "Error reading the wallet");
            }
            // TODO Add listenable future to make sure wallet kit is running
            //Log.d(TAG, mBTillController.getWalletAppKit().wallet().toString());

            Fragment fragment = new MenuFragment();
            transaction.add(R.id.fragmentFrame, fragment);
            transaction.commit();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mBTillController.getWallet().saveToFile(mFile);

            Log.d(TAG, "Saved file");
        } catch (IOException e) {
            Log.e(TAG, "Error saving file");
        }
        //File mFile = new File(this.getFilesDir(), "wallet.dat");
        //mFile.delete();


    }
}