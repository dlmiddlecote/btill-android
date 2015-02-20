package com.g1453012.btill;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.DownloadListener;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.UnreadableWalletException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class HomeScreen extends Activity {

    private final static String TAG = "HomeScreen";

    private WalletKitThread mWalletKitThread = new WalletKitThread();

    private BTillController mBTillController = null;

    private File mFile;

    private WalletAppKit mWalletAppKit;
    private final String filePrefix = "Bitcoin-test";
    private final TestNet3Params mNetParams = TestNet3Params.get();

    //private final Handler mHandler = new Handler();

    public BTillController getBTillController() {
        return mBTillController;
    }

    private class WalletKitThread extends Thread {

        File mCheckpointFile;
        FileInputStream mCheckpointStream;

        public WalletKitThread() {

           /* mCheckpointFile = new File("checkpoints");

            try {
                mCheckpointStream = new FileInputStream(mCheckpointFile);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Checkpoints file not found");
            }*/
        }

        @Override
        public void run() {

            mWalletAppKit = new WalletAppKit(mNetParams, getApplicationContext().getExternalFilesDir("/wallet/"), filePrefix) {
                @Override
                protected void onSetupCompleted() {
                    // Allow spending unconfirmed transactions
                    Log.d(TAG, "Inside WalletKit OnSetupComplete");
                    mWalletAppKit.wallet().allowSpendingUnconfirmedTransactions();

                }
            };

            //mWalletAppKit.connectToLocalHost();
            DownloadListener l = new DownloadListener() {

                @Override
                public void onBlocksDownloaded(Peer peer, Block block, int blocksLeft) {
                    super.onBlocksDownloaded(peer, block, blocksLeft);
                    Log.d(TAG, "Downloaded");
                    int used = (int)((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000);

                    if (used > 16) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                        }
                    }


                }
            };

            mWalletAppKit.setDownloadListener(l);


            //mWalletAppKit.setBlockingStartup(false);
            //mWalletAppKit.setCheckpoints(mCheckpointStream);
            mBTillController.setWalletAppKit(mWalletAppKit);
            mWalletAppKit.startAsync();
            //mWalletAppKit.startAndWait();
            Log.d(TAG, "After started");

            try {
                mWalletAppKit.awaitRunning(30, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                Log.d(TAG, "Timed out");
            }
            mWalletAppKit.peerGroup().setFastCatchupTimeSecs(mBTillController.getWallet().getEarliestKeyCreationTime());
            Log.d(TAG, "After waiting");



        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

            try {
                mBTillController.setWallet(Wallet.loadFromFile(mFile));
                Log.d(TAG, "Loaded Wallet");
                Log.d(TAG, "Wallet Address: " + mBTillController.getWallet().currentReceiveAddress().toString());
                Log.d(TAG, "Wallet Balance: " + mBTillController.getWallet().getBalance().toFriendlyString());
                Log.d(TAG, "Wallet: " + mBTillController.getWallet().toString());
            } catch (UnreadableWalletException e) {
                Log.d(TAG, "Error reading the wallet");
            }


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