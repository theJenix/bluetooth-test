package edu.gatech.thelastcrusade.bluetooth_test;

import java.io.IOException;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.view.Menu;
import edu.gatech.thelastcrusade.bluetooth_test.util.Toaster;

public class BluetoothTestActivity extends Activity {
    private final BluetoothServerSocket mmServerSocket;
    private final String HOST_NAME = "Connery's party";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test);
        
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
        
        try {
            tmp = adapter.listenUsingRfcommWithServiceRecord(HOST_NAME, UUID.fromString(this.getString(R.string.app.uuid)));
        } catch (IOException e)
        {
            
        }
        
        mmServerSocket = tmp;

    }

    private void enableDiscovery() {
        Intent discoverableIntent = new
        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    protected void onDeviceFound(BluetoothAdapter adapter) {
        
        
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
