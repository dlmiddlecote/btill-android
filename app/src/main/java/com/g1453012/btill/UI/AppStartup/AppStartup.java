package com.g1453012.btill.UI.AppStartup;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.g1453012.btill.Bitcoin.WalletKitThread;
import com.g1453012.btill.Bluetooth.ConnectThread;
import com.g1453012.btill.PersistentParameters;
import com.g1453012.btill.R;
import com.g1453012.btill.UI.HomeScreenFragments.MainScreen;
import com.g1453012.btill.UI.HomeScreenFragments.Order.SearchingForShopFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Order.ServerNotFoundFragment;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.bitcoinj.core.AbstractWalletEventListener;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.store.UnreadableWalletException;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AppStartup extends FragmentActivity implements BeaconConsumer {

    private final static String TAG = "AppStartup";
    // This is what happens when a device is found
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice bt = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (bt.getAddress().toString().equals(ConnectThread.DANSMAC) || bt.getAddress().toString().equals(ConnectThread.LUKESMAC)
                        || bt.getAddress().toString().equals(ConnectThread.ANDYSMAC) || bt.getAddress().toString().equals(ConnectThread.PIMAC)) {
                    Log.d(TAG, bt.getName());
                    ConnectThread.setBluetoothDevice(bt);
                    onBluetoothEnabled();
                }
            }
        }
    };
    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 2;
    private static PersistentParameters params;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final String filePrefix = "Bitcoin-test";
    com.g1453012.btill.Shared.Menu mMenu;
    private boolean blockLoadingView = false;
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    private boolean registered = false;
    private boolean walletSet = false;

    public static void setParams(PersistentParameters param) {
        params = param;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //params = new PersistentParameters(this, "newReceipt.store", true);

        beaconManager.bind(this);

        generateLoadingView();

        /*IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBroadcastReceiver, filter);
        registered = true;*/

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.no_bluetooth, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Log.d(TAG, "Starting");
            mBluetoothAdapter.startDiscovery();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                if (mBluetoothAdapter.startDiscovery()) {
                    Log.d("DISCOVERY", "STARTED DISCOVERY");
                }
            } else {
                finish();
                return;
            }
        }
    }

    private void onBluetoothEnabled() {
        int connectionTries = 0;
        int MAX_ATTEMPTS = 25;
        Log.d(TAG, "onBluetoothEnabled");
        try {
            ConnectThread connectThread = new ConnectThread();
            while (connectionTries < MAX_ATTEMPTS) {
                if (connectThread.getBluetoothDevice() != null) {
                    Future<Boolean> connectionFuture = connectThread.runFuture();
                    if (connectionFuture.get()) {
                        params.setSocket(connectThread.getSocket());
                        break;
                    }
                    connectionTries++;
                }
            }

        } catch (InterruptedException e) {
            Log.e(TAG, "Getting the Socket was interrupted");
            finish();
        } catch (ExecutionException e) {
            Log.e(TAG, "Getting the Socket had an Execution Exception");
            finish();
        }
        if (connectionTries < MAX_ATTEMPTS) {
            //setWallet();
            generateMenuView();
        } else {
            Log.e(TAG, "Connection Timed out...");
            //setWallet();
            generateServerNotFoundView();
        }
    }

    private void setWallet() {
        final File file = new File(this.getExternalFilesDir("/wallet/"), filePrefix + ".wallet");
        WalletKitThread walletKitThread = new WalletKitThread(getApplicationContext(), file);
        Log.d(TAG, "Starting wallet kit thread");
        walletKitThread.start();

        try {
            params.setWallet(Wallet.loadFromFile(file));
            if (params.getWallet() != null) {
                Log.d(TAG, "Loaded Wallet");
                Log.d(TAG, "Wallet Balance: " + params.getWallet().getBalance().toFriendlyString());
                Log.d("WALLET", "Wallet: " + params.getWallet().toString());
            }
        } catch (UnreadableWalletException e) {
            Log.d(TAG, "Error reading the wallet");
        }

        if (params.getWallet() != null) {
            params.getWallet().addEventListener(new AbstractWalletEventListener() {
                @Override
                public void onWalletChanged(Wallet wallet) {
                    params.getBalanceFragment().needsUpdating();
                    wallet.allowSpendingUnconfirmedTransactions();
                    try {
                        wallet.cleanup();
                        wallet.saveToFile(file);
                        Log.d(TAG, "Wallet Saved");
                    } catch (IOException e) {
                        Log.e(TAG, "Error saving file");
                    }
                }

                @Override
                public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                    Coin difference = prevBalance.subtract(newBalance);
                    Log.d("COINS SENT", "Coins have been sent: " + difference.toFriendlyString());
                }
            });
        }

    }

    private void generateMenuView() {

        blockLoadingView = true;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = MainScreen.newInstance(params);
        transaction.replace(R.id.appStartupFragmentFrame, fragment);
        transaction.commit();
    }

    public void generateServerNotFoundView() {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = ServerNotFoundFragment.newInstance(params);
        transaction.replace(R.id.appStartupFragmentFrame, fragment);
        transaction.commit();

    }

    private void generateLoadingView() {

        setContentView(R.layout.home);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = SearchingForShopFragment.newInstance();
        transaction.replace(R.id.appStartupFragmentFrame, fragment);
        transaction.commit();
        if (!walletSet) {
            setWallet();
            walletSet = true;
        }

        Timer timer = new Timer("timer");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //setWallet();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!blockLoadingView) {
                            generateServerNotFoundView();
                        }
                    }
                });
            }
        }, 7000);
    }

    @Override
    public void onBeaconServiceConnect() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) {
            beaconManager.setBackgroundMode(true);
            beaconManager.setBackgroundBetweenScanPeriod(60000l);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBroadcastReceiver, filter);
        registered = true;

        if (beaconManager.isBound(this)) {
            beaconManager.setBackgroundMode(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (params.getSocket() != null) {
                params.getSocket().close();
            }
            if (registered) {
                unregisterReceiver(mBroadcastReceiver);
            }
            beaconManager.unbind(this);
        } catch (IOException e) {
            Log.e(TAG, "Error closing socket");
        }
    }
}