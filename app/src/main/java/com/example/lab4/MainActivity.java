package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private PowerStateChangeReceiver powerReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register the receiver programmatically
        powerReceiver = new PowerStateChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(powerReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister to avoid leaks
        if (powerReceiver != null) {
            unregisterReceiver(powerReceiver);
        }
    }
}