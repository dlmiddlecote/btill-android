package com.g1453012.btill.UI.HomeScreenFragments;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
//import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
        import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.g1453012.btill.BTillController;
import com.g1453012.btill.Bluetooth.ConnectThread;
import com.g1453012.btill.PersistentParameters;
import com.g1453012.btill.R;
import com.g1453012.btill.Bitcoin.WalletKitThread;
import com.g1453012.btill.UI.HomeScreenFragments.Order.OrderFragment;

import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.UnreadableWalletException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class AppStartup extends FragmentActivity {

    private final static String TAG = "HomeScreen";

    private PersistentParameters params = new PersistentParameters();

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 2;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final String filePrefix = "Bitcoin-test";
    private final TestNet3Params mNetParams = TestNet3Params.get();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            onBluetoothEnabled();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                onBluetoothEnabled();
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
        ConnectThread connectThread = new ConnectThread(mBluetoothAdapter);
        try {
            while (connectionTries < MAX_ATTEMPTS) {
                Future<Boolean> connectionFuture = connectThread.runFuture();
                if (connectionFuture.get()) {
                    params.setSocket(connectThread.getSocket());
                    break;
                }
                connectionTries++;
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
            Log.e(TAG, "Connection Timed out... Quitting...");
            setWallet();
            generateServerNotFoundView(0);
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
        setContentView(R.layout.activity_home_screen);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = OrderFragment.newInstance(params);
        transaction.add(R.id.fragmentFrame, fragment);
        transaction.commit();
    }

    public void generateServerNotFoundView(int type) {
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
                onBluetoothEnabled();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        File file = new File(this.getExternalFilesDir("/wallet/"), filePrefix + ".wallet");
        try {
            params.getWallet().saveToFile(file);
            Log.d(TAG, "Wallet Saved");
        } catch (IOException e) {
            Log.e(TAG, "Error saving file");
        }
    }
}