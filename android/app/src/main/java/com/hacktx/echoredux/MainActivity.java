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
import com.gtranslate.Language;
import com.gtranslate.Translator;

import net.glxn.qrgen.android.QRCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
            // TODO: Tell server that we've received the message via SMS

            // TODO: Generate QR code
            Log.i(TAG, "Generating QR code...");
            String message = intent.getStringExtra("text");
            QRCode qrCode = QRCode.from(message).withSize(1000, 1000);
            Bitmap bitmap = qrCode.bitmap();
            ((ImageView) findViewById(R.id.qrCode)).setImageBitmap(bitmap);

            // TODO: Tell server that we've generated the QR code

            // TODO: Save the image to disk
            saveToInternalStorage(bitmap);

            // TODO: Tell server that we've saved the QR code

            // TODO: Read the image from disk
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("dir", Context.MODE_PRIVATE);
            Bitmap bitmap2 = loadImageFromStorage(directory.toString());

            // TODO: Tell server that we've read the QR code

            // TODO: Parse the QR code
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

            // TODO: Tell server that we've parsed the QR code

            // TODO: Use Google Translate to translate back to English
            new Thread(new Runnable() {
                public void run() {
                    // Translator translate = Translator.getInstance();
                    // String translatedText = translate.translate(textToTranslate, Language.ENGLISH, Language.ENGLISH);

                    // TODO: Tell server that we've translated back to English

                    speak(textToTranslate);
                }
            }).start();
        }
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
