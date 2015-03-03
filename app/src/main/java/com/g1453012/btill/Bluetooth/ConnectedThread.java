package com.g1453012.btill.Bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.g1453012.btill.Shared.BTMessage;

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


    public Future<String> readFuture() {
        return pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Log.d(TAG, "Inside Connected Run Future");
                // Set up a byte buffer
                byte[] buffer = new byte[1024];
                int bytes;
                boolean read = true;
                int bytesTotal = 0;
                String message = new String();
                while (read) {
                    bytes = mInStream.read(buffer);
                    bytesTotal += bytes;
                    message += new String(buffer, 0, bytes);
                    Thread.sleep(100);
                    if (bytes < 990) {
                        read = false;
                    }
                }

                Log.d(TAG, "Read " + bytesTotal + " bytes: " + message);
                return message;
            }
        });
    }

    public Future<Boolean> writeFuture(final BTMessage message) {
        return pool.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    byte[] messageBytes = message.getBytes();
                    int remaining = messageBytes.length;
                    for (int i = 0; i <= (messageBytes.length / 990); i++) {
                        mOutStream.write(messageBytes, i * 990, Math.min(990, remaining));
                        mOutStream.flush();
                        remaining -= 990;
                        Thread.sleep(100);
                    }
                    Log.d(TAG, "Written BTMessage");
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
