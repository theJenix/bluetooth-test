package edu.gatech.thelastcrusade.bluetooth_test;

import java.io.IOException;
import java.util.UUID;

import edu.gatech.thelastcrusade.bluetooth_test.util.Toaster;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class BluetoothTestServerActivity extends Activity {

    // for displaying multiple client messages in one toast
    private StringBuilder message = new StringBuilder();
    private Object msgMutex = new Object();

    private final String TAG = "BluetoothTestServerActivity";
    private BluetoothServerSocket mmServerSocket;
    private final String HOST_NAME = "Sean Connery's party";
    protected MessageThread connectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test_server);

        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            adapter.enable();
            if (!adapter.isEnabled()) {
                Toaster.tToast(this, "Unable to enable bluetooth adapter.");
                return;
            }
        }

        enableDiscovery();
        
        final Handler handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == MessageThread.MESSAGE_READ) {
                    onReadMessage(msg.obj.toString(), msg.arg1);
                    return true;
                }
                return false;
            }
        });

        try {
            mmServerSocket = adapter.listenUsingRfcommWithServiceRecord(
                                HOST_NAME,
                                UUID.fromString(this.getString(R.string.app_uuid))
                             );
            if(mmServerSocket != null){
                Log.i(TAG, "Server Socket Made");
            } else {
                Log.w(TAG, "Server Socket NOT made");
            }

            new Thread() {
                public void run() {
                    BluetoothSocket socket = null;
                    try {
                        while(true) {
                            // If a connection was accepted
                            socket = mmServerSocket.accept(); //blocking call
                            if (socket != null) {
                                Log.i(TAG, "Connection accepted");
                                connectedThread = new MessageThread(socket, handler);
                                connectedThread.start();
                            }
                        }
                    } catch (IOException e) {
                        Log.w(TAG, e.getStackTrace().toString());
                    }
                };
            }.start();
        } catch (IOException e){
            Log.w(TAG, e.getStackTrace().toString());
        }
    }

    protected void onReadMessage(String string, int arg1) {
        synchronized(msgMutex) {
            if (message.length() > 0) {
                message.append("\n");
            } else {
                startDelayedDisplayMessage();
            }
            message.append(string);
        }
    }

    private void startDelayedDisplayMessage() {
        int delayMillis = 2000; /*2s*/
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                synchronized(msgMutex) {
                    Toaster.tToast(BluetoothTestServerActivity.this, message.toString());
                    message = new StringBuilder();
                }
            }
            
        }, delayMillis);
    }

    private void enableDiscovery() {
        Intent discoverableIntent = new
        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    protected void onDeviceFound(BluetoothAdapter adapter) {
        // TODO Auto-generated method stub        
        
    }

    protected void onDiscoveryFinished(BluetoothAdapter adapter) {
        // TODO Auto-generated method stub
        
    }

    protected void onDiscoveryStarted(BluetoothAdapter adapter) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(
                R.menu.activity_bluetooth_test_server_activitiy, menu);
        return true;
    }

}
