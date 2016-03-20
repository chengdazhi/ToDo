package com.app.chengdazhi.todo.components;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.app.chengdazhi.todo.models.Event;
import com.app.chengdazhi.todo.tools.MyDatabaseHelper;

import java.util.Calendar;
import java.util.List;

/**
 * Created by chengdazhi on 2/16/16.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private NotificationManager manager = null;

    public static final int MODE_DONE = 1;

    public static final int MODE_DELAY = 2;

    public static final String MODE_NAME = "mode";

    public static final String CONTENT_EVENT_NAME = "content_event";

    public static final String CONTENT_TOP_NAME = "content_top";

    private MyDatabaseHelper dbHelper;

    public static PendingIntent getPendingIntent(Context context, int mode, List<Event> eventList,
                                                 List<Event> topList){
        Intent intent = new Intent(context, NotificationReceiver.class);
        int eventIds[] = new int[eventList.size()];
        int topIds[] = new int[topList.size()];
        for(int i = 0; i < eventList.size(); i++) {
            eventIds[i] = eventList.get(i).getEventId();
        }
        for(int i = 0; i < topList.size(); i++) {
            topIds[i] = topList.get(i).getEventId();
        }

        intent.putExtra(CONTENT_EVENT_NAME, eventIds);
        intent.putExtra(CONTENT_TOP_NAME, topIds);

        intent.putExtra(MODE_NAME, mode);
        Log.d("NotificationReceiver", "mode = " + mode);

        PendingIntent pIntent = PendingIntent.getBroadcast(context, mode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pIntent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationReceiver", "notification receiver on receive");

        dbHelper = new MyDatabaseHelper(context, "ToDo.db", null, 2);

        if(manager == null) {
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        int mode = intent.getIntExtra(MODE_NAME, -1);
        if(mode != MODE_DELAY && mode != MODE_DONE) {
            Log.e("ToDo", "Mode not received! at NotificationReceiver onReceive() mode = " + mode);
            return;
        }

        int eventIds[] = intent.getIntArrayExtra(CONTENT_EVENT_NAME);
        int topIds[] = intent.getIntArrayExtra(CONTENT_TOP_NAME);

        switch (mode) {
            case MODE_DONE:
                Log.d("NotificationReceiver", "mode done");
                for(int id : eventIds) {
                    dbHelper.eventDone(id, dbHelper);
                }
                for(int id : topIds) {
                    dbHelper.topDone(id, dbHelper);
                }
                manager.cancel(AlarmService.UNDONE_NOTIFICATION_ID);
                break;

            case MODE_DELAY:
                Log.d("NotificationReceiver", "mode delay");
                Event events[] = new Event[eventIds.length];
                Event tops[] = new Event[topIds.length];

                List<Event> eventList = dbHelper.readFromEvent(dbHelper);
                List<Event> topList = dbHelper.readFromTop(dbHelper);

                for(int i = 0; i < eventIds.length; i++) {
                    for(Event e : eventList) {
                        if(e.getEventId() == eventIds[i]) {
                            events[i] = e;
                        }
                    }
                }

                for(int i = 0; i < topIds.length; i++) {
                    for(Event e : topList) {
                        if(e.getEventId() == topIds[i]) {
                            tops[i] = e;
                        }
                    }
                }

                for(Event event : events) {
                    if(event == null) {
                        continue;
                    }
                    Calendar deadline = event.getDeadline();
                    long deadlineStamp = deadline.getTimeInMillis();
                    long currentStamp = Calendar.getInstance().getTimeInMillis();
                    long span = currentStamp - deadlineStamp;
                    if(span < 0) {
                        Log.e("ToDo", "event not done but sent to notification receiver!");
                        continue;
                    }
                    int days = (int)(span / (24 * 60 * 60 * 1000));
                    boolean plus2flag = false;
                    if(deadline.get(Calendar.HOUR_OF_DAY) > Calendar.getInstance().get(Calendar.HOUR_OF_DAY)){
                        plus2flag = true;
                    } else if(deadline.get(Calendar.HOUR_OF_DAY) == Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                        if(deadline.get(Calendar.MINUTE) > Calendar.getInstance().get(Calendar.MINUTE)) {
                            plus2flag = true;
                        }
                    }
                    if(plus2flag) {
                        deadlineStamp += (days + 2) * (24 * 60 * 60 * 1000);
                    } else {
                        deadlineStamp += (days + 1) * (24 * 60 * 60 * 1000);
                    }
                    deadline.setTimeInMillis(deadlineStamp);
                    event.setDeadline(deadline);
                    dbHelper.updateEvent(event, dbHelper);
                }
                for(Event event : tops) {
                    if(event == null) {
                        continue;
                    }
                    Calendar deadline = event.getDeadline();
                    long deadlineStamp = deadline.getTimeInMillis();
                    long currentStamp = Calendar.getInstance().getTimeInMillis();
                    long span = currentStamp - deadlineStamp;
                    if(span < 0) {
                        Log.e("ToDo", "event not done but sent to notification receiver!");
                        continue;
                    }
                    int days = (int)(span / (24 * 60 * 60 * 1000));
                    deadlineStamp += (days + 1) * (24 * 60 * 60 * 1000);
                    deadline.setTimeInMillis(deadlineStamp);
                    event.setDeadline(deadline);
                    dbHelper.updateTop(event, dbHelper);
                }

                manager.cancel(AlarmService.UNDONE_NOTIFICATION_ID);
                break;

            default:
                break;
        }


    }
}
