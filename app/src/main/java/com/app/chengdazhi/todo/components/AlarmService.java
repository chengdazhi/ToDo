package com.app.chengdazhi.todo.components;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.app.chengdazhi.todo.R;
import com.app.chengdazhi.todo.models.Event;
import com.app.chengdazhi.todo.tools.MyDatabaseHelper;
import com.app.chengdazhi.todo.tools.SettingsUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by chengdazhi on 10/13/15.
 *
 * need to make sure the service is running all the time.
 *
 * create the notification with a deleteIntent which triggers broadcast receiver inside the service
 * this should not work with the notification's autocancel set to true(to be tested)
 * the reminder notification's deleteIntent should contain the id so the service can cancel it
 *
 */
public class AlarmService extends Service {
    private NotificationManager manager;

    private MyDatabaseHelper dbHelper;

    public static final int UNDONE_NOTIFICATION_ID = -100;

    public static final int NEW_NOTIFICATION_ID = -1;

    private boolean showNotificatioinBoolean;

    private boolean soundBoolean;

    private boolean vibrateBoolean;

    private boolean breathLightBoolean;

    private boolean instantAddBoolean;

    private String lastNotificationTitle = "";

    private String lastNotificationText = "";

    private List<Integer> eventIdList;

    private List<Long> reminderList;

    private boolean isDialogNotificationActive = false;

    private Handler handler = new Handler();

