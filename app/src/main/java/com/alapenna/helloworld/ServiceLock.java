package com.alapenna.helloworld;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import javax.net.ssl.ManagerFactoryParameters;

public class ServiceLock extends Service {

    private static final String TAG = "ServiceLock";

    //SMS
    SmsReceiver myReceiver;
    BroadcastReceiver msgCom;

    //GPS
    LocationManager locationManager = null;
    private String provider;
    private MyLocationListener myListener;

    //DATA
    String phoneNumber;


    @Override
    public void onCreate(){

        initData();
        initSMS();
        intiBroadcast();

        Toast.makeText(this,"ServiceLock --> Service Started",Toast.LENGTH_SHORT).show();
        Log.d(TAG,"Service started");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        //redémarage en cas d'arret
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(msgCom);
        unregisterReceiver(myReceiver);
        Log.d("ServiceLock","service done");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void intiBroadcast(){
        msgCom = new MsgCom();
        // récepteur local pour les intents
        LocalBroadcastManager.getInstance(this).registerReceiver(msgCom,new IntentFilter("lockMyPhone"));
    }

    private void initSMS(){
        //reception des SMS
        myReceiver = new SmsReceiver();
        this.registerReceiver(myReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }


    private void initGPS(){
        // le gestionnaire de position
        if(locationManager==null){
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            //definition du critère pour sélectionner fournisseur de position de plus précis
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);

            //renvoie le fournisseur disponible
            provider = locationManager.getBestProvider(criteria,true);
            Log.d(TAG,"provider = "+provider);
        }

        if(provider!=null){
            //dernière position connue par le provider
            Location location = locationManager.getLastKnownLocation(provider);
            myListener = new MyLocationListener(this,phoneNumber);

            if (location!=null){
                myListener.onLocationChanged(location);
            }

            //conditions de mise à jour de la position : au moins 10 mètres et 15000 millsecs
            locationManager.requestLocationUpdates(provider,15000,10,myListener);
        }
    }

    private void stopGPS(){
        if(locationManager!=null && myListener!=null){
            locationManager.removeUpdates(myListener);

            myListener=null;
        }
    }

    private void initData(){
        //chargement des paramètres
        ManageSettings loadMyData = new ManageSettings();
        HashMap<String,String> data = new HashMap<String,String>();
        loadMyData.restoreData(this.getApplicationContext(),data);

        ManageSettings.isRecord = Boolean.valueOf(data.get("RECORD"));
        phoneNumber=data.get("TEL");
    }



    private class MsgCom extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent){
            if(TextUtils.equals(intent.getAction(),"lockMyPhone")){
                String message = intent.getStringExtra("message");
                Log.d("receiver","Got message: "+message);

                if(message.equals("stopService")){
                    stopGPS();
                    ServiceLock.this.stopSelf();
                    Log.d("receiver","Service stopped");
                } else if (message.equals("startGPS")){
                    initGPS();
                    Log.d(TAG,"GPS tracking started");
                } else if (message.equals("stopGPS")){
                    stopGPS();
                    Log.d(TAG,"GPS tracking stopped");
                } else if(message.equals("startLOG")){
                    ManageSettings.isRecord=true;
                    Log.d(TAG, "Start log");
                } else if(message.equals("stopLOG")){
                    ManageSettings.isRecord=false;
                    Log.d(TAG, "Stop log");
                }
            }
        }
    }

}
