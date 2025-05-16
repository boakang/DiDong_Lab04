package com.example.lab4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("BootReceiver", "Device rebooted. Ready to receive SMS.");

            // Nếu bạn có logic cần khởi động lại receiver động hoặc service, thêm ở đây.
            // Ví dụ: khởi động một service nếu cần.
            // Intent serviceIntent = new Intent(context, YourService.class);
            // context.startForegroundService(serviceIntent);
        }
    }
}