    private Runnable task =new Runnable() {
        public void run() {
            // TODOAuto-generated method stub
            Log.d("ToDo", "Service task running");
            handler.postDelayed(this, 10 * 1000);//设置延迟时间，此处是5秒
            Log.v("ToDo", "Loop start");
            launchNotifications();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ToDo", "Service onCreate");
        dbHelper = new MyDatabaseHelper(this, "ToDo.db", null, 2);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        eventIdList = new ArrayList<>();
        reminderList = new ArrayList<Long>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(task, 5000);//to call five seconds later

        launchNotifications();

        return super.onStartCommand(intent, flags, startId);
    }

    public void launchNotifications(){
        Log.v("AlarmService", "launchNotifications() start");
        showNotificatioinBoolean = SettingsUtil.getNotification(this);
        soundBoolean = SettingsUtil.getSound(this);
        vibrateBoolean = SettingsUtil.getVibrate(this);
        breathLightBoolean = SettingsUtil.getBreathLight(this);
        instantAddBoolean = SettingsUtil.getInstantAdd(this);

        Log.v("AlarmService", "instantAddBoolean: " + instantAddBoolean
                + "; isDialogNotificationActive: " + isDialogNotificationActive);
        if(instantAddBoolean && !isDialogNotificationActive){
            launchDialogNotification();
        } else if(!instantAddBoolean && isDialogNotificationActive) {
            dismissDialogNotification();
        }

        if(showNotificatioinBoolean) {
            undoneAlarm();
            reminderAlarm();
        }

    }

    public void launchDialogNotification(){
        Log.v("AlarmService", "launchDialogNotification() start");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setAutoCancel(false)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.todo_logo))
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(getString(R.string.notificatioin_new_task))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH);

        Intent alarmIntent = new Intent(getApplicationContext(), DialogEventActivity.class);
        PendingIntent alarmPendingIntent = PendingIntent.getActivity(this,
                NEW_NOTIFICATION_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(alarmPendingIntent);

        manager.notify(NEW_NOTIFICATION_ID, mBuilder.build());
        isDialogNotificationActive = true;
    }

    public void dismissDialogNotification(){
        Log.v("AlarmService", "dismissDialogNotification() start");
        manager.cancel(NEW_NOTIFICATION_ID);
        isDialogNotificationActive = false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void undoneAlarm() {
        Log.v("AlarmService", "undoneAlarm() start");
        String presentNotificationTitle = "";
        String presentNotificationText = "";

        List<Event> eventList = dbHelper.readFromEvent(dbHelper);//already sorted
        List<Event> topList = dbHelper.readFromTop(dbHelper);

        Event singleUndoneEvent = null;
        //To add expand View
        List<Event> undoneEventList = new ArrayList<Event>();
        List<Event> undoneTopList = new ArrayList<Event>();
        List<Event> undoneTotalList = new ArrayList<Event>();

        int undoneCount = 0;
        for(Event event : eventList) {
            if(event.getDeadline().getTimeInMillis() < Calendar.getInstance().getTimeInMillis()){
                singleUndoneEvent = event;
                undoneEventList.add(event);
            } else {
                break;
            }
        }
        for(Event event : topList) {
            if(event.getDeadline().getTimeInMillis() < Calendar.getInstance().getTimeInMillis()){
                singleUndoneEvent = event;
                undoneTopList.add(event);
            } else {
                break;
            }
        }
        undoneTotalList = undoneEventList;
        undoneTotalList.addAll(undoneTopList);
        Collections.sort(undoneTotalList);

        undoneCount = undoneTotalList.size();

        if(undoneCount == 0){
            manager.cancel(UNDONE_NOTIFICATION_ID);
            lastNotificationText = "";
            lastNotificationTitle = "";
            return;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setAutoCancel(false)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.todo_logo))
                .setSmallIcon(getNotificationIcon());

        if(undoneCount == 1){
            if(singleUndoneEvent != null) {
                presentNotificationTitle = singleUndoneEvent.getEvent();
                presentNotificationText = singleUndoneEvent.getNote();
            } else {
                Log.e("AlarmService", "error: singleUndoneEvent shouldn't be null");
            }

            PendingIntent donePIntent = NotificationReceiver.getPendingIntent(this,
                    NotificationReceiver.MODE_DONE, undoneEventList, undoneTopList);
            mBuilder.addAction(R.drawable.ic_action_accept, getString(R.string.complete), donePIntent);

            PendingIntent delayPIntent = NotificationReceiver.getPendingIntent(this,
                    NotificationReceiver.MODE_DELAY, undoneEventList, undoneTopList);
            mBuilder.addAction(R.drawable.ic_action_delay, getString(R.string.delay), delayPIntent);

        } else {//如果有多条未完成事项
            Log.v("AlarmService", "multiple tasks undone");
            if(singleUndoneEvent != null) {
                presentNotificationTitle = singleUndoneEvent.getEvent();
            } else {
                Log.e("AlarmService", "error: singleUndoneEvent shouldn't be null");
            }
            presentNotificationText = undoneCount + " " + getString(R.string.notification_undone_title_multiple);

            StringBuilder sb = new StringBuilder();
            for(int i = undoneCount - 2; i >= 0; i--) {
                sb.append(undoneTotalList.get(i).getEvent() + "\n");
            }
            sb.deleteCharAt(sb.length() - 1);
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(sb.toString()));

            PendingIntent donePIntent = NotificationReceiver.getPendingIntent(this,
                    NotificationReceiver.MODE_DONE, undoneEventList, undoneTopList);
            mBuilder.addAction(R.drawable.ic_action_accept, getString(R.string.complete_all), donePIntent);

            PendingIntent delayPIntent = NotificationReceiver.getPendingIntent(this,
                    NotificationReceiver.MODE_DELAY, undoneEventList, undoneTopList);
            mBuilder.addAction(R.drawable.ic_action_delay, getString(R.string.delay_all), delayPIntent);
        }

        if(vibrateBoolean) {
            mBuilder = setVibrate(mBuilder);
        }
        if(breathLightBoolean) {
            mBuilder = setLights(mBuilder);
        }
        if(soundBoolean) {
            mBuilder = setSound(mBuilder);
        }

        if(presentNotificationTitle.equals(lastNotificationTitle) && presentNotificationText.equals(lastNotificationText)){
            Log.v("AlarmService", "undone notification already exists");
            return;
        } else {
            Log.v("AlarmService", "undone notification doesn't exist, creating now");
            lastNotificationText = presentNotificationText;
            lastNotificationTitle = presentNotificationTitle;
        }
        mBuilder.setContentTitle(presentNotificationTitle);
        mBuilder.setContentText(presentNotificationText);

        Intent alarmIntent = new Intent(this, MainActivity.class);
        PendingIntent alarmPendingIntent = PendingIntent.getActivity(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(alarmPendingIntent);

        manager.notify(UNDONE_NOTIFICATION_ID, mBuilder.build());

    }

    public void reminderAlarm() {
        Log.v("AlarmService", "reminderAlarm() start");
        List<Event> eventList = dbHelper.readFromEvent(dbHelper);
        Log.v("ToDo", "reminder alarm start, list size : " + eventList.size());
        for(Event event : eventList) {
            List<Calendar> reminders = event.getReminders();
            for(Calendar calendar : reminders) {

                if(eventIdList.contains(event.getEventId()) && reminderList.contains(calendar.getTimeInMillis())){
                    Log.v("AlarmService", "eventReminder already exists in hashmap. event id: " + event.getEventId());
                    continue;
                } else {
                    Log.v("AlarmService", "eventReminder doesn't exist in hashmap. event id:" + event.getEventId());

                    Log.v("ToDo", calendar.getTimeInMillis() + ":" + System.currentTimeMillis() + ":" + Math.abs(calendar.getTimeInMillis() - System.currentTimeMillis()));
                    if (Math.abs(calendar.getTimeInMillis() - System.currentTimeMillis()) < 30 * 1000) {
                        //set the Notification id as eventId
                        NotificationCompat.Builder reminderBuilder = new NotificationCompat.Builder(this)
                                .setAutoCancel(true)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.todo_logo))
                                .setSmallIcon(getNotificationIcon())
                                .setContentTitle(event.getEvent())
                                .setContentText(event.getNote());

                        if (vibrateBoolean) {
                            reminderBuilder = setVibrate(reminderBuilder);
                        }
                        if (breathLightBoolean) {
                            reminderBuilder = setLights(reminderBuilder);
                        }
                        if (soundBoolean) {
                            reminderBuilder = setSound(reminderBuilder);
                        }

                        Intent alarmIntent = new Intent(this, MainActivity.class);
                        PendingIntent alarmPendingIntent = PendingIntent.getActivity(this,
                                event.getEventId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        reminderBuilder.setContentIntent(alarmPendingIntent);
                        manager.notify(event.getEventId(), reminderBuilder.build());

                        if(!eventIdList.contains(event.getEventId())){
                            eventIdList.add(event.getEventId());
                        }
                        reminderList.add(calendar.getTimeInMillis());
                        break;
                    }
                }
            }
        }
    }

    //doesn't work properly on HUAWEI M2, keeps sending existed notification
//    private boolean isNotificationVisible(int MY_ID) {
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent test = PendingIntent.getActivity(this, MY_ID, notificationIntent, PendingIntent.FLAG_NO_CREATE);
//        return test != null;
//    }

    private NotificationCompat.Builder setSound(NotificationCompat.Builder builder){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        return builder;
    }

    private NotificationCompat.Builder setVibrate(NotificationCompat.Builder builder){
        builder.setVibrate(new long[]{0, 500, 500, 500});
        return builder;
    }

    private NotificationCompat.Builder setLights(NotificationCompat.Builder builder){
        builder.setLights(Color.GREEN, 2000, 2000);
        return builder;
    }

    private int getNotificationIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.drawable.ic_action_time_light : R.drawable.todo_logo;
    }

    //for future usage
//    private class InnerReceiver extends BroadcastReceiver{
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//        }
//    }
}
