package com.hacktx.echoredux;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import net.glxn.qrgen.android.QRCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private MainActivityReceiver mainAcvitiyReceiver;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestSmsPermission();
    }

    @Override
    public void onResume() {
        super.onResume();
        mainAcvitiyReceiver = new MainActivityReceiver();
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

    @Override
    protected void onStop() {
        super.onStop();

        if (tts != null) {
            tts.shutdown();
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

    class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            sendStatusToServer("Received SMS message", "Thanks, Twilio!", 0, intent.getStringExtra("text"), 5);

            Log.i(TAG, "Generating QR code...");
            String message = intent.getStringExtra("text");
            QRCode qrCode = QRCode.from(message).withSize(1000, 1000);
            Bitmap bitmap = qrCode.bitmap();
            ((ImageView) findViewById(R.id.qrCode)).setImageBitmap(bitmap);

            sendStatusToServer("Generated QR code", "They're the future!", 1, qrCode.toString(), 5);

            saveToInternalStorage(bitmap);

            sendStatusToServer("Persisting QR code", "Stabilize memory...", 2, bitmap.toString(), 5);

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("dir", Context.MODE_PRIVATE);
            Bitmap bitmap2 = loadImageFromStorage(directory.toString());

            sendStatusToServer("Retrieved QR code", "Looks like it's where we left it.", 3, directory.toString() + "/qr.jpg", 5);

            Log.i("TAG", "Parsing QR code...");

            int width = bitmap2.getWidth(), height = bitmap2.getHeight();
            int[] pixels = new int[width * height];
            bitmap2.getPixels(pixels, 0, width, 0, 0, width, height);
            bitmap2.recycle();
            bitmap2 = null;
            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
            MultiFormatReader reader = new MultiFormatReader();
            Result result = null;
            try {
                result = reader.decode(bBitmap);
                Log.i(TAG, "Result " + result.getText());
            } catch (NotFoundException e) {
                e.printStackTrace();
            }

            if (result == null) {
                throw new RuntimeException("Unable to parse QR code!");
            }

            final String textToTranslate = result.getText();

            sendStatusToServer("Parsed QR code", "Look at us go!", 4, textToTranslate, 5);

            new Thread(new Runnable() {
                public void run() {
                    sendStatusToServer("Talking to Alexa", "Better listen up!", 4, "Alexa, tell Tweet Bot to Tweet " + textToTranslate, 5);
                    speak(textToTranslate);
                }
            }).start();
        }
    }

    private void sendStatusToServer(final String title, final String message, final int id, final String data, final int sleepSeconds) {
        new Thread(new Runnable() {
            public void run() {
                HttpURLConnection httpcon;
                String url = "http://echov2.herokuapp.com/update";
                JSONObject root = new JSONObject();
                try {
                    root.put("index", id);
                    root.put("name", title);
                    root.put("description", message);
                    root.put("data", data);
                    root.put("img", "https://cnet4.cbsistatic.com/img/QJcTT2ab-sYWwOGrxJc0MXSt3UI=/2011/10/27/a66dfbb7-fdc7-11e2-8c7c-d4ae52e62bcc/android-wallpaper5_2560x1600_1.jpg");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String data = root.toString();
                try {
                    httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
                    httpcon.setDoOutput(true);
                    httpcon.setRequestProperty("Content-Type", "application/json");
                    httpcon.setRequestProperty("Accept", "application/json");
                    httpcon.setRequestMethod("POST");
                    httpcon.connect();

                    OutputStream os = httpcon.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(data);
                    writer.close();
                    os.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream(), "UTF-8"));

                    String line = null;
                    StringBuilder sb = new StringBuilder();

                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(sleepSeconds * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("dir", Context.MODE_PRIVATE);
        File mypath = new File(directory, "qr.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private Bitmap loadImageFromStorage(String path) {
        try {
            File f = new File(path, "qr.jpg");
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void speak(final String str) {
        tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Log.i(TAG, "Saying \"Alexa, tell Tweet Bot to Tweet " + str + "\"");
                    tts.speak("Alexa, tell Tweet Bot to Tweet " + str, TextToSpeech.QUEUE_FLUSH, null, hashCode() + "");

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    tts.speak("Yes", TextToSpeech.QUEUE_FLUSH, null, hashCode() + "");
                } else {
                    throw new RuntimeException("Failed to init TextToSpeech!");
                }
            }
        });
    }
}
