package com.g1453012.btill;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.UnreadableWalletException;

import java.io.File;
import java.io.IOException;


public class HomeScreen extends Activity {

    private final static String TAG = "HomeScreen";

    private BTillController mBTillController = null;

    private File mFile;

    public BTillController getBTillController() {
        return mBTillController;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_home_screen);
        if (savedInstanceState==null){
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Create new Controller when app loads
            mBTillController = new BTillController();
            ConnectThread mConnectThread = new ConnectThread();
            mConnectThread.start();
            mBTillController.setBluetoothSocket(mConnectThread.getSocket());
            mFile = new File(this.getFilesDir(), "wallet.dat");

            try {
                mBTillController.setWallet(Wallet.loadFromFile(mFile));
                Log.d(TAG, "Created Wallet Wrapper");
            } catch (UnreadableWalletException e) {
                mBTillController.setWallet(new Wallet(TestNet3Params.get()));
                Log.d(TAG, "Created Wallet");
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