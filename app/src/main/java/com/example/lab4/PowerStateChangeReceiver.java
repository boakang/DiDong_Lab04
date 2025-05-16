package com.example.lab4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class PowerStateChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        String action = intent.getAction();
        Log.d("PowerReceiver", "Received action: " + action);

        if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
            Toast.makeText(context, "Power connected", Toast.LENGTH_SHORT).show();
        } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
            Toast.makeText(context, "Power disconnected", Toast.LENGTH_SHORT).show();
        }
    }
}
