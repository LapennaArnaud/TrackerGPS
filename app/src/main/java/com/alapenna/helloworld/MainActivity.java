package com.alapenna.helloworld;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText editTextPhone;
    EditText editTextPass;
    CheckBox checkBoxRecord;

    Context myContext;
    Controller myController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myContext = this.getApplicationContext();
        myController = new Controller(this.getApplicationContext(),MainActivity.this);

        //Champ numéro téléphone
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextPass = (EditText) findViewById(R.id.editTextPass);
        checkBoxRecord = (CheckBox) findViewById(R.id.checkBoxRecord);

        //unlock
        Button buttonUnlock = (Button) findViewById(R.id.buttonUnlock);
        buttonUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((myController.unlock(editTextPass.getText().toString()))) {
                    editTextPhone.setText(myController.getTel());
                    checkBoxRecord.setChecked(myController.getRecord());
                } else {
                    Toast.makeText(myContext, "Unlock failed : MDP faux", Toast.LENGTH_LONG).show();
                }
            }
        });

        //start
        Button buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myController.isStartOk(editTextPhone.getText().toString(), editTextPass.getText().toString())) {

                    if (checkBoxRecord.isChecked()) {
                        ManageSettings.isRecord = true;
                    } else {
                        ManageSettings.isRecord = false;
                    }

                    Toast.makeText(myContext, "Start service", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(myContext, "Can't start service : isStartOk false", Toast.LENGTH_LONG).show();
                }
            }
        });

        // stop
        Button buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myController.isStopOk(editTextPass.getText().toString())) {
                    if (myController.broadCastMessage("stopService")) {
                        Toast.makeText(myContext, "Stop service...", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(myContext, "Can't stop service : isStopOK false", Toast.LENGTH_LONG).show();
                }
            }
        });


        //Checkbox
        checkBoxRecord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (myController.unlock(editTextPass.getText().toString())) {
                    if(isChecked){
                        ManageSettings.isRecord = true;
                    }else{
                        ManageSettings.isRecord = false;
                    }
                } else {
                    Toast.makeText(myContext,"record imp : not unlocked",Toast.LENGTH_LONG).show();
                }
            }
        });


        //button de test
        Button buttonTest = (Button)findViewById(R.id.buttonTest);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Sms mySms = new Sms();
                if(editTextPhone.getText().toString().trim().isEmpty()){
                    mySms.sendSMS("0674669323", "test SMS",myContext);
                }else {
                    mySms.sendSMS(editTextPhone.getText().toString(), "test SMS",myContext);
                }
            }
        });


        /*

        //Recepteur de SMS
        SmsReceiver myReciever = new SmsReceiver();

        this.registerReceiver(myReciever, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

        //Logcat
        Log.i("TAG", "MESSAGE");

        */

        // btn maps
        Button buttonMaps = (Button) findViewById(R.id.buttonMaps);
        buttonMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondeActivite = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(secondeActivite);
                Toast.makeText(myContext,"LA MAPS",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPause(){

        myController.onPauseOk(editTextPhone.getText().toString(),editTextPass.getText().toString());

        super.onPause();
    }

    /**
     * Test si le service est deja lance
     *
     * @param serviceClass
     * @return
     */
    public boolean isMyServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

}
