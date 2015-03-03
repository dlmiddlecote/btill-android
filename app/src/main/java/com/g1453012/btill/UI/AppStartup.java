package com.g1453012.btill.UI;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.g1453012.btill.BTillController;
import com.g1453012.btill.Bitcoin.WalletKitThread;
import com.g1453012.btill.Bluetooth.ConnectThread;
import com.g1453012.btill.PersistentParameters;
import com.g1453012.btill.R;
import com.g1453012.btill.UI.HomeScreenFragments.Order.OrderFragment;

import org.bitcoinj.core.Wallet;
import org.bitcoinj.store.UnreadableWalletException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class AppStartup extends FragmentActivity {

    private final static String TAG = "HomeScreen";

    private PersistentParameters params = new PersistentParameters();
    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 2;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final String filePrefix = "Bitcoin-test";

    private boolean blockLoadingView = false;


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

        generateLoadingView();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBroadcastReceiver, filter);

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
            setWallet();
            generateMenuView();
        } else {
            Log.e(TAG, "Connection Timed out...");
            setWallet();
            generateServerNotFoundView();
        }
    }

    private void setWallet() {
        File file = new File(this.getExternalFilesDir("/wallet/"), filePrefix + ".wallet");
        WalletKitThread walletKitThread = new WalletKitThread(getApplicationContext(), file);
        walletKitThread.start();
        File checkpointFile = new File(this.getExternalFilesDir("/wallet/"), "checkpoints");
        try {
            FileInputStream checkpointStream = new FileInputStream(checkpointFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Couldn't find checkpoint file");
        }

        try {
            params.setWallet(Wallet.loadFromFile(file));
            Log.d(TAG, "Loaded Wallet");
            Log.d(TAG, "Wallet Address: " + params.getWallet().currentReceiveAddress().toString());
            Log.d(TAG, "Wallet Balance: " + params.getWallet().getBalance().toFriendlyString());
            //Log.d(TAG, "Wallet: " + mBTillController.getWallet().toString());
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

        setContentView(R.layout.server_not_found_home);

        TextView mBalanceTotal = (TextView) findViewById(R.id.serverNotFoundBalanceAmount);
        mBalanceTotal.setText(params.getWallet().getBalance(Wallet.BalanceType.ESTIMATED).toFriendlyString());

        ImageView mBalanceQR = (ImageView) findViewById(R.id.serverNotFoundQR);
        Bitmap mBitmap = BTillController.generateQR(params.getWallet());
        mBalanceQR.setImageBitmap(mBitmap);
        mBalanceQR.setVisibility(View.VISIBLE);

        TextView mWalletAddress = (TextView) findViewById(R.id.serverNotFoundWalletAddress);
        mWalletAddress.setText(params.getWallet().currentReceiveAddress().toString());

        Button mServerNotFoundButton = (Button) findViewById(R.id.serverNotFoundRetryButton);
        mServerNotFoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBluetoothEnabled();
                mBluetoothAdapter.startDiscovery();
            }
        });

    }

    private void generateLoadingView() {

        setContentView(R.layout.home);

        Timer timer = new Timer("timer");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                setWallet();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!blockLoadingView) {
                            generateServerNotFoundView();
                        }
                    }
                });
            }
        }, 5000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        File file = new File(this.getExternalFilesDir("/wallet/"), filePrefix + ".wallet");
        try {
            if (params.getSocket() != null) {
                params.getSocket().close();
            }
            unregisterReceiver(mBroadcastReceiver);
            params.getWallet().cleanup();
            params.getWallet().saveToFile(file);
            Log.d(TAG, "Wallet Saved");
        } catch (IOException e) {
            Log.e(TAG, "Error saving file");
        }
    }
}