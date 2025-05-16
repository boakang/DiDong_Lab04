package com.example.lab4;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static boolean isRunning = false;

    private static final int PERMISSION_REQUEST_CODE = 100;

    private BroadcastReceiver broadcastReceiver;
    private ArrayList<String> requesters = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView lvMessages;
    private Switch swAutoResponse;
    private Button btnResponse1, btnResponse2;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_AUTO_RESPONSE = "auto_response";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvMessages = findViewById(R.id.lv_messages);
        swAutoResponse = findViewById(R.id.sw_auto_response);
        btnResponse1 = findViewById(R.id.btn_response_1);
        btnResponse2 = findViewById(R.id.btn_response_2);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, requesters);
        lvMessages.setAdapter(adapter);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Đọc trạng thái auto response khi app khởi động
        boolean autoResponseOn = prefs.getBoolean(KEY_AUTO_RESPONSE, false);
        swAutoResponse.setChecked(autoResponseOn);

        // Lưu trạng thái auto response khi thay đổi
        swAutoResponse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_AUTO_RESPONSE, isChecked).apply();
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ArrayList<String> addresses = intent.getStringArrayListExtra(SmsReceiver.SMS_MESSAGE_ADDRESS_KEY);
                if (addresses != null) {
                    boolean autoResponse = prefs.getBoolean(KEY_AUTO_RESPONSE, false);
                    processReceiveAddresses(addresses, autoResponse);
                }
            }
        };

        // Xin quyền runtime nếu chưa có
        if (!hasPermissions()) {
            requestPermissions();
        }

        // Xử lý khi app bật lại từ trạng thái tắt
        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<String> addresses = intent.getStringArrayListExtra(SmsReceiver.SMS_MESSAGE_ADDRESS_KEY);
            if (addresses != null) {
                boolean autoResponse = prefs.getBoolean(KEY_AUTO_RESPONSE, false);
                processReceiveAddresses(addresses, autoResponse);
            }
        }

        btnResponse1.setOnClickListener(v -> {
            if (hasPermissions()) {
                for (String phone : requesters) {
                    sendSms(phone, "I am fine and safe. Worry not!");
                }
            } else {
                requestPermissions();
            }
        });

        btnResponse2.setOnClickListener(v -> {
            if (hasPermissions()) {
                for (String phone : requesters) {
                    sendSms(phone, "Tell my mother I love her");
                }
            } else {
                requestPermissions();
            }
        });
    }

    private void processReceiveAddresses(ArrayList<String> addresses, boolean autoResponseOn) {
        for (String addr : addresses) {
            if (!requesters.contains(addr)) {
                requesters.add(addr);
            }
        }
        adapter.notifyDataSetChanged();

        if (autoResponseOn) {
            if (hasPermissions()) {
                for (String addr : addresses) {
                    sendSms(addr, "I am fine and safe. Worry not!");
                }
            } else {
                requestPermissions();
            }
        }
    }

    private void sendSms(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(this, "Sent message to " + phoneNumber, Toast.LENGTH_SHORT).show();
    }

    private boolean hasPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (!allGranted) {
                Toast.makeText(this, "Permissions are required for this app to work!", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;

        if (broadcastReceiver != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(broadcastReceiver,
                        new IntentFilter(SmsReceiver.SMS_FORWARD_BROADCAST_RECEIVER),
                        Context.RECEIVER_NOT_EXPORTED); // <--- thêm dòng này
            } else {
                registerReceiver(broadcastReceiver,
                        new IntentFilter(SmsReceiver.SMS_FORWARD_BROADCAST_RECEIVER));
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        unregisterReceiver(broadcastReceiver);
    }
}
