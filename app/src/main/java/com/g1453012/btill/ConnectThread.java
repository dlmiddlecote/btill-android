package com.g1453012.btill;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by dlmiddlecote on 18/02/15.
 */
public class ConnectThread extends Thread {

    private static final String TAG = "ConnectThread";
    private BluetoothSocket mSocket = null;

    private static UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final String MAC = "00:15:83:64:83:DE";
    private static final String LUKESMAC = "";
    private ConnectedThread mConnectedThread;

    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    //private BluetoothAdapter mBluetoothAdapter;

    public BluetoothSocket getSocket() {
        return mSocket;
    }

    public ConnectThread(BluetoothAdapter bluetoothAdapter) {

        BluetoothDevice device = null;
        //mBluetoothAdapter = bluetoothAdapter;

        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bt: mPairedDevices) {
            if (bt.getAddress().toString().equals(MAC) || bt.getAddress().toString().equals(LUKESMAC)) {
                device = bt;
            }
        }

        // Temporary Bluetooth Socket
        BluetoothSocket tmp = null;
        // Try to create an RFCOMM socket to the UUID given
        try {
            tmp = device.createRfcommSocketToServiceRecord(mUUID);
            Log.d(TAG, "Created Socket");
        } catch (IOException e) {
        }
        // Set the temp socket to be the socket in the thread
        mSocket = tmp;
    }

    // What to do when the ConnectThread is started
    public void run() {
/*
        // Stop discovery
        mBluetoothAdapter.cancelDiscovery();
        try {
            // Connect to socket
            mSocket.connect();
            Log.d(TAG, "Connected through Socket");
        } catch (IOException connectException) {
            try {
                Log.d(TAG, "Inside Catch");
                // If cannot connect, close the socket
                mSocket.close();
            } catch (IOException closeException) {

            }
            return;
        }
        // After Connect, start a ConnectedThread
        mConnectedThread = new ConnectedThread(mSocket);
        if (mConnectedThread != null) {
            Log.d(TAG, "Makes connected thread");
        }*/
    }

    public Future<Boolean> runFuture() {
        return pool.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mBluetoothAdapter.cancelDiscovery();
                try {
                    mSocket.connect();
                    Log.d(TAG, "Connected to Server");
                    return Boolean.TRUE;
                } catch (IOException e) {
                    try {
                        Log.e(TAG, "Couldn't connect to Server, retry...");
                        mSocket.close();
                        return Boolean.FALSE;
                    } catch (IOException f) {
                        Log.e(TAG, "Couldn't close socket");
                    }
                }
                return Boolean.FALSE;
            }
        });
    }


}
