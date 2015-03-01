package com.g1453012.btill.Bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.g1453012.btill.Shared.BTMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by dlmiddlecote on 18/02/15.
 */
public class ConnectedThread extends Thread {

    private static final String TAG = "ConnectedThread";

    private final ExecutorService pool = Executors.newFixedThreadPool(10);

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

    // When thread is started, read from socket
    public void run() {
        // TODO what we want it to do
    }

    // How to read from server
    public String read() {
        Log.d(TAG, "Inside Connected Run");
        // Set up a byte buffer
        final int BUFFER_SIZE = 16384;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;

        // Read from the input stream, and convert bytes to a string

        Log.d(TAG, "Message?");
        try {
            bytes = mInStream.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String message = new String(buffer, 0, bytes);
        Log.d(TAG, message + " read " + bytes + " bytes.");
        return message;
    }

    public Future<String> readFuture() {
        return pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Log.d(TAG, "Inside Connected Run Future");

                byte[] buffer = new byte[990];
                int bytesRead = 990;
                String message = new String();

                while (bytesRead==990) {
                    try {
                        bytesRead = mInStream.read(buffer);
                        message += new String(buffer, 0, bytesRead);
                    }
                    catch (IOException ex) {
                        Log.e(TAG, "Error reading from inputstream");
                    }
                }
                Log.d(TAG, "Received: " + message);
                return message;
            }
        });
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

    public boolean write(BTMessage message) {
        try {
            // Take to input string and convert to bytes, and send
            byte[] messageBytes = message.getBytes();
            int remaining = messageBytes.length;
            for (int i = 0; i <= messageBytes.length / 990; i++) {
                mOutStream.write(messageBytes, i * 990, Math.min(990, remaining));
                mOutStream.flush();
                remaining -= 990;
            }
            Log.i(TAG, "Written BTMessage");
            return true;
        } catch (IOException e) {
            Log.i(TAG, "Couldn't Write BTMessage");
            return false;
        }
    }

    public Future<Boolean> writeFuture(final BTMessage message) {
        return pool.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    byte[] messageBytes = message.getBytes();
                    int remaining = messageBytes.length;

                    for (int i = 0; i <= messageBytes.length / 990; i++) {
                        Log.d(TAG, "Writing " + Math.min(990, remaining) + " bytes");
                        mOutStream.write(messageBytes, i * 990, Math.min(990, remaining));
                        mOutStream.flush();
                        remaining -= 990;
                    }
                    return Boolean.TRUE;

                } catch (IOException e) {
                    Log.e(TAG, "Couldn't write inside Future");
                    return Boolean.FALSE;
                }
            }
        });
    }


    // Cancel ConnectedThread method
    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {

        }
    }
}
