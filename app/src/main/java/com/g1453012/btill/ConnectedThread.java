package com.g1453012.btill;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by dlmiddlecote on 18/02/15.
 */
public class ConnectedThread extends Thread {

    private static final String TAG = "ConnectedThread";

   // New Socket
    private final BluetoothSocket mSocket;

    // New Input Stream
    private final InputStream mInStream;

    // New Output Stream
    private final OutputStream mOutStream;


    // ConnectedThread Constructor
    public ConnectedThread(BluetoothSocket socket) {
        mSocket = socket;
        // Temporary Input/Outputs
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {

        }
        // Set In/Output streams to be temporary ones created
        mInStream = tmpIn;
        mOutStream = tmpOut;
    }

    public ConnectedThread get() {
        return this;
    }

    // When thread is started, read from socket
    public void run() {
        // TODO what we want it to do
    }

    // How to read from server
    public String read() {
        Log.d(TAG, "Inside Connected Run");
        // Set up a byte buffer
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;

        // Read from the input stream, and convert bytes to a string

        Log.d(TAG, "Message?");
        try {
            bytes = mInStream.read(buffer, bytes, BUFFER_SIZE - bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String message = new String(buffer, 0, bytes);
        Log.d(TAG, message);
        return message;
    }


    // Write to output stream method
    public boolean write(String s) {
        try {
            // Take to input string and convert to bytes, and send
            mOutStream.write(s.getBytes());
            Log.i(TAG, "Written");
            return true;
        } catch (IOException e) {
            Log.i(TAG, "Couldn't Write.");
            return false;
        }
    }


    // Cancel ConnectedThread method
    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {

        }
    }
}
