package com.hacktx.echoredux;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

        if (tts != null){
            tts.stop();
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
            ((ImageView) findViewById(R.id.qrCode)).setImageBitmap(qrCode.bitmap());

            // TODO: Tell server that we've generated the QR code

            // TODO: Upload the image to Imgur

            // TODO: Tell server that we've uploaded the QR code

            // TODO: Download the image from Imgur

            // TODO: Tell server that we've downloaded the QR code

            // TODO: Parse the QR code
            Log.i("TAG", "Parsing QR code...");

            Bitmap bitmap = qrCode.bitmap();
            int width = bitmap.getWidth(), height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            bitmap.recycle();
            bitmap = null;
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

                    // TODO: TTS for Alexa
                    speak(textToTranslate);
                }
            }).start();
        }
    }

    private void speak(final String str) {
        tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.speak(str, TextToSpeech.QUEUE_FLUSH, null, hashCode() + "");
                } else {
                    throw new RuntimeException("Failed to init TextToSpeech!");
                }
            }
        });
    }
}
