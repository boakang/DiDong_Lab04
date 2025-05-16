package com.example.lab4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import android.content.SharedPreferences;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                StringBuilder smsBuilder = new StringBuilder();

                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    String sender = smsMessage.getDisplayOriginatingAddress();
                    String message = smsMessage.getMessageBody();

                    // Lưu vào chuỗi
                    smsBuilder.append("From: ").append(sender).append("\n")
                            .append("Message: ").append(message).append("\n\n");

                    // Hiển thị Toast
                    Toast.makeText(context, "From: " + sender + "\nMessage: " + message, Toast.LENGTH_LONG).show();

                    // Hiển thị Notification
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "sms_channel")
                            .setSmallIcon(android.R.drawable.ic_dialog_email)
                            .setContentTitle("New SMS from " + sender)
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel("sms_channel", "SMS Notifications", NotificationManager.IMPORTANCE_HIGH);
                        notificationManager.createNotificationChannel(channel);
                    }

                    notificationManager.notify(0, builder.build());
                }

                // Lưu tin nhắn vào SharedPreferences
                SharedPreferences prefs = context.getSharedPreferences("sms_prefs", Context.MODE_PRIVATE);
                prefs.edit().putString("last_sms", smsBuilder.toString()).apply();
            }
        }
    }
}
