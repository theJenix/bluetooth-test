package edu.gatech.thelastcrusade.bluetooth_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class BluetoothTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test);
        
        ((Button)findViewById(R.id.btn_host)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                transitionTo(BluetoothTestServerActivity.class);
            }
        });

        ((Button)findViewById(R.id.btn_fan)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                transitionTo(BluetoothTestClientActivity.class);
            }
        });
    }

    protected void transitionTo(Class<? extends Activity> activityClass) {
        Intent intent = new Intent();
        intent.setClass(this, activityClass);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_bluetooth_test, menu);
        return true;
    }

}
