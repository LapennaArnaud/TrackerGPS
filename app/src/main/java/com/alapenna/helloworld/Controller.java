package com.alapenna.helloworld;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by arnaud on 15/04/2016.
 */
public class Controller {

    Context myContext;
    String pass;
    String tel;
    Boolean modeInit = false; // est true que si le chargement des data grace au shared prefs donne null et null
    Boolean unlock = false;
    ManageSettings manageData;
    MainActivity myActivity;

    public Controller(Context myContext, MainActivity myActivity){
        this.myContext=myContext;
        this.myActivity=myActivity;

        manageData = new ManageSettings();

        //chargement des paramètres
        loadSettings();
    }

    public Boolean getRecord(){
        return ManageSettings.isRecord;
    }

    public String getTel(){
        return tel;
    }

    public Boolean unlock (String pass){
        //chargement des paramètres
        ManageSettings loadMyData = new ManageSettings();
        HashMap<String,String> data = new HashMap<String,String>();
        loadMyData.restoreData(myContext,data);

        if(pass.trim().isEmpty() || pass == null){
            return false;
        }
//        if(loadMyData.encode(pass).equals(data.get("PASS"))){
//            unlock = true;
//            return true;
//        }
        if(pass.equals(data.get("PASS"))){
            unlock = true;
            return true;
        }else{
            unlock=false;
            return false;
        }

    }


    public Boolean isStopOk (String pass){
        if(myActivity.isMyServiceRunning(ServiceLock.class) && unlock(pass)){
            return true;
        }else{
            return false;
        }
    }

    public void loadSettings(){
        //chargement des paramètres de configuration
        ManageSettings loadMyData = new ManageSettings();
        HashMap<String,String> data = new HashMap<String,String>();

        loadMyData.restoreData(myContext,data);

        if(data.get("TEL") == null && data.get("PASS") == null){
            modeInit = true;
            unlock = true;
        }

        pass = data.get("PASS");
        ManageSettings.isRecord = Boolean.parseBoolean(data.get("RECORD"));
        tel=data.get("TEL");
    }

    public boolean broadCastMessage(String msg){
        Log.d("MainActivity", "Broadcasting message");
        Intent intent = new Intent("lockMyPhone");
        intent.putExtra("message",msg);
        return LocalBroadcastManager.getInstance(myContext).sendBroadcast(intent);
    }

    public boolean saveSettings(String tel, String pass){
        HashMap<String,String> data = new HashMap<String,String>();

        if(tel==null || pass==null){
            return false;
        }
        if(tel.trim().isEmpty() || pass.trim().isEmpty()){
            return false;
        }

        data.put("TEL",tel);
        data.put("PASS", pass);

        if(ManageSettings.isRecord){
            data.put("RECORD","true");
        }else {
            data.put("RECORD","false");
        }
        manageData.saveData(myContext, data);
        loadSettings();
        return true;
    }

    public boolean isStartOk(String tel,String pass){
        if(tel.trim().isEmpty() || pass.trim().isEmpty()){
            return false;
        }

        //première utilisation du soft
        if(modeInit){
            saveSettings(tel,pass); // sauvegarde les données dans le xml si le mode init est true et que les champs sont remplis lors de l'appuie sur start
            modeInit = false;
            Toast.makeText(myContext,"data saved: init state",Toast.LENGTH_SHORT).show();

            if(!myActivity.isMyServiceRunning(ServiceLock.class)){
                Intent i = new Intent(myContext,ServiceLock.class);
                myContext.startService(i);
                return true;
            }
            return false;
        }

        //débloqué ?
        if(!unlock){
            return false;
        }

        //ihm débloqué, test password ?
        if(unlock(pass) && !myActivity.isMyServiceRunning(ServiceLock.class)){
            saveSettings(tel,pass);
            Intent i = new Intent(myContext,ServiceLock.class);
            myContext.startService(i);
            return true;
        }

        return false;
    }

    public boolean onPauseOk (String tel,String pass){
        if(tel == null || pass == null){
            return false;
        }

        if(tel.trim().isEmpty() || pass.trim().isEmpty()){
            return false;
        }
        if(unlock){
            saveSettings(tel,pass);
            return true;
        }
        return false;
    }

}
