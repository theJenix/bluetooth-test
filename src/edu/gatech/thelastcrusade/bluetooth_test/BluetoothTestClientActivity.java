package edu.gatech.thelastcrusade.bluetooth_test;

import java.io.IOException;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.gatech.thelastcrusade.bluetooth_test.util.Toaster;

public class BluetoothTestClientActivity extends Activity {

    private ConnectThread   connectThread;
    private ConnectedThread messageThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test_client);
        
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        Button button = (Button)this.findViewById(R.id.button0);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((Button)findViewById(R.id.button0)).setEnabled(false);
                adapter.startDiscovery();
            }
        });
        button = (Button)this.findViewById(R.id.button1);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onHelloButtonClicked();
            }
        });

        registerReceivers(adapter);
        
        try {
            checkAndEnableBluetooth(adapter);
        } catch (BluetoothNotEnabledException e) {
            Toaster.tToast(this, "Unable to enable bluetooth adapter.");
            e.printStackTrace();
            return;
        }
    }

    protected void onHelloButtonClicked() {
        //initial test message
        this.messageThread.write("Hello, Reid".getBytes()); 
    }

    /**
     * Check if bluetooth is enabled, and if not, enable it. 
     * 
     * @param adapter
     * @throws BluetoothNotEnabledException 
     */
    private void checkAndEnableBluetooth(BluetoothAdapter adapter) throws BluetoothNotEnabledException {
        if (!adapter.isEnabled()) {
            adapter.enable();
            if (!adapter.isEnabled()) {
                throw new BluetoothNotEnabledException();
            }
        }
    }

    /**
     * Register receivers for the intent actions used to establish and manage the bluetooth connection
     * 
     * @param adapter
     */
    private void registerReceivers(final BluetoothAdapter adapter) {
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
    }

    protected void onConnected(BluetoothAdapter adapter, Intent intent) {
        Handler handler = new Handler(new Handler.Callback() {
            
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == ConnectedThread.MESSAGE_READ) {
                    onReadMessage(msg.obj.toString(), msg.arg1);
                    return true;
                }
                return false;
            }
        });
        
        //create the message thread, which will be responsible for reading and writing messages
        this.messageThread = new ConnectedThread(this.connectThread.getSocket(), handler);
        this.messageThread.run();
    }

    protected void onDeviceFound(BluetoothAdapter adapter, Intent intent) {
        // Get the BluetoothDevice object from the Intent
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        Toaster.tToast(this, device.getName() + "\n" + device.getAddress());
        // Cancel discovery because it will slow down the connection
        adapter.cancelDiscovery();
        
        try {
            this.connectThread = new ConnectThread(this, device);
            this.connectThread.run();
        } catch (IOException e) {
            e.printStackTrace();
            Toaster.tToast(this, "Unable to create ConnectThread to connect to server");
        }
    }

    protected void onReadMessage(String string, int arg1) {
        Toaster.tToast(this, string);
    }

    protected void onDiscoveryFinished(BluetoothAdapter adapter) {
        ((Button)findViewById(R.id.button0)).setEnabled(true);
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
