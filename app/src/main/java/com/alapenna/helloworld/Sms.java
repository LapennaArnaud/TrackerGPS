package com.alapenna.helloworld;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by arnaud on 17/03/2016.
 */
public class Sms {

    public void sendSMS ( String phoneNumer, String message, final Context myContext){
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(myContext, 0,new Intent(SENT),0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(myContext,0,new Intent(DELIVERED),0);

        //Envoie du SMS
        myContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(myContext.getApplicationContext(),"SMS sent",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(myContext.getApplicationContext(),"Generic failure",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE :
                        Toast.makeText(myContext.getApplicationContext(),"no service",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(myContext.getApplicationContext(),"null PDU",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(myContext.getApplicationContext(),"Radio off",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        },new IntentFilter(SENT));


        //sms recu par le destinatire ?
        myContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(myContext.getApplicationContext(),"SMS delivered",Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(myContext.getApplicationContext(),"SMS not delivered",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        },new IntentFilter(DELIVERED));

        // Demande d'envoi du SMS
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumer, null, message, sentPI, deliveredPI);
    }

}
