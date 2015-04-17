package com.g1453012.btill.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by dlmiddlecote on 18/02/15.
 */
public class ConnectThread extends Thread {

    public static final String ANDYSMAC = "00:03:C9:EA:E1:C7";
    public static final String LUKESMAC = "48:5D:60:FC:B0:46";
    public static final String DANSMAC = "00:15:83:64:83:DE";
    public static final String PIMAC = "00:15:83:64:94:62";
    private static final String TAG = "ConnectThread";
    private final static ExecutorService poolStatic = Executors.newFixedThreadPool(10);
    private static UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static volatile BluetoothDevice mBluetoothDevice = null;
    private final ExecutorService pool = Executors.newFixedThreadPool(10);
    private BluetoothSocket mSocket = null;
    private ConnectedThread mConnectedThread;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    //private BluetoothAdapter mBluetoothAdapter;

    public ConnectThread() {

        // Temporary Bluetooth Socket
        BluetoothSocket tmp = null;
        // Try to create an RFCOMM socket to the UUID given
        try {
            tmp = mBluetoothDevice.createRfcommSocketToServiceRecord(mUUID);
            Log.d(TAG, "Created Socket");
        } catch (IOException e) {
        }
        // Set the temp socket to be the socket in the thread
        mSocket = tmp;

    }

    public static BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }

    public static void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        mBluetoothDevice = bluetoothDevice;
    }

    public BluetoothSocket getSocket() {
        return mSocket;
    }

    // What to do when the ConnectThread is started
    public void run() {
    }

    public Future<Boolean> runFuture() {
        return pool.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    mBluetoothAdapter.cancelDiscovery();
                    mSocket.connect();
                    Log.d(TAG, "Connected to Server");
                    return Boolean.TRUE;
                } catch (IOException e) {
                    //try {
                    Log.e(TAG, "Couldn't connect to Server, retry...");
                    //mSocket.close();
                    return Boolean.FALSE;
                    //} catch (IOException f) {
                    //    Log.e(TAG, "Couldn't close socket");
                    //}
                }
                //return Boolean.FALSE;
            }
        });
    }

}
