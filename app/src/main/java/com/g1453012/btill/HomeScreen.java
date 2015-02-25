package com.g1453012.btill;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
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

import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.UnreadableWalletException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class HomeScreen extends Activity {

    private final static String TAG = "HomeScreen";

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 2;

    private WalletKitThread mWalletKitThread;

    private BTillController mBTillController = null;

    private File mFile;

    private File mCheckpointFile;

    FileInputStream mCheckpointStream;

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private final String filePrefix = "Bitcoin-test";
    private final TestNet3Params mNetParams = TestNet3Params.get();

    private Bundle mSavedInstanceState;

    public BTillController getBTillController() {
        return mBTillController;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBTillController = new BTillController();
        mSavedInstanceState = savedInstanceState;


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
        ConnectThread mConnectThread = new ConnectThread(mBluetoothAdapter);
        try {
            while (connectionTries < MAX_ATTEMPTS) {
                Future<Boolean> connectionFuture = mConnectThread.runFuture();
                if (connectionFuture.get()) {
                    mBTillController.setBluetoothSocket(mConnectThread.getSocket());
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
        mWalletKitThread = new WalletKitThread(getApplicationContext(), mBTillController, mFile);
        mWalletKitThread.start();

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
            //Log.d(TAG, "Wallet: " + mBTillController.getWallet().toString());
        } catch (UnreadableWalletException e) {
            Log.d(TAG, "Error reading the wallet");
        }
    }

    private void generateMenuView() {
        setContentView(R.layout.activity_home_screen);
        if (mSavedInstanceState==null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Fragment fragment = new MenuFragment();
            transaction.add(R.id.fragmentFrame, fragment);
            transaction.commit();
        }
    }

    public void generateServerNotFoundView(int type) {
        setContentView(R.layout.server_not_found_home);

        TextView mBalanceTotal = (TextView) findViewById(R.id.serverNotFoundBalanceAmount);
        mBalanceTotal.setText(mBTillController.getWallet().getBalance(Wallet.BalanceType.ESTIMATED).toFriendlyString());

        ImageView mBalanceQR = (ImageView) findViewById(R.id.serverNotFoundQR);
        Bitmap mBitmap = mBTillController.generateQR();
        mBalanceQR.setImageBitmap(mBitmap);
        mBalanceQR.setVisibility(View.VISIBLE);

        TextView mWalletAddress = (TextView) findViewById(R.id.serverNotFoundWalletAddress);
        mWalletAddress.setText(mBTillController.getWallet().currentReceiveAddress().toString());

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
        try {
            mBTillController.getWallet().saveToFile(mFile);
            Log.d(TAG, "Wallet Saved");
        } catch (IOException e) {
            Log.e(TAG, "Error saving file");
        }
    }
}