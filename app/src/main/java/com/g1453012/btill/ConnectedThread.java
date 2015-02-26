package com.g1453012.btill;

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
                final int BUFFER_SIZE = 1024;
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytes = 0;

                try {
                    bytes = mInStream.read(buffer);
                } catch (IOException e) {
                    Log.e(TAG, "Error reading from inputstream");
                }
                String readCountMessage = new String(buffer, 0, bytes);
                Integer readCount = Integer.parseInt(readCountMessage);
                String message = new String();
                bytes = 0;
                int bytesTotal = 0;
                for (int i = 0; i < readCount.intValue(); i++) {
                    try {
                        bytes = mInStream.read(buffer);
                        bytesTotal += bytes;
                        message += new String(buffer, 0, bytes);
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading from inputstream");
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
                int start = 0;
                Integer readCount = (message.getBytes().length / 990) + 1;
                int lengthLeft = message.getBytes().length;
                try {
                    mOutStream.write(readCount.toString().getBytes());
                    mOutStream.flush();
                    for (int i = 0; i < readCount.intValue(); i++) {
                        if (lengthLeft < 990) {
                            mOutStream.write(message.getBytes(), start, lengthLeft);
                            Log.d(TAG, "Wrote to server");
                            return Boolean.TRUE;
                        }
                        else {
                            mOutStream.write(message.getBytes(), start, 990);
                        }
                        mOutStream.flush();
                        start += 990;
                        lengthLeft -= 990;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Couldn't write inside Future");
                    return Boolean.FALSE;
                }
                return Boolean.FALSE;
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
