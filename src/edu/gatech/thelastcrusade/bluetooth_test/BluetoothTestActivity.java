package edu.gatech.thelastcrusade.bluetooth_test;

import java.io.IOException;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import edu.gatech.thelastcrusade.bluetooth_test.util.Toaster;

public class BluetoothTestActivity extends Activity {
    private final String TAG = "Bluetooth_Host";
    private BluetoothServerSocket mmServerSocket;
    private final String HOST_NAME = "Connery's party";
    protected ConnectedThread connectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test);
        Log.w(TAG, "Create Called");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.w(TAG, "Resume Called");
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;

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
                if (msg.what == ConnectedThread.MESSAGE_READ) {
                    onReadMessage(msg.obj.toString(), msg.arg1);
                    return true;
                }
                return false;
            }
        });
        Log.w(TAG, "Handler Made");
        try {
            tmp = adapter.listenUsingRfcommWithServiceRecord(HOST_NAME, UUID.fromString(this.getString(R.string.app_uuid)));
            mmServerSocket = tmp;
            if(tmp != null){
                Log.w(TAG, "Server Socket Made");
            }

            BluetoothSocket socket = null;
            // If a connection was accepted
            socket = mmServerSocket.accept();
            if (socket != null) {
                Log.w(TAG, "Connection accepted");
                connectedThread = new ConnectedThread(socket, handler);
                connectedThread.run();
            }
        } catch (IOException e){ }
    }

    protected void onReadMessage(String string, int arg1) {
        Toaster.tToast(this, string);
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
        getMenuInflater().inflate(R.menu.activity_bluetooth_test, menu);
        return true;
    }

}
