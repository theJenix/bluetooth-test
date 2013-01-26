package edu.gatech.thelastcrusade.bluetooth_test;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import edu.gatech.thelastcrusade.bluetooth_test.util.Toaster;

public class BluetoothTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test);
        
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
        this.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                    onDiscoveryStarted(adapter);
                } else if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    onDiscoveryFinished(adapter);
                } else if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                    onDeviceFound(adapter);
                }

            }
            
        }, filter);

        adapter.startDiscovery();
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
