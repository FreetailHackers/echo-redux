package com.hacktx.echoredux.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getSimpleName();
    private final String SMS_SENDER = "+8179853456";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                if (smsMessage.getOriginatingAddress().equals(SMS_SENDER)) {
                    Log.i(TAG, smsMessage.getMessageBody());

                    // Notify app of incoming message
                    Intent i = new Intent("com.hacktx.echoredux.SMS_RECEIVED");
                    i.putExtra("text", smsMessage.getMessageBody().replace("Sent from your Twilio trial account - ", ""));
                    context.sendBroadcast(i);
                }
            }
        }
    }
}
