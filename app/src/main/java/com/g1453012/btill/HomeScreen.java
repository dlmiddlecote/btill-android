package com.g1453012.btill;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;


public class HomeScreen extends Activity {

    private BTillController mBTillController = null;

    public BTillController getBTillController() {
        return mBTillController;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_home_screen);
        if (savedInstanceState==null){
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            Fragment fragment = new MenuFragment();
            transaction.add(R.id.fragmentFrame, fragment);
            transaction.commit();

            // Create new Controller when app loads
            mBTillController = new BTillController();
            ConnectThread mConnectThread = new ConnectThread();
            mConnectThread.start();
            mBTillController.setBluetoothSocket(mConnectThread.getSocket());


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
}