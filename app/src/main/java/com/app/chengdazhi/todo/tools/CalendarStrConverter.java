package com.app.chengdazhi.todo.tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by chengdazhi on 10/1/15.
 *
 * This util class converts the Calendar object to corresponding long number or the other way around.
 * converts the list of calendars to the list of long numbers and the other way around.
 */
public class CalendarStrConverter {

    //converts the calendar of the deadline to the long number for the database to store.
    public static long convertToLong(Calendar calendar) {
        return calendar.getTimeInMillis();
    }

    //converts the long number to the Calendar object, to use the information in the database to create an Event object.
    public static Calendar convertToCalendar(long deadline) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(deadline);
        return calendar;
    }

    //The list of reminders is not sorted so the judgements need to be done one by one.
    public static List<Calendar> convertToList(String reminders) {
        List<Calendar> list = new ArrayList<Calendar>();
        String[] strs = reminders.split(";");
        for(String reminder : strs) {
            if(!reminder.equals("")) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(reminder));
                list.add(calendar);
            }
        }
        return list;
    }

    public static String convertToStr(List<Calendar> list) {
        StringBuilder reminders = new StringBuilder();
        for(Calendar reminder : list) {
            reminders.append(reminder.getTimeInMillis() + ";");
        }
        return reminders.toString();
    }
}
