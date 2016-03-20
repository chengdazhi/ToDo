package com.app.chengdazhi.todo.tools;

import android.content.Context;
import android.util.Log;

import com.app.chengdazhi.todo.models.Event;
import com.app.chengdazhi.todo.R;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by chengdazhi on 10/3/15.
 */
public class EventUtil {

    public static final int DAY_IN_MILLIS = 86400000;

    //对list中的所有event设置status属性
    //此处可能出现bug：有可能对event对象进行的操作无效

    public static List<Event> markEventList(List<Event> list) {
        if(list.size() == 0){
            return list;
        }
        Collections.sort(list);
        Event previousEvent = null;
        if(!isToday(list.get(0)) && isUndone(list.get(0))) {
            list.get(0).setStatus(Event.STATUS_UNDONE);
        } else if(isToday(list.get(0))) {
            list.get(0).setStatus(Event.STATUS_TODAY);
        } else if(isTomorrow(list.get(0))) {
            list.get(0).setStatus(Event.STATUS_TOMORROW);
        } else if(isThisWeek(list.get(0))) {
            list.get(0).setStatus(Event.STATUS_THIS_WEEK);
        } else {
            list.get(0).setStatus(Event.STATUS_AFTER);
        }
        for(Event event : list) {
            if(previousEvent != null) {
                if (isToday(event) && !isToday(previousEvent)) {
                    event.setStatus(Event.STATUS_TODAY);
                } else if (isTomorrow(event) && !isTomorrow(previousEvent)) {
                    event.setStatus(Event.STATUS_TOMORROW);
                } else if (isThisWeek(event) && !isThisWeek(previousEvent)) {
                    event.setStatus(Event.STATUS_THIS_WEEK);
                } else if (isAfter(event) && !isAfter(previousEvent)) {
                    event.setStatus(Event.STATUS_AFTER);
                } else {
                    event.setStatus(Event.STATUS_NONE);
                }
            }
            previousEvent = event;
        }
        return list;
    }

    public static boolean isUndone(Event event) {
        return event.getDeadline().getTimeInMillis() < Calendar.getInstance().getTimeInMillis();
    }

    public static boolean isToday(Event event) {
        Calendar calendar = event.getDeadline();
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0);
//        if(calendar.getTimeInMillis() < today.getTimeInMillis())
//            Log.e("ToDo", "error: event should be in the past");
        return ((calendar.getTimeInMillis() - today.getTimeInMillis()) <= DAY_IN_MILLIS && calendar.getTimeInMillis() >= today.getTimeInMillis());
    }

    public static boolean isTomorrow(Event event) {
        Calendar calendar = event.getDeadline();
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0);
        return (calendar.getTimeInMillis() - today.getTimeInMillis()) > DAY_IN_MILLIS && (calendar.getTimeInMillis() - today.getTimeInMillis()) <= (DAY_IN_MILLIS * 2);
    }

    //判断是否在未来七天
    public static boolean isThisWeek(Event event){
        Calendar calendar = event.getDeadline();
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0);
        return (calendar.getTimeInMillis() - today.getTimeInMillis()) > (DAY_IN_MILLIS * 2) && (calendar.getTimeInMillis() - today.getTimeInMillis()) <= (DAY_IN_MILLIS * 7);
    }

    public static boolean isAfter(Event event) {
        Calendar calendar = event.getDeadline();
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0);
        return (calendar.getTimeInMillis() - today.getTimeInMillis()) > (DAY_IN_MILLIS * 7);
    }

    public static String getTime(Event event){
        Calendar calendar = event.getDeadline();
        String time = "";
        String hour = calendar.get(Calendar.HOUR_OF_DAY) + "";
        String minute = calendar.get(Calendar.MINUTE) + "";
        if(minute.length() == 1){
            minute = "0" + minute;
        }
        if(hour.length() == 1){
            hour = "0" + hour;
        }
        time = hour + ":" + minute;
        return time;
    }

    public static String getDayOfWeek(Event event, Context context) {
        Calendar calendar = event.getDeadline();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String dayOfWeekStr = "";
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                dayOfWeekStr = context.getString(R.string.monday);
                break;

            case Calendar.TUESDAY:
                dayOfWeekStr = context.getString(R.string.tuesday);
                break;

            case Calendar.WEDNESDAY:
                dayOfWeekStr = context.getString(R.string.wednesday);
                break;

            case Calendar.THURSDAY:
                dayOfWeekStr = context.getString(R.string.thursday);
                break;

            case Calendar.FRIDAY:
                dayOfWeekStr = context.getString(R.string.friday);
                break;

            case Calendar.SATURDAY:
                dayOfWeekStr = context.getString(R.string.saturday);
                break;

            case Calendar.SUNDAY:
                dayOfWeekStr = context.getString(R.string.sunday);
                break;

            default:
                Log.e("ToDo", "No day of week under the given calendar. Very Odd!!!");
                break;

        }
        return dayOfWeekStr;
    }

    public static String getDateWithYear(Event event, Context context){
        Calendar calendar = event.getDeadline();
        int year = calendar.get(Calendar.YEAR);
        String date = getDateWithoutYear(event, context) + "-" + year;
        return date;
    }

    //不确定此方法中dayOfMonth是从0开始还是1开始，因此可能有bug。
    public static String getDateWithoutYear(Event event, Context context) {
        Calendar calendar = event.getDeadline();
        String date = "";
        int month = calendar.get(Calendar.MONTH);
        switch (month) {
            case Calendar.JANUARY:
                date = context.getString(R.string.january) + "-";
                break;

            case Calendar.FEBRUARY:
                date = context.getString(R.string.february) + "-";
                break;

            case Calendar.MARCH:
                date = context.getString(R.string.march) + "-";
                break;

            case Calendar.APRIL:
                date = context.getString(R.string.april) + "-";
                break;

            case Calendar.MAY:
                date = context.getString(R.string.may) + "-";
                break;

            case Calendar.JUNE:
                date = context.getString(R.string.june) + "-";
                break;

            case Calendar.JULY:
                date = context.getString(R.string.july) + "-";
                break;

            case Calendar.AUGUST:
                date = context.getString(R.string.august) + "-";
                break;

            case Calendar.SEPTEMBER:
                date = context.getString(R.string.september) + "-";
                break;

            case Calendar.OCTOBER:
                date = context.getString(R.string.october) + "-";
                break;

            case Calendar.NOVEMBER:
                date = context.getString(R.string.november) + "-";
                break;

            case Calendar.DECEMBER:
                date = context.getString(R.string.december) + "-";
                break;

            default:
                Log.e("ToDo", "wrong month code, strange!!!");
                break;
        }
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        date += dayOfMonth;
        return date;
    }

    public static String getFullString(Event event, Context context) {
        Calendar calendar = event.getDeadline();
        return getDateWithYear(event, context) + " " + getTime(event);
    }

    public static String toTimeString(Event event, Context context) {
        String result = "";
        if(event.getIsDone()){
            result = getFullString(event, context);
        } else if(isToday(event) || isTomorrow(event)) {
            result = getTime(event);
        } else if(isThisWeek(event)) {
            result = getDayOfWeek(event, context);
        } else if(isUndone(event)) {
            result = getFullString(event, context);
        } else {
            result = getDateWithYear(event, context);
        }
        return result;
    }

}
