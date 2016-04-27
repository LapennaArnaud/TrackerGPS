package com.alapenna.helloworld;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";
    private ServiceLock monService;

    //@TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        Log.i(TAG, "Intent received: " + intent.getAction());

        if (intent.getAction() == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            Bundle bundle = intent.getExtras();

            String info = intent.getStringExtra("format");

            if (bundle != null) {

                Object[] pdus = (Object[]) bundle.get("pdus");

                final SmsMessage[] messages = new SmsMessage[pdus.length];

                for (int i = 0; i < pdus.length; i++) {

                    //pour l'api 23 mini pour choisir le format
                    //messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], info);
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                }

                if (messages.length > -1) {
                    Log.i(TAG, "Message received: " + messages[0].getMessageBody());
                    Toast.makeText(context, messages[0].getMessageBody(), Toast.LENGTH_LONG).show();
                    checkSMS(messages[0].getMessageBody(), context);
                }
            }
        }
    }

//    public void checkSMS(String SMS, Context context) {
//        // Stoppe la réception des SMS : ! Attention le système n'est pus pilotable !
//        if (SMS.equals("stopService")) {
//            broadCastMessage(context, "stopService");
//        } else if (SMS.equals("startGPS")){
//            broadCastMessage(context,"startGPS");
//        } else if (SMS.equals("stopGPS")){
//            broadCastMessage(context,"stopGPS");
//        }
//    }

    void checkSMS(String SMS, Context context)
    {
        if(SMS==null || SMS.trim().isEmpty()) return;

        // Stop la reception des SMS : ! Attention le systeme n'est plus pilotable !
        if(SMS.equals("stopService")) {
            broadCastMessage(context, "stopService");
        }

        // Demarre le tracking GPS
        if(SMS.equals("startGPS")) {
            broadCastMessage(context, "startGPS");
        }

        // Stop le tracking GPS
        if(SMS.equals("stopGPS")) {
            broadCastMessage(context, "stopGPS");
        }

        // Start le log
        if(SMS.equals("startLOG")) {
            broadCastMessage(context, "startLOG");
        }

        // Stop le log
        if(SMS.equals("stopLOG")) {
            broadCastMessage(context, "stopLOG");
        }
    }



    private void broadCastMessage(Context context,String msg){
        Log.d(TAG, "Broadcasting message");
        Intent intent = new Intent("lockMyPhone");
        intent.putExtra("message",msg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}

