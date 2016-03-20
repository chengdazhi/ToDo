package com.app.chengdazhi.todo;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.app.chengdazhi.todo.models.Event;
import com.app.chengdazhi.todo.tools.MyDatabaseHelper;

import java.util.Calendar;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testDbHelper() throws Exception {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext(), "ToDo.db", null, 1);
//        Log.v("test", "this is verbose log from test");
//        dbHelper.addLabel(new Label(R.color.blue, "study"), dbHelper); //successful

        /*
        //successful
        List<Calendar> list = new ArrayList<Calendar>();
        list.add(Calendar.getInstance());
        list.add(Calendar.getInstance());
        dbHelper.addEvent(new Event("eat dinner", "eat a sardine", Calendar.getInstance(), list, 1), dbHelper);
        */

//        dbHelper.deleteFromEvent(1, dbHelper); successful

        /*
        successful
        List<Calendar> list = new ArrayList<Calendar>();
        list.add(Calendar.getInstance());
        list.add(Calendar.getInstance());
        Event event = new Event("eat lunch", "eat two sardines", Calendar.getInstance(), list, 1);
        event.setEventId(2);
        dbHelper.updateEvent(event, dbHelper);
        */

//        dbHelper.eventDone(2, dbHelper); successful

        /*
        successful
        boolean isExist = dbHelper.labelExist("study", dbHelper);
        if(isExist) {
            throw new Exception("label exists");
        } else {
            throw new Exception("label doesn't exist");
        }
        */

//        dbHelper.updateLabel(new Label(R.color.black, "sleep", 1), dbHelper); successful

//        Label label = dbHelper.getLabel(1, dbHelper); successful

//        dbHelper.deleteLabel(1, dbHelper);

//        dbHelper.deleteFromDone(1, dbHelper);

        Calendar deadline = Calendar.getInstance();
        deadline.set(2015, 9, 20, 21, 18);
        Event event = new Event("Take Tom to dinner", "there is a meeting to be hold at here today, this is a note regarding this event. I would like this note to be longer for testing. It should be long enough by now, at least I hope.", deadline);
//        dbHelper.addEvent(event, dbHelper);
//        dbHelper.addEvent(event, dbHelper);
        dbHelper.addEvent(event, dbHelper);
    }

    public void testEventUtil() throws Exception{
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 5);
        Event event = new Event("event", calendar);
//        throw new Exception(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + " " + Calendar.getInstance().get(Calendar.MINUTE));
//        throw new Exception(EventUtil.toString(event, getContext()));
//        throw new Exception(EventUtil.getFullString(event, getContext()));
    }

}