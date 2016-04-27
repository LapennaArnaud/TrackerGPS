package com.alapenna.helloworld;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartAuto extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, ServiceLock.class);
        context.startService(i);
    }
}
