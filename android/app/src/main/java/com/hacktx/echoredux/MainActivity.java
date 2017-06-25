package com.hacktx.echoredux;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private MainAcvitiyReceiver mainAcvitiyReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestSmsPermission();
    }

    @Override
    public void onResume() {
        super.onResume();
        mainAcvitiyReceiver = new MainAcvitiyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.hacktx.echoredux.SMS_RECEIVED");
        registerReceiver(mainAcvitiyReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mainAcvitiyReceiver != null) {
            unregisterReceiver(mainAcvitiyReceiver);
            mainAcvitiyReceiver = null;
        }
    }

    private void requestSmsPermission() {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    class MainAcvitiyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: Tell server that we've received the message via SMS

            // TODO: Generate QR code

            // TODO: Tell server that we've generated the QR code

            // TODO: Upload the image to Imgur

            // TODO: Tell server that we've uploaded the QR code

            // TODO: Download the image from Imgur

            // TODO: Tell server that we've downloaded the QR code

            // TODO: Parse the QR code

            // TODO: Tell server that we've parsed the QR code

            // TODO: Use Google Translate to translate back to English

            // TODO: Tell server that we've translated back to English

            // TODO: TTS for Alexa
        }
    }
}
