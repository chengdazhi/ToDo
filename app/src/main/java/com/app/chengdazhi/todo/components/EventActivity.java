package com.app.chengdazhi.todo.components;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.chengdazhi.todo.models.Event;
import com.app.chengdazhi.todo.models.Label;
import com.app.chengdazhi.todo.R;
import com.app.chengdazhi.todo.tools.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class EventActivity extends ActionBarActivity {

    public ActionBar actionBar;

    private boolean isNew = true;

    private String eventStr = "";

    private String note = "";

    private Calendar deadline = null;

    private List<Calendar> reminders = new ArrayList<Calendar>();

    private Label label = null;

    private int labelId = 0;

    private Event originalEvent = new Event(null, null);

    private MyDatabaseHelper dbHelper;

    private static final String INTENT_NAME = "event";

    private static final String INTENT_MODE_NAME = "mode";

    private EditText eventEditText;

    private TextView eventWarningTextView;

    private EditText noteEditText;

    private EditText deadlineEditText;

    private TextView deadlineWarningTextView;

    private LinearLayout reminderLayout1;

    private LinearLayout reminderLayout2;

    private LinearLayout reminderLayout3;

    private EditText reminderEditText1;

    private EditText reminderEditText2;

    private EditText reminderEditText3;

    private ImageView reminderCancel1;

    private ImageView reminderCancel2;

    private ImageView reminderCancel3;

    private TextView reminderWarning1;

    private TextView reminderWarning2;

    private TextView reminderWarning3;

    private ImageView reminderAdd;

    private boolean isLayout1Displayed = true;

    private boolean isLayout2Displayed = false;

    private boolean isLayout3Displayed = false;

    private boolean isAddDisplayed = true;

    private TextView labelColorTextview;

    private TextView labelNameTextview;

    private ListView labelListView;

    private List<Label> labelList;

    private LabelListAdapter adapter;

    private final String DATE_SEPARATER = "-";

    private final String TIME_SEPARATER = ":";

    public static final int MODE_NEW = 1;

    public static final int MODE_EVENT = 2;//更新event表中事件

    public static final int MODE_DONE = 3;

    public static final int MODE_TOP = 4;

    private int mode = -1;

    class LabelListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return labelList.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if(position < labelList.size())
                return labelList.get(position);
            else
                return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout linearLayout = new LinearLayout(EventActivity.this);
            linearLayout.setPadding(50, 20, 20, 20);
            TextView labelColor = new TextView(EventActivity.this);
            labelColor.setText(getString(R.string.label_color));
            TextView labelName = new TextView(EventActivity.this);
            if(position < labelList.size()) {
                labelColor.setTextColor(getResources().getColor(labelList.get(position).getColorId()));
                labelName.setText(labelList.get(position).getName());
            } else {
                labelName.setText(getString(R.string.label_none));
            }
            labelColor.setTextSize(16);
            labelName.setTextSize(16);
            labelColor.setPadding(0, 0, 20, 0);
            linearLayout.addView(labelColor);
            linearLayout.addView(labelName);
            return linearLayout;
        }
    }

    public static void startEventActivity(Event event, Context context, int mode){
        Intent intent = new Intent();
        intent.setClass(context, EventActivity.class);
        intent.putExtra(INTENT_NAME, event);
        intent.putExtra(INTENT_MODE_NAME, mode);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            Log.e("EventActivity", "actionbar == null");
        }

        dbHelper = new MyDatabaseHelper(this, "ToDo.db", null, 2);

        Intent intent = getIntent();

        this.mode = intent.getIntExtra(INTENT_MODE_NAME, -1);
        if(this.mode == -1) {
            Log.e("ToDo", "starting event activity with no mode!");
        }
        if(mode != MODE_NEW) {
            Event event = (Event) intent.getSerializableExtra(INTENT_NAME);
            if(event != null) {
                isNew = false;
                eventStr = event.getEvent();
                note = event.getNote();
                deadline = event.getDeadline();
                reminders = event.getReminders();
                if(event.getLabelId() != 0)
                    label = dbHelper.getLabel(event.getLabelId(), dbHelper);
                originalEvent = event;
            }
        }

        initViews();

        Log.v("ToDo", "initLabelList");
        initLabelList();
        adapter = new LabelListAdapter();

    }

//    @Override
//    public void onBackPressed() {
//        Intent homeIntent = new Intent(EventActivity.this, MainActivity.class);
//        startActivity(homeIntent);
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        finish();
//    }

    private void initLabelList() {
        labelList = dbHelper.readFromLabel(dbHelper);
        Log.v("ToDo", "label list size : " + labelList.size());
    }

    private void initViews() {
        eventEditText = (EditText) this.findViewById(R.id.event_name_edittext);
        noteEditText = (EditText) this.findViewById(R.id.event_note_edittext);
        deadlineEditText = (EditText) this.findViewById(R.id.event_deadline_edittext);
        reminderLayout1 = (LinearLayout) this.findViewById(R.id.event_reminders_layout_1);
        reminderLayout2 = (LinearLayout) this.findViewById(R.id.event_reminders_layout_2);
        reminderLayout3 = (LinearLayout) this.findViewById(R.id.event_reminders_layout_3);
        reminderEditText1 = (EditText) this.findViewById(R.id.event_reminders_edittext_1);
        reminderEditText2 = (EditText) this.findViewById(R.id.event_reminders_edittext_2);
        reminderEditText3 = (EditText) this.findViewById(R.id.event_reminders_edittext_3);
        reminderCancel1 = (ImageView) this.findViewById(R.id.event_reminders_cancel_1);
        reminderCancel2 = (ImageView) this.findViewById(R.id.event_reminders_cancel_2);
        reminderCancel3 = (ImageView) this.findViewById(R.id.event_reminders_cancel_3);
        reminderAdd = (ImageView) this.findViewById(R.id.event_reminders_add);
        labelColorTextview = (TextView) this.findViewById(R.id.new_event_label_color_textview);
        labelNameTextview = (TextView) this.findViewById(R.id.new_event_label_name_textview);
        deadlineWarningTextView = (TextView) this.findViewById(R.id.deadline_warning_textview);
        eventWarningTextView = (TextView) this.findViewById(R.id.event_name_warning_textview);
        reminderWarning1 = (TextView) this.findViewById(R.id.reminder_warning_textview_1);
        reminderWarning2 = (TextView) this.findViewById(R.id.reminder_warning_textview_2);
        reminderWarning3 = (TextView) this.findViewById(R.id.reminder_warning_textview_3);

        eventEditText.setOnFocusChangeListener(new MyOnFocusChangeListener());
        noteEditText.setOnFocusChangeListener(new MyOnFocusChangeListener());
        deadlineEditText.setOnFocusChangeListener(new MyOnFocusChangeListener());
        reminderEditText1.setOnFocusChangeListener(new MyOnFocusChangeListener());
        reminderEditText2.setOnFocusChangeListener(new MyOnFocusChangeListener());
        reminderEditText3.setOnFocusChangeListener(new MyOnFocusChangeListener());

        eventEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.getBackground().setColorFilter(getResources().getColor(R.color.light_green), PorterDuff.Mode.SRC_ATOP);
                } else {
                    v.getBackground().setColorFilter(getResources().getColor(R.color.font_event), PorterDuff.Mode.SRC_ATOP);
                }
            }
        });

        //must change the EdittextOnclickListener
        deadlineEditText.setOnClickListener(new MyDateAndTimeOnClickListener(deadlineEditText));
        reminderEditText1.setOnClickListener(new MyDateAndTimeOnClickListener(reminderEditText1));
        reminderEditText2.setOnClickListener(new MyDateAndTimeOnClickListener(reminderEditText2));
        reminderEditText3.setOnClickListener(new MyDateAndTimeOnClickListener(reminderEditText3));

        reminderCancel1.setOnClickListener(new MyCancelButtonOnClickListener());
        reminderCancel2.setOnClickListener(new MyCancelButtonOnClickListener());
        reminderCancel3.setOnClickListener(new MyCancelButtonOnClickListener());

        deadlineEditText.addTextChangedListener(new MyTextWatcher(deadlineWarningTextView));
        eventEditText.addTextChangedListener(new MyTextWatcher(eventWarningTextView));
        reminderEditText1.addTextChangedListener(new MyTextWatcher(reminderWarning1));
        reminderEditText2.addTextChangedListener(new MyTextWatcher(reminderWarning2));
        reminderEditText3.addTextChangedListener(new MyTextWatcher(reminderWarning3));

        reminderAdd.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(isLayout2Displayed) {
                    reminderLayout3.setVisibility(View.VISIBLE);
                    isLayout3Displayed = true;
                    reminderAdd.setVisibility(View.GONE);
                    isAddDisplayed = false;
                } else if(isLayout1Displayed) {
                    reminderLayout2.setVisibility(View.VISIBLE);
                    isLayout2Displayed = true;
                } else {
                    reminderLayout1.setVisibility(View.VISIBLE);
                    isLayout1Displayed = true;
                }
            }
        });

        labelNameTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                labelListView = new ListView(EventActivity.this);
                labelListView.setAdapter(adapter);
                final AlertDialog dialog = new AlertDialog.Builder(EventActivity.this).setTitle(getString(R.string.choose_label))
                        .setView(labelListView).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                labelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(position < labelList.size()){
                            labelId = labelList.get(position).getLabelId();
                            labelColorTextview.setTextColor(getResources().getColor(labelList.get(position).getColorId()));
                            labelNameTextview.setText(labelList.get(position).getName());
                        } else {
                            labelId = 0;
                            labelColorTextview.setTextColor(getResources().getColor(R.color.font_event));
                            labelNameTextview.setText(getString(R.string.label_none));
                        }
                        dialog.dismiss();
                    }
                });
                WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                layoutParams.width = 200;
                layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setAttributes(layoutParams);
                dialog.show();
            }
        });

        if(!isNew){
            eventEditText.setText(eventStr);
            noteEditText.setText(note);
            deadlineEditText.setText((deadline.get(Calendar.MONTH) + 1) + DATE_SEPARATER + deadline.get(Calendar.DAY_OF_MONTH) + DATE_SEPARATER +
                    deadline.get(Calendar.YEAR) + " " + encodeHourOrMinute(deadline.get(Calendar.HOUR_OF_DAY)) + TIME_SEPARATER +
                    encodeHourOrMinute(deadline.get(Calendar.MINUTE)));
            Log.v("ToDo", "EventActivity : reminder size : " + reminders.size());
            switch (reminders.size()){
                case 0:

                    break;

                case 1:
                    reminderEditText1.setText((reminders.get(0).get(Calendar.MONTH) + 1) + DATE_SEPARATER + reminders.get(0).get(Calendar.DAY_OF_MONTH) +
                            DATE_SEPARATER + reminders.get(0).get(Calendar.YEAR) + " " + reminders.get(0).get(Calendar.HOUR_OF_DAY) +
                            TIME_SEPARATER + encodeHourOrMinute(reminders.get(0).get(Calendar.MINUTE)));
                    break;

                case 2:
                    reminderLayout2.setVisibility(View.VISIBLE);
                    isLayout2Displayed = true;
                    reminderEditText1.setText((reminders.get(0).get(Calendar.MONTH) + 1) + DATE_SEPARATER + reminders.get(0).get(Calendar.DAY_OF_MONTH) +
                            DATE_SEPARATER + reminders.get(0).get(Calendar.YEAR) + " " + reminders.get(0).get(Calendar.HOUR_OF_DAY) +
                            TIME_SEPARATER + encodeHourOrMinute(reminders.get(0).get(Calendar.MINUTE)));
                    reminderEditText2.setText((reminders.get(1).get(Calendar.MONTH) + 1) + DATE_SEPARATER + reminders.get(1).get(Calendar.DAY_OF_MONTH) +
                            DATE_SEPARATER + reminders.get(1).get(Calendar.YEAR) + " " + encodeHourOrMinute(reminders.get(1).get(Calendar.HOUR_OF_DAY)) +
                            TIME_SEPARATER + encodeHourOrMinute(reminders.get(1).get(Calendar.MINUTE)));
                    break;

                case 3:
                    reminderLayout2.setVisibility(View.VISIBLE);
                    isLayout2Displayed = true;
                    reminderLayout3.setVisibility(View.VISIBLE);
                    isLayout3Displayed = true;
                    reminderAdd.setVisibility(View.GONE);
                    isAddDisplayed = false;
                    reminderEditText1.setText((reminders.get(0).get(Calendar.MONTH) + 1) + DATE_SEPARATER + reminders.get(0).get(Calendar.DAY_OF_MONTH) +
                            DATE_SEPARATER + reminders.get(0).get(Calendar.YEAR) + " " + reminders.get(0).get(Calendar.HOUR_OF_DAY) +
                            TIME_SEPARATER + encodeHourOrMinute(reminders.get(0).get(Calendar.MINUTE)));
                    reminderEditText2.setText((reminders.get(1).get(Calendar.MONTH) + 1) + DATE_SEPARATER + reminders.get(1).get(Calendar.DAY_OF_MONTH) +
                            DATE_SEPARATER + reminders.get(1).get(Calendar.YEAR) + " " + encodeHourOrMinute(reminders.get(1).get(Calendar.HOUR_OF_DAY)) +
                            TIME_SEPARATER + encodeHourOrMinute(reminders.get(1).get(Calendar.MINUTE)));
                    reminderEditText3.setText((reminders.get(2).get(Calendar.MONTH) + 1) + DATE_SEPARATER + reminders.get(2).get(Calendar.DAY_OF_MONTH) +
                            DATE_SEPARATER + reminders.get(2).get(Calendar.YEAR) + " " + encodeHourOrMinute(reminders.get(2).get(Calendar.HOUR_OF_DAY)) +
                            TIME_SEPARATER + encodeHourOrMinute(reminders.get(2).get(Calendar.MINUTE)));
                    break;
            }
            if(label != null){
                labelNameTextview.setText(label.getName());
                labelColorTextview.setTextColor(getResources().getColor(label.getColorId()));
                labelId = label.getLabelId();
            }
        }
    }

    class MyCancelButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.event_reminders_cancel_1:
                    if(isLayout3Displayed) {
                        reminderEditText1.setText(reminderEditText2.getText());
                        reminderEditText2.setText(reminderEditText3.getText());
                        reminderEditText3.setText("");
                        reminderLayout3.setVisibility(View.GONE);
                        isLayout3Displayed = false;
                        reminderAdd.setVisibility(View.VISIBLE);
                        isAddDisplayed = true;
                    } else if(isLayout2Displayed) {
                        reminderEditText1.setText(reminderEditText2.getText());
                        reminderEditText2.setText("");
                        reminderLayout2.setVisibility(View.GONE);
                        isLayout2Displayed = false;
                    } else {
                        reminderEditText1.setText("");
                        reminderLayout1.setVisibility(View.GONE);
                        isLayout1Displayed = false;
                    }
                    break;

                case R.id.event_reminders_cancel_2:
                    if(isLayout3Displayed) {
                        reminderEditText2.setText(reminderEditText3.getText());
                        reminderEditText3.setText("");
                        reminderLayout3.setVisibility(View.GONE);
                        isLayout3Displayed = false;
                        reminderAdd.setVisibility(View.VISIBLE);
                        isAddDisplayed = true;
                    } else {
                        reminderEditText2.setText("");
                        reminderLayout2.setVisibility(View.GONE);
                        isLayout2Displayed = false;
                    }
                    break;

                case R.id.event_reminders_cancel_3:
                    reminderEditText3.setText("");
                    reminderLayout3.setVisibility(View.GONE);
                    isLayout3Displayed = false;
                    reminderAdd.setVisibility(View.VISIBLE);
                    isAddDisplayed = true;
                    break;
            }
        }
    }

    class MyDateAndTimeOnClickListener implements View.OnClickListener {

        private EditText editText;

        public MyDateAndTimeOnClickListener(EditText editText){
            this.editText = editText;
        }

        @Override
        public void onClick(View v) {
            final String[] dateAndTime = new String[1];
            String[] dateAndTimeEdit = editText.getText().toString().trim().split(" ");
            final boolean[] isTimeDialogShown = {false};

            //initialize date picker
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH);
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            String deadlineDate = editText.getText().toString().trim();
            if(!deadlineDate.equals("")){
                String[] date = dateAndTimeEdit[0].split(DATE_SEPARATER);
                month = Integer.parseInt(date[0]) - 1;
                day = Integer.parseInt(date[1]);
                year = Integer.parseInt(date[2]);
            }

            //initialize time picker
            final int HOUR;
            final int MINUTE;
            final String deadlineTime = editText.getText().toString().trim();
            if(!deadlineTime.equals("")){
                String[] time = dateAndTimeEdit[1].split(TIME_SEPARATER);
                HOUR = Integer.parseInt(time[0]);
                MINUTE = Integer.parseInt(time[1]);
            } else {
                HOUR = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                MINUTE = Calendar.getInstance().get(Calendar.MINUTE);
            }

            new DatePickerDialog(EventActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    dateAndTime[0] = (monthOfYear + 1) + DATE_SEPARATER + dayOfMonth + DATE_SEPARATER + year;

                    //start the time picker dialog
                    if(!isTimeDialogShown[0]) {
                        new TimePickerDialog(EventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                editText.setText(dateAndTime[0] + " " + encodeHourOrMinute(hourOfDay) + TIME_SEPARATER + encodeHourOrMinute(minute));
                            }
                        }, HOUR, MINUTE, true).show();
                        isTimeDialogShown[0] = true;
                    }
                }
            }, year, month, day).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
//                Intent intent = new Intent(EventActivity.this, MainActivity.class);
//                startActivity(intent);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;

            case R.id.item_ok:

                if(eventEditText.getText().toString().trim().equals("")){
                    eventWarningTextView.setVisibility(View.VISIBLE);
                    Animation shake = AnimationUtils.loadAnimation(EventActivity.this, R.anim.shake);
                    eventEditText.startAnimation(shake);
                } else if (deadlineEditText.getText().toString().trim().equals("")){
                    //must rewrite the action
                    deadlineWarningTextView.setVisibility(View.VISIBLE);
                    Animation shake = AnimationUtils.loadAnimation(EventActivity.this, R.anim.shake);
                    deadlineEditText.startAnimation(shake);
                } else if(isLayout1Displayed && isReminderAfterDeadline(reminderEditText1, deadlineEditText)) {
                    reminderWarning1.setVisibility(View.VISIBLE);
                    Animation shake = AnimationUtils.loadAnimation(EventActivity.this, R.anim.shake);
                    reminderEditText1.startAnimation(shake);
                } else if(isLayout2Displayed && isReminderAfterDeadline(reminderEditText2, deadlineEditText)) {
                    reminderWarning2.setVisibility(View.VISIBLE);
                    Animation shake = AnimationUtils.loadAnimation(EventActivity.this, R.anim.shake);
                    reminderEditText2.startAnimation(shake);
                } else if(isLayout2Displayed && isReminderAfterDeadline(reminderEditText2, deadlineEditText)) {
                    reminderWarning2.setVisibility(View.VISIBLE);
                    Animation shake = AnimationUtils.loadAnimation(EventActivity.this, R.anim.shake);
                    reminderEditText2.startAnimation(shake);
                } else {
                    eventStr = eventEditText.getText().toString().trim();
                    note = noteEditText.getText().toString().trim();
                    String deadlineEditStrs[] = deadlineEditText.getText().toString().trim().split(" ");
                    String[] deadlineDate = deadlineEditStrs[0].split("-");
                    deadline = Calendar.getInstance();
                    deadline.set(Calendar.MONTH, Integer.parseInt(deadlineDate[0]) - 1);//month值比正确值少一
                    deadline.set(Calendar.DAY_OF_MONTH, Integer.parseInt(deadlineDate[1]));
                    deadline.set(Calendar.YEAR, Integer.parseInt(deadlineDate[2]));
                    String[] deadlineTime = deadlineEditStrs[1].split(":");
                    deadline.set(Calendar.HOUR_OF_DAY, Integer.parseInt(decodeHourOrMinute(deadlineTime[0])));
                    deadline.set(Calendar.MINUTE, Integer.parseInt(decodeHourOrMinute(deadlineTime[1])));
                    reminders.clear();
                    Log.v("ToDo", "EventActivity : layout1:" + isLayout1Displayed + " layout2:" + isLayout2Displayed + " layout3:" + isLayout3Displayed);
                    if(isLayout1Displayed && !reminderEditText1.getText().toString().trim().equals("")){
                        String[] reminder1Strs = reminderEditText1.getText().toString().trim().split(" ");
                        String[] reminder1Date = reminder1Strs[0].split("-");
                        Calendar reminder1 = Calendar.getInstance();
                        reminder1.set(Calendar.MONTH, Integer.parseInt(reminder1Date[0]) - 1);
                        reminder1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(reminder1Date[1]));
                        reminder1.set(Calendar.YEAR, Integer.parseInt(reminder1Date[2]));
                        String[] reminder1Time = reminder1Strs[1].split(":");
                        reminder1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(decodeHourOrMinute(reminder1Time[0])));
                        reminder1.set(Calendar.MINUTE, Integer.parseInt(decodeHourOrMinute(reminder1Time[1])));
                        reminders.add(reminder1);
                        Log.v("ToDo", "EventActivity : add reminder1");
                    }
                    if(isLayout2Displayed && !reminderEditText2.getText().toString().trim().equals("")){
                        String[] reminder2Strs = reminderEditText2.getText().toString().trim().split(" ");
                        String[] reminder2Date = reminder2Strs[0].split("-");
                        Calendar reminder2 = Calendar.getInstance();
                        reminder2.set(Calendar.MONTH, Integer.parseInt(reminder2Date[0]) - 1);
                        reminder2.set(Calendar.DAY_OF_MONTH, Integer.parseInt(reminder2Date[1]));
                        reminder2.set(Calendar.YEAR, Integer.parseInt(reminder2Date[2]));
                        String[] reminder2Time = reminder2Strs[1].split(":");
                        reminder2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(decodeHourOrMinute(reminder2Time[0])));
                        reminder2.set(Calendar.MINUTE, Integer.parseInt(decodeHourOrMinute(reminder2Time[1])));
                        reminders.add(reminder2);
                        Log.v("ToDo", "EventActivity : add reminder2");
                    }
                    if(isLayout3Displayed && !reminderEditText3.getText().toString().trim().equals("")){
                        String[] reminder3Strs = reminderEditText3.getText().toString().trim().split(" ");
                        String[] reminder3Date = reminder3Strs[0].split("-");
                        Calendar reminder3 = Calendar.getInstance();
                        reminder3.set(Calendar.MONTH, Integer.parseInt(reminder3Date[0]) - 1);
                        reminder3.set(Calendar.DAY_OF_MONTH, Integer.parseInt(reminder3Date[1]));
                        reminder3.set(Calendar.YEAR, Integer.parseInt(reminder3Date[2]));
                        String[] reminder3Time = reminder3Strs[1].split(":");
                        reminder3.set(Calendar.HOUR_OF_DAY, Integer.parseInt(decodeHourOrMinute(reminder3Time[0])));
                        reminder3.set(Calendar.MINUTE, Integer.parseInt(decodeHourOrMinute(reminder3Time[1])));
                        reminders.add(reminder3);
                        Log.v("ToDo", "EventActivity : add reminder3");
                    }
                    originalEvent.setEvent(eventStr);
                    originalEvent.setNote(note);
                    originalEvent.setDeadline(deadline);
                    originalEvent.setReminders(reminders);
                    originalEvent.setLabelId(labelId);
                    switch (mode) {
                        case MODE_NEW:
                            dbHelper.addEvent(originalEvent, dbHelper);
                            break;

                        case MODE_EVENT:
                            dbHelper.updateEvent(originalEvent, dbHelper);
                            break;

                        case MODE_DONE:
                            dbHelper.updateDone(originalEvent, dbHelper);
                            break;

                        case MODE_TOP:
                            dbHelper.updateTop(originalEvent, dbHelper);
                            break;

                        default:
                            Toast.makeText(EventActivity.this, "MODE ERROR!", Toast.LENGTH_LONG).show();
                            break;
                    }
//                    Intent homeIntent = new Intent(EventActivity.this, MainActivity.class);
//                    startActivity(homeIntent);
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public String encodeHourOrMinute(int hourOrMinute){
        if((hourOrMinute + "").length() == 1){
            return "0" + hourOrMinute;
        } else {
            return hourOrMinute + "";
        }
    }

    public String decodeHourOrMinute(String hourOrMinute){
        if(hourOrMinute.startsWith("0")){
            return hourOrMinute.substring(1);
        } else {
            return hourOrMinute;
        }
    }

    public boolean isReminderAfterDeadline(EditText reminderEdit, EditText deadlineEdit){
        boolean isAfter = false;
        if(reminderEdit.getText().toString().trim().equals("")) {
            return isAfter;
        }

        String reminderStr = reminderEdit.getText().toString().trim();
        String deadlineStr = deadlineEdit.getText().toString().trim();
        String[] reminderStrs = reminderStr.split(" ");
        String[] deadlineStrs = deadlineStr.split(" ");
        String[] reminderDateStrs = reminderStrs[0].split("-");
        String[] reminderTimeStrs = reminderStrs[1].split(":");
        String[] deadlineDateStrs = deadlineStrs[0].split("-");
        String[] deadlineTimeStrs = deadlineStrs[1].split(":");

        if(Integer.parseInt(reminderDateStrs[2]) > Integer.parseInt(deadlineDateStrs[2])){
            Log.v("isAfterLog", "year -->> reminder:" + reminderDateStrs[2] + ";deadline:" + deadlineDateStrs[2]);
            isAfter = true;
            return isAfter;
        } else if(Integer.parseInt(reminderDateStrs[2]) < Integer.parseInt(deadlineDateStrs[2])) {
            return isAfter;
        } else if(Integer.parseInt(reminderDateStrs[0]) > Integer.parseInt(deadlineDateStrs[0])) {
            Log.v("isAfterLog", "month -->> reminder:" + reminderDateStrs[0] + ";deadline:" + deadlineDateStrs[0]);
            isAfter = true;
            return isAfter;
        } else if(Integer.parseInt(reminderDateStrs[0]) < Integer.parseInt(deadlineDateStrs[0])) {
            return isAfter;
        }  else if(Integer.parseInt(reminderDateStrs[1]) > Integer.parseInt(deadlineDateStrs[1])) {
            Log.v("isAfterLog", "day -->> reminder:" + reminderDateStrs[1] + ";deadline:" + deadlineDateStrs[1]);
            isAfter = true;
            return isAfter;
        } else if(Integer.parseInt(reminderDateStrs[1]) < Integer.parseInt(deadlineDateStrs[1])) {
            return isAfter;
        }  else if(Integer.parseInt(reminderTimeStrs[0]) > Integer.parseInt(deadlineTimeStrs[0])) {
            Log.v("isAfterLog", "hour -->> reminder:" + reminderTimeStrs[0] + ";deadline:" + deadlineTimeStrs[0]);
            isAfter = true;
            return isAfter;
        } else if(Integer.parseInt(reminderTimeStrs[0]) < Integer.parseInt(deadlineTimeStrs[0])) {
            return isAfter;
        }  else if(Integer.parseInt(reminderTimeStrs[1]) > Integer.parseInt(deadlineTimeStrs[1])) {
            Log.v("isAfterLog", "minute -->> reminder:" + reminderTimeStrs[1] + ";deadline:" + deadlineTimeStrs[1]);
            isAfter = true;
            return isAfter;
        }
        Log.v("isAfterLog", "isAfter=" + isAfter);
        return isAfter;
    }

    class MyOnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                v.getBackground().setColorFilter(getResources().getColor(R.color.light_green), PorterDuff.Mode.SRC_ATOP);
            } else {
                v.getBackground().setColorFilter(getResources().getColor(R.color.font_event), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    class MyTextWatcher implements TextWatcher {
        private TextView warningTextView;

        public MyTextWatcher(TextView textView){
            this.warningTextView = textView;
        }

        //prevents from using non-parameter constructor
        private MyTextWatcher(){}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            this.warningTextView.setVisibility(View.GONE);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}
