package com.example.lab4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import java.util.ArrayList;

public class SmsReceiver extends BroadcastReceiver {
    public static final String SMS_FORWARD_BROADCAST_RECEIVER = "sms_forward_broadcast_receiver";
    public static final String SMS_MESSAGE_ADDRESS_KEY = "sms_message_address_key";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null) return;

        ArrayList<String> addresses = new ArrayList<>();

        for (Object pdu : pdus) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
            String msgBody = sms.getMessageBody();
            String sender = sms.getOriginatingAddress();

            if (msgBody != null && msgBody.toLowerCase().contains("are you ok?")) {
                addresses.add(sender);
            }
        }

        if (!addresses.isEmpty()) {
            // Đọc trạng thái Auto Response từ SharedPreferences
            SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            boolean autoResponseOn = prefs.getBoolean("auto_response", false);

            if (autoResponseOn) {
                SmsManager smsManager = SmsManager.getDefault();
                for (String addr : addresses) {
                    smsManager.sendTextMessage(addr, null, "I am fine and safe. Worry not!", null, null);
                }
            }

            // Nếu app đang chạy, gửi broadcast cho MainActivity để cập nhật UI
            if (MainActivity.isRunning) {
                Intent forwardIntent = new Intent(SMS_FORWARD_BROADCAST_RECEIVER);
                forwardIntent.putStringArrayListExtra(SMS_MESSAGE_ADDRESS_KEY, addresses);
                context.sendBroadcast(forwardIntent);
            }
        }
    }

}
