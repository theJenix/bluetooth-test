package edu.gatech.thelastcrusade.bluetooth_test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class ConnectedThread extends Thread {
    public static final int MESSAGE_READ = 1;

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
 
    private int   messageNumber = 0;
    private Handler mmHandler;
    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSocket  = socket;
        mmHandler = handler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
 
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }
 
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }
 
    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
 
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                //NOTE: for now, assume all strings
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write(buffer, 0, bytes);
                this.messageNumber++; //one more message
                mmHandler.obtainMessage(MESSAGE_READ, this.messageNumber, 0, baos.toString())
                        .sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
        //before we exit, notify the launcher thread that the connection is dead
//        notifyDisconnect();
    }
 
    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }
 
    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            
        } finally {
            //before we exit, notify the launcher thread that the connection is dead
//          notifyDisconnect();
        }
    }
}