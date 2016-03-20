package com.app.chengdazhi.todo.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by chengdazhi on 10/13/15.
 *
 * receives alarm broadcast and start the AlarmService.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("ToDo", "Alarm Receiver onReceive");
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(context, AlarmService.class);
        context.startService(serviceIntent);
    }
}
