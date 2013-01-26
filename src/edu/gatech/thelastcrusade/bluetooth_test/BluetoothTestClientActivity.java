package edu.gatech.thelastcrusade.bluetooth_test;

import java.io.IOException;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import edu.gatech.thelastcrusade.bluetooth_test.util.Toaster;

public class BluetoothTestClientActivity extends Activity {

    private ConnectThread thread;
    protected ConnectedThread connectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test_client);
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            adapter.enable();
            if (!adapter.isEnabled()) {
                Toaster.tToast(this, "Unable to enable bluetooth adapter.");
                return;
            }
        }
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(ConnectThread.ACTION_CONNECTED);
        this.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                    onDiscoveryStarted(adapter);
                } else if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    onDiscoveryFinished(adapter);
                } else if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                    onDeviceFound(adapter, intent);
                } else if (intent.getAction().equals(ConnectThread.ACTION_CONNECTED)) {
                    onConnected(adapter, intent);
                }
            }
            
        }, filter);

        adapter.startDiscovery();
    }

    protected void onConnected(BluetoothAdapter adapter, Intent intent) {
//       this.thread.write("Hello, Reid"); 
    }

    protected void onDeviceFound(BluetoothAdapter adapter, Intent intent) {
        // Get the BluetoothDevice object from the Intent
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        Toaster.tToast(this, device.getName() + "\n" + device.getAddress());
        // Cancel discovery because it will slow down the connection
        adapter.cancelDiscovery();
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
        try {
            this.thread = new ConnectThread(this, device) {
                @Override
                public void onConnected(BluetoothSocket socket) {
                    BluetoothTestClientActivity.this.connectedThread =
                            new ConnectedThread(socket, handler);
                }
            };
            this.thread.run();
        } catch (IOException e) {
            e.printStackTrace();
            Toaster.tToast(this, "Unable to create ConnectThread to connect to server");
        }
    }

    protected void onReadMessage(String string, int arg1) {
        Toaster.tToast(this, string);
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
        getMenuInflater().inflate(R.menu.activity_bluetooth_test_client, menu);
        return true;
    }

}
