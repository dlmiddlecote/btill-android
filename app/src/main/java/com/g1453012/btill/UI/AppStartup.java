package com.g1453012.btill.UI;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.g1453012.btill.Bitcoin.WalletKitThread;
import com.g1453012.btill.Bluetooth.ConnectThread;
import com.g1453012.btill.PersistentParameters;
import com.g1453012.btill.R;
import com.g1453012.btill.UI.HomeScreenFragments.Order.OrderFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Order.SearchingForShopFragment;
import com.g1453012.btill.UI.HomeScreenFragments.Order.ServerNotFoundFragment;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
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

    private PersistentParameters params;
    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 2;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final String filePrefix = "Bitcoin-test";

    com.g1453012.btill.Shared.Menu mMenu;
    private boolean blockLoadingView = false;

    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    private boolean registered = false;
    private boolean walletSet = false;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        super.onCreate(savedInstanceState);

        params = new PersistentParameters(this, "receipt.store");

        beaconManager.bind(this);

        generateLoadingView();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBroadcastReceiver, filter);
        registered = true;

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.no_bluetooth, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else {
            mBluetoothAdapter.startDiscovery();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                if (mBluetoothAdapter.startDiscovery()) {
                    Log.d("DISCOVERY", "STARTED DISCOVERY");
                }
            }
            else {
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
        File file = new File(this.getExternalFilesDir("/wallet/"), filePrefix + ".wallet");
        WalletKitThread walletKitThread = new WalletKitThread(getApplicationContext(), file);
        Log.d(TAG, "Starting wallet kit thread");
        walletKitThread.start();
        /*File checkpointFile = new File("checkpoints");
        try {
            FileInputStream checkpointStream = new FileInputStream(checkpointFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Couldn't find checkpoint file");
        }*/

        try {
            params.setWallet(Wallet.loadFromFile(file));
            Log.d(TAG, "Loaded Wallet");
            //Log.d(TAG, "Wallet Address: " + params.getWallet().currentReceiveAddress().toString());
            Log.d(TAG, "Wallet Balance: " + params.getWallet().getBalance().toFriendlyString());
            Log.d(TAG, "Wallet: " + params.getWallet().toString());
        } catch (UnreadableWalletException e) {
            Log.d(TAG, "Error reading the wallet");
        }

    }

    private void generateMenuView() {
        blockLoadingView = true;
        setContentView(R.layout.activity_home_screen);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = OrderFragment.newInstance(params);
        transaction.add(R.id.fragmentFrame, fragment);
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
        if (beaconManager.isBound(this)) {
            beaconManager.setBackgroundMode(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        File file = new File(this.getExternalFilesDir("/wallet/"), filePrefix + ".wallet");
        try {
            if (params.getSocket() != null) {
                params.getSocket().close();
            }
            if (registered) {
                unregisterReceiver(mBroadcastReceiver);
            }
            beaconManager.unbind(this);
            if (params.getWallet() != null) {
                params.getWallet().cleanup();
                //params.getWallet().saveToFile(file);
                //Log.d(TAG, "Wallet Saved");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error saving file");
        }
    }
}