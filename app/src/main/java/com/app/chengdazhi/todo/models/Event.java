package com.app.chengdazhi.todo.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by chengdazhi on 10/1/15.
 */
public class Event implements Comparable<Event>, Serializable {
    private int eventId = 0;

    private String event;

    private String note;

    private Calendar deadline;

    private List<Calendar> reminders;

    private int labelId = 0;

    private boolean isDone = false;

    //status属性决定某event在item_layout中最上方的textView是否应该显示，以及显示什么。
    private int status = 0;

    public static final int STATUS_NONE = 0; //不需要显示

    public static final int STATUS_UNDONE = 1; //事项已超时

    public static final int STATUS_TODAY = 2;

    public static final int STATUS_TOMORROW = 3;

    public static final int STATUS_THIS_WEEK = 4;

    public static final int STATUS_AFTER = 5;

    public static final int STATUS_TOP = 6;

    public Event(String event, String note, Calendar deadline, List<Calendar> reminders){
        this.event = event;
        this.note = note;
        this.deadline = deadline;
        this.reminders = reminders;
    }

    public Event(String event, Calendar deadline) {
        this.event = event;
        this.note = "";
        this.deadline = deadline;
        this.reminders = new ArrayList<Calendar>();
    }

    public Event(String event, String note, Calendar deadline) {
        this.event = event;
        this.note = note;
        this.deadline = deadline;
        this.reminders = new ArrayList<Calendar>();
    }

    public Event(String event, Calendar deadline, List<Calendar> reminders){
        this.event = event;
        this.note = "";
        this.deadline = deadline;
        this.reminders = reminders;
    }

    public Event(String event, String note, Calendar deadline, List<Calendar> reminders, int labelId){
        this.event = event;
        this.note = note;
        this.deadline = deadline;
        this.reminders = reminders;
        this.labelId = labelId;
    }

    public Event(String event, Calendar deadline, int labelId) {
        this.event = event;
        this.note = "";
        this.deadline = deadline;
        this.reminders = new ArrayList<Calendar>();
        this.labelId = labelId;
    }

    public Event(String event, String note, Calendar deadline, int labelId) {
        this.event = event;
        this.note = note;
        this.deadline = deadline;
        this.reminders = new ArrayList<Calendar>();
        this.labelId = labelId;
    }

    public Event(String event, Calendar deadline, List<Calendar> reminders, int labelId){
        this.event = event;
        this.note = "";
        this.deadline = deadline;
        this.reminders = reminders;
        this.labelId = labelId;
    }


    public String getEvent() {
        return event;
    }

    public String getNote() {
        return note;
    }

    public Calendar getDeadline() {
        return deadline;
    }

    public List<Calendar> getReminders() {
        return reminders;
    }

    public int getLabelId() {
        return labelId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getEventId() {
        return eventId;
    }

    @Override
    public int compareTo(Event another) {
        if(deadline.getTimeInMillis() > another.getDeadline().getTimeInMillis())
            return 1;
        else if(deadline.getTimeInMillis() == another.getDeadline().getTimeInMillis())
            return 0;
        else
            return -1;
    }

    public void setEvent(String event){
        this.event = event;
    }

    public void setNote(String note){
        this.note = note;
    }

    public void setDeadline(Calendar deadline){
        this.deadline = deadline;
    }

    public void setReminders(List<Calendar> reminders) {
        this.reminders = reminders;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    public boolean getIsDone() {
        return isDone;
    }

    public void setStatus(int statusCode) {
        this.status = statusCode;
    }

    public int getStatus() {
        return status;
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
    }
}
