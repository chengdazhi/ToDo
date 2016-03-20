package com.app.chengdazhi.todo.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.app.chengdazhi.todo.models.Event;
import com.app.chengdazhi.todo.models.Label;
import com.app.chengdazhi.todo.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by chengdazhi on 10/1/15.
 */

/**
 * 在向数据库中保存数据时不提供id，更新数据时提供id。
 * 数据库返回event时都要setEventId。
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_LABEL_DATABASE = "create table Label (" +
            "id integer primary key autoincrement, " +
            "colorId integer, " +
            "name text)";

    public static final String CREATE_EVENT_DATABASE = "create table Event (" +
            "id integer primary key autoincrement, " +
            "event text, " +
            "note text, " +
            "deadline text, " +
            "reminders text, " +
            "labelId integer)";

    public static final String CREATE_DONE_DATABASE = "create table Done (" +
            "id integer primary key autoincrement, " +
            "event text, " +
            "note text, " +
            "deadline text, " +
            "reminders text, " +
            "labelId integer)";

    public static final String CREATE_TOP_DATABASE = "create table Top (" +
            "id integer primary key autoincrement, " +
            "event text, " +
            "note text, " +
            "deadline text, " +
            "reminders text, " +
            "labelId integer)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EVENT_DATABASE);
        db.execSQL(CREATE_DONE_DATABASE);
        db.execSQL(CREATE_TOP_DATABASE);
        db.execSQL(CREATE_LABEL_DATABASE);
        ContentValues values = new ContentValues();
        values.put("colorId", R.color.blue);
        values.put("name", mContext.getString(R.string.label_study));
        db.insert("Label", null, values);
        values = new ContentValues();
        values.put("colorId", R.color.dark_purple);
        values.put("name", mContext.getString(R.string.label_work));
        db.insert("Label", null, values);
        values = new ContentValues();
        values.put("colorId", R.color.brown);
        values.put("name", mContext.getString(R.string.label_others));
        db.insert("Label", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                switch (newVersion) {
                    case 2:
                        db.execSQL(CREATE_TOP_DATABASE);
                        break;

                    default:
                        break;
                }
                break;

            default:
                break;
        }
    }

    //to add an event to the Event table
    public void addEvent(Event event, MyDatabaseHelper dbHelper) {
        ContentValues values = new ContentValues();
        values.put("event", event.getEvent());
        values.put("note", event.getNote());
        values.put("deadline", CalendarStrConverter.convertToLong(event.getDeadline()));
        values.put("reminders", CalendarStrConverter.convertToStr(event.getReminders()));
        values.put("labelId", event.getLabelId());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insert("Event", null, values);
        db.close();
    }

    public void deleteFromEvent(int id, MyDatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Event", "id = ?", new String[]{id + ""});
        db.close();
    }

    public void deleteFromTop(int id, MyDatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Top", "id = ?", new String[]{id + ""});
        db.close();
    }

    //此处必须传入带有eventId属性的event
    public void updateEvent(Event event, MyDatabaseHelper dbHelper) {
        if(event.getEventId() == 0) {
            Log.e("ToDo", "at MyDatabaseHelper.java updateEvent(), eventId = 0");
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("event", event.getEvent());
        values.put("note", event.getNote());
        values.put("deadline", CalendarStrConverter.convertToLong(event.getDeadline()));
        values.put("reminders", CalendarStrConverter.convertToStr(event.getReminders()));
        values.put("labelId", event.getLabelId());
        db.update("Event", values, "id = ?", new String[]{event.getEventId() + ""});
        db.close();
    }

    //executed when an event is done. this method first add the event to the Done table then deletes the event in the Event Table;
    public void eventDone(int id, MyDatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Cursor cursor = db.query("Event", null, "id = ?", new String[]{id + ""}, null, null, null);
        if(cursor.moveToFirst()) {
            values.put("event", cursor.getString(cursor.getColumnIndex("event")));
            values.put("note", cursor.getString(cursor.getColumnIndex("note")));
            values.put("deadline", cursor.getString(cursor.getColumnIndex("deadline")));
            values.put("reminders", cursor.getString(cursor.getColumnIndex("reminders")));
            values.put("labelId", cursor.getString(cursor.getColumnIndex("labelId")));
            db.insert("Done", null, values);
        } else {
            Log.e("ToDo", "error: no event using given id");
        }
        db.delete("Event", "id = ?", new String[]{id + ""});
        cursor.close();
        db.close();
    }

    public void updateDone(Event event, MyDatabaseHelper dbHelper) {
        if(event.getEventId() == 0) {
            Log.e("ToDo", "at MyDatabaseHelper.java updateDone(), eventId = 0");
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("event", event.getEvent());
        values.put("note", event.getNote());
        values.put("deadline", CalendarStrConverter.convertToLong(event.getDeadline()));
        values.put("reminders", CalendarStrConverter.convertToStr(event.getReminders()));
        values.put("labelId", event.getLabelId());
        db.update("Done", values, "id = ?", new String[]{event.getEventId() + ""});
        db.close();
    }

    public void updateTop(Event event, MyDatabaseHelper dbHelper) {
        if(event.getEventId() == 0) {
            Log.e("ToDo", "at MyDatabaseHelper.java updateTop(), eventId = 0");
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("event", event.getEvent());
        values.put("note", event.getNote());
        values.put("deadline", CalendarStrConverter.convertToLong(event.getDeadline()));
        values.put("reminders", CalendarStrConverter.convertToStr(event.getReminders()));
        values.put("labelId", event.getLabelId());
        db.update("Top", values, "id = ?", new String[]{event.getEventId() + ""});
        db.close();
    }

    public void doneToEvent(int id, MyDatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Cursor cursor = db.query("Done", null, "id = ?", new String[]{id + ""}, null, null, null);
        if(cursor.moveToFirst()) {
            values.put("event", cursor.getString(cursor.getColumnIndex("event")));
            values.put("note", cursor.getString(cursor.getColumnIndex("note")));
            values.put("deadline", cursor.getString(cursor.getColumnIndex("deadline")));
            values.put("reminders", cursor.getString(cursor.getColumnIndex("reminders")));
            values.put("labelId", cursor.getString(cursor.getColumnIndex("labelId")));
            db.insert("Event", null, values);
        } else {
            Log.e("ToDo", "error: no event using given id");
        }
        db.delete("Done", "id = ?", new String[]{id + ""});
        cursor.close();
        db.close();
    }

    //returns whether the label's name already exists in the Label table
    public boolean labelExist(String label, MyDatabaseHelper dbHelper) {
        boolean isExist = false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Label", null, "name = ?", new String[]{label}, null, null, null);
        if(cursor.getCount() != 0)
            isExist = true;
        cursor.close();
        db.close();
        return isExist;
    }

    public void addLabel(Label label, MyDatabaseHelper dbHelper) {
        ContentValues values = new ContentValues();
        values.put("colorId", label.getColorId());
        values.put("name", label.getName());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insert("Label", null, values);
        db.close();
    }

    //不接受没有labelId的label。如果没有则不合法
    public void updateLabel(Label label, MyDatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("colorId", label.getColorId());
        values.put("name", label.getName());
        db.update("Label", values, "id = ?", new String[]{label.getLabelId() + ""});
        db.close();
    }

    //returns null if no label is found under the given labelId
    public Label getLabel(int labelId, MyDatabaseHelper dbHelper) {
        Label label = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Label", null, "id = ?", new String[] { labelId + "" }, null, null, null);
        if(cursor.moveToFirst()) {
            int colorId = cursor.getInt(cursor.getColumnIndex("colorId"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            label = new Label(colorId, name, labelId);
        } else {
            Log.e("ToDo", "no label found with the given labelId");
        }
        cursor.close();
        db.close();
        return label;
    }

    public void deleteLabel(int labelId, MyDatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Label", "id = ?", new String[]{labelId + ""});
        Cursor cursor = db.query("Event", null, "labelId = ?", new String[]{labelId + ""}, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                String eventStr = cursor.getString(cursor.getColumnIndex("event"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                Calendar deadline = CalendarStrConverter.convertToCalendar(cursor.getLong(cursor.getColumnIndex("deadline")));
                List<Calendar> reminders = CalendarStrConverter.convertToList(cursor.getString(cursor.getColumnIndex("reminders")));
                int eventId = cursor.getInt(cursor.getColumnIndex("id"));
                Event event = new Event(eventStr, note, deadline, reminders, 0);
                event.setEventId(eventId);
                dbHelper.updateEvent(event, dbHelper);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        db = dbHelper.getWritableDatabase();
        cursor = db.query("Done", null, "labelId = ?", new String[] { labelId + "" }, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                String eventStr = cursor.getString(cursor.getColumnIndex("event"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                Calendar deadline = CalendarStrConverter.convertToCalendar(cursor.getLong(cursor.getColumnIndex("deadline")));
                List<Calendar> reminders = CalendarStrConverter.convertToList(cursor.getString(cursor.getColumnIndex("reminders")));
                int eventId = cursor.getInt(cursor.getColumnIndex("id"));
                Event event = new Event(eventStr, note, deadline, reminders, 0);
                event.setEventId(eventId);
                dbHelper.updateDone(event, dbHelper);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        db = dbHelper.getWritableDatabase();
        cursor = db.query("Top", null, "labelId = ?", new String[] { labelId + "" }, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                String eventStr = cursor.getString(cursor.getColumnIndex("event"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                Calendar deadline = CalendarStrConverter.convertToCalendar(cursor.getLong(cursor.getColumnIndex("deadline")));
                List<Calendar> reminders = CalendarStrConverter.convertToList(cursor.getString(cursor.getColumnIndex("reminders")));
                int eventId = cursor.getInt(cursor.getColumnIndex("id"));
                Event event = new Event(eventStr, note, deadline, reminders, 0);
                event.setEventId(eventId);
                dbHelper.updateTop(event, dbHelper);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    public void deleteFromDone(int id, MyDatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Done", "id = ?", new String[] { id + "" });
        db.close();
    }

    /**
     * 用于查询时过滤结果，显示Event和Done两个表中的内容。
     * @param filter 查询时键入的关键词
     * @return 查询后的一个泛型为event的list集合
     */
    public List<Event> query(String filter, MyDatabaseHelper dbHelper) {
        List<Event> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Event", null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                StringBuilder builder = new StringBuilder();
                String eventStr = cursor.getString(cursor.getColumnIndex("event"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                String deadlineStr = cursor.getString(cursor.getColumnIndex("deadline"));
                String remindersStr = cursor.getString(cursor.getColumnIndex("reminders"));
                int eventLabelId = cursor.getInt(cursor.getColumnIndex("labelId"));
                String labelName = "";
                if(eventLabelId != 0) {
                    labelName = dbHelper.getLabel(eventLabelId, dbHelper).getName();
                }
                String event = builder.append(eventStr).append(note).append(deadlineStr).append(remindersStr).append(labelName).toString();
                if(event.contains(filter)){
                    Calendar deadline = CalendarStrConverter.convertToCalendar(cursor.getLong(cursor.getColumnIndex("deadline")));
                    List<Calendar> reminders = CalendarStrConverter.convertToList(cursor.getString(cursor.getColumnIndex("reminders")));
                    int eventId = cursor.getInt(cursor.getColumnIndex("id"));
                    int labelId = cursor.getInt(cursor.getColumnIndex("labelId"));
                    Event eventObj = new Event(eventStr, note, deadline, reminders, labelId);
                    eventObj.setEventId(eventId);
                    list.add(eventObj);
                }
            } while(cursor.moveToNext());
        } else {
            Log.i("ToDo", "no event under the filter in Event table");
        }
        db = dbHelper.getReadableDatabase();
        cursor.close();
        cursor = db.query("Done", null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                StringBuilder builder = new StringBuilder();
                String eventStr = cursor.getString(cursor.getColumnIndex("event"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                String deadlineStr = cursor.getString(cursor.getColumnIndex("deadline"));
                String remindersStr = cursor.getString(cursor.getColumnIndex("reminders"));
                Log.v("ToDo", "LabelID:" + cursor.getInt(cursor.getColumnIndex("labelId")));
                int eventLabelId = cursor.getInt(cursor.getColumnIndex("labelId"));
                String labelName = "";
                if(eventLabelId != 0) {
                    labelName = dbHelper.getLabel(eventLabelId, dbHelper).getName();
                }
                String event = builder.append(eventStr).append(note).append(deadlineStr).append(remindersStr).append(labelName).toString();
                if(event.contains(filter)) {
                    Calendar deadline = CalendarStrConverter.convertToCalendar(cursor.getLong(cursor.getColumnIndex("deadline")));
                    List<Calendar> reminders = CalendarStrConverter.convertToList(cursor.getString(cursor.getColumnIndex("reminders")));
                    int eventId = cursor.getInt(cursor.getColumnIndex("id"));
                    int labelId = cursor.getInt(cursor.getColumnIndex("labelId"));
                    Event eventObj = new Event(eventStr, note, deadline, reminders, labelId);
                    eventObj.setEventId(eventId);
                    eventObj.setIsDone(true);
                    list.add(eventObj);
                }
            } while(cursor.moveToNext());
        }
        db = dbHelper.getReadableDatabase();
        cursor.close();
        cursor = db.query("Top", null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                StringBuilder builder = new StringBuilder();
                String eventStr = cursor.getString(cursor.getColumnIndex("event"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                String deadlineStr = cursor.getString(cursor.getColumnIndex("deadline"));
                String remindersStr = cursor.getString(cursor.getColumnIndex("reminders"));
                Log.v("ToDo", "LabelID:" + cursor.getInt(cursor.getColumnIndex("labelId")));
                int eventLabelId = cursor.getInt(cursor.getColumnIndex("labelId"));
                String labelName = "";
                if(eventLabelId != 0) {
                    labelName = dbHelper.getLabel(eventLabelId, dbHelper).getName();
                }
                String event = builder.append(eventStr).append(note).append(deadlineStr).append(remindersStr).append(labelName).toString();
                if(event.contains(filter)) {
                    Calendar deadline = CalendarStrConverter.convertToCalendar(cursor.getLong(cursor.getColumnIndex("deadline")));
                    List<Calendar> reminders = CalendarStrConverter.convertToList(cursor.getString(cursor.getColumnIndex("reminders")));
                    int eventId = cursor.getInt(cursor.getColumnIndex("id"));
                    int labelId = cursor.getInt(cursor.getColumnIndex("labelId"));
                    Event eventObj = new Event(eventStr, note, deadline, reminders, labelId);
                    eventObj.setEventId(eventId);
                    eventObj.setIsDone(true);
                    list.add(eventObj);
                }
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Collections.sort(list);
        return list;
    }

    /**
     * 用于用label过滤结果，只显示Event表中的内容
     * @param labelId 传入labelId，便于过滤
     * @param dbHelper 用于获取数据库实体并进行操作
     * @return 泛型为event的list集合
     */
    public List<Event> filter(int labelId, MyDatabaseHelper dbHelper) {
        List<Event> list = new ArrayList<Event>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Event", null, "labelId = ?", new String[] { labelId + "" }, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                String eventStr = cursor.getString(cursor.getColumnIndex("event"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                Calendar deadline = CalendarStrConverter.convertToCalendar(cursor.getLong(cursor.getColumnIndex("deadline")));
                List<Calendar> reminders = CalendarStrConverter.convertToList(cursor.getString(cursor.getColumnIndex("reminders")));
                int eventId = cursor.getInt(cursor.getColumnIndex("id"));
                Event event = new Event(eventStr, note, deadline, reminders, labelId);
                event.setEventId(eventId);
                list.add(event);
            } while (cursor.moveToNext());
        } else {
            Log.w("ToDo", "no result in event table under the current filter");
        }
        cursor.close();
        db.close();
        Collections.sort(list);
        return list;
    }

    public List<Event> filterTop(int labelId, MyDatabaseHelper dbHelper) {
        List<Event> list = new ArrayList<Event>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Top", null, "labelId = ?", new String[] { labelId + "" }, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                String eventStr = cursor.getString(cursor.getColumnIndex("event"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                Calendar deadline = CalendarStrConverter.convertToCalendar(cursor.getLong(cursor.getColumnIndex("deadline")));
                List<Calendar> reminders = CalendarStrConverter.convertToList(cursor.getString(cursor.getColumnIndex("reminders")));
                int eventId = cursor.getInt(cursor.getColumnIndex("id"));
                Event event = new Event(eventStr, note, deadline, reminders, labelId);
                event.setEventId(eventId);
                list.add(event);
            } while (cursor.moveToNext());
        } else {
            Log.w("ToDo", "no result in top table under the current filter");
        }
        db.close();
        Collections.sort(list);
        return list;
    }

    public List<Event> readFromEvent(MyDatabaseHelper dbHelper) {
        List<Event> list = new ArrayList<Event>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Event", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do {
                String eventStr = cursor.getString(cursor.getColumnIndex("event"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                Calendar deadline = CalendarStrConverter.convertToCalendar(cursor.getLong(cursor.getColumnIndex("deadline")));
                List<Calendar> reminders = CalendarStrConverter.convertToList(cursor.getString(cursor.getColumnIndex("reminders")));
                int labelId = cursor.getInt(cursor.getColumnIndex("labelId"));
                int eventId = cursor.getInt(cursor.getColumnIndex("id"));
                Event event = new Event(eventStr, note, deadline, reminders, labelId);
                event.setEventId(eventId);
                list.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Collections.sort(list);
        return list;
    }

    public List<Event> readFromTop(MyDatabaseHelper dbHelper) {
        List<Event> list = new ArrayList<Event>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Top", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do {
                String eventStr = cursor.getString(cursor.getColumnIndex("event"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                Calendar deadline = CalendarStrConverter.convertToCalendar(cursor.getLong(cursor.getColumnIndex("deadline")));
                List<Calendar> reminders = CalendarStrConverter.convertToList(cursor.getString(cursor.getColumnIndex("reminders")));
                int labelId = cursor.getInt(cursor.getColumnIndex("labelId"));
                int eventId = cursor.getInt(cursor.getColumnIndex("id"));
                Event event = new Event(eventStr, note, deadline, reminders, labelId);
                event.setEventId(eventId);
                list.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Collections.sort(list);
        return list;
    }

    public List<Event> readFromDone(MyDatabaseHelper dbHelper) {
        List<Event> list = new ArrayList<Event>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Done", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do {
                String eventStr = cursor.getString(cursor.getColumnIndex("event"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                Calendar deadline = CalendarStrConverter.convertToCalendar(cursor.getLong(cursor.getColumnIndex("deadline")));
                List<Calendar> reminders = CalendarStrConverter.convertToList(cursor.getString(cursor.getColumnIndex("reminders")));
                int labelId = cursor.getInt(cursor.getColumnIndex("labelId"));
                int eventId = cursor.getInt(cursor.getColumnIndex("id"));
                Event event = new Event(eventStr, note, deadline, reminders, labelId);
                event.setEventId(eventId);
                event.setIsDone(true);
                list.add(event);
            } while (cursor.moveToNext());
        } else {
            Log.w("ToDo", "no read from Done table");
        }
        cursor.close();
        db.close();
        Collections.sort(list);
        return list;
    }

    public List<Label> readFromLabel(MyDatabaseHelper dbHelper) {
        List<Label> list = new ArrayList<Label>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Label", null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                int colorId = cursor.getInt(cursor.getColumnIndex("colorId"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int labelId = cursor.getInt(cursor.getColumnIndex("id"));
                Label label = new Label(colorId, name, labelId);
                list.add(label);
            } while (cursor.moveToNext());
        } else {
            Log.w("ToDo", "no read from Label table");
        }
        cursor.close();
        db.close();
        return list;
    }

    public void eventTop(int id, MyDatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Cursor cursor = db.query("Event", null, "id = ?", new String[]{id + ""}, null, null, null);
        if(cursor.moveToFirst()) {
            values.put("event", cursor.getString(cursor.getColumnIndex("event")));
            values.put("note", cursor.getString(cursor.getColumnIndex("note")));
            values.put("deadline", cursor.getString(cursor.getColumnIndex("deadline")));
            values.put("reminders", cursor.getString(cursor.getColumnIndex("reminders")));
            values.put("labelId", cursor.getString(cursor.getColumnIndex("labelId")));
            db.insert("Top", null, values);
        } else {
            Log.e("ToDo", "error: no event using given id");
        }
        cursor.close();
        db.delete("Event", "id = ?", new String[]{id + ""});
        db.close();
    }

    public void topEvent(int id, MyDatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Cursor cursor = db.query("Top", null, "id = ?", new String[]{id + ""}, null, null, null);
        if(cursor.moveToFirst()) {
            values.put("event", cursor.getString(cursor.getColumnIndex("event")));
            values.put("note", cursor.getString(cursor.getColumnIndex("note")));
            values.put("deadline", cursor.getString(cursor.getColumnIndex("deadline")));
            values.put("reminders", cursor.getString(cursor.getColumnIndex("reminders")));
            values.put("labelId", cursor.getString(cursor.getColumnIndex("labelId")));
            db.insert("Event", null, values);
        } else {
            Log.e("ToDo", "error: no event using given id");
        }
        cursor.close();
        db.delete("Top", "id = ?", new String[]{id + ""});
        db.close();
    }

    public void topDone(int id, MyDatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Cursor cursor = db.query("Top", null, "id = ?", new String[]{id + ""}, null, null, null);
        if(cursor.moveToFirst()) {
            values.put("event", cursor.getString(cursor.getColumnIndex("event")));
            values.put("note", cursor.getString(cursor.getColumnIndex("note")));
            values.put("deadline", cursor.getString(cursor.getColumnIndex("deadline")));
            values.put("reminders", cursor.getString(cursor.getColumnIndex("reminders")));
            values.put("labelId", cursor.getString(cursor.getColumnIndex("labelId")));
            db.insert("Done", null, values);
        } else {
            Log.e("ToDo", "error: no event using given id");
        }
        cursor.close();
        db.delete("Top", "id = ?", new String[]{id + ""});
        db.close();
    }
}
