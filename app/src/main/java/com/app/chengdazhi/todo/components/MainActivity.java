package com.app.chengdazhi.todo.components;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.app.chengdazhi.todo.R;
import com.app.chengdazhi.todo.models.Label;
import com.app.chengdazhi.todo.tools.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * features to be added or improved:
 * 1. able to add a window in the system menu
 *      guide: http://developer.android.com/guide/topics/appwidgets/index.html
 *      design: http://developer.android.com/design/patterns/widgets.html
 * 2. login and backup online
 * 3. add a notification to quickly add a new event √
 * 4. enable group edit
 * 5. enable photo usage (to be considered)
 * 6. enable setting event to top
 *      update database
 *      option1: make the done button to right swipe gesture.
 *      option2: single click to edit and change edit button to atop.
 * 7. refresh(for backup online)
 *      a. add material refresh button
 *      b. use google's pull to refresh
 * 8. settings:
 *      notification can appear as a notification or a text
 *      notification's sound and vibrate
 *      account information
 *      about(to be considered)
 * 9. the edittext should be of material design style (not popping out dialogs when wrong) √
 *      change the bottom line color √
 *      add warning below the edittext √
 *      add animation if possible √
 * 10. make the date and time edittext in the same edittext √
 *      use material date and time picker dialog if possible.
 * 11. add custom activity on crash
 * 12. single acitivity mode  √
 * 13. backup the data to the sdcard(optional)
 * 14. swipe right to exit new activity
 * 15. creating api to share with other apps
 * 16. add a new logo to the adding notification.
 *      show the specifics and even some actions when only one task is not done on time.
 * 17. add animations
 *
 * bugs:
 * 1. the reminder time must be before the deadline √
 * 2. the reminder notification may not show when in background
 * 3. change the color of the status bar in the New Event
 * 4. enhance the search function
 *
 * things to be done:
 * 1. publish to wandoujai and google play   √
 * 2. complete the documentation
 * 2. write a read me and publish is on github
 *
 * libraries:
 * 1. neokree material drawer
 * 2. baoyz swipe menu listview
 *
 * prospective libraries:
 * 1. rey5137/material
 * 2. navasmdc/MaterialDesignLibrary
 * 3. Ereza/CustomActivityOnCrash
 */
public class MainActivity extends MaterialNavigationDrawer {
    //Do not call onCreate() and setContentView()

    private MyDatabaseHelper dbHelper;

    public MaterialSection<EventFragment> eventSection;

    public MaterialSection<DoneFragment> doneSection;

    public List<MaterialSection<EventFragment>> sectionList = new ArrayList<MaterialSection<EventFragment>>();

    public MaterialSection<ManageLabelFragment> manageLabelSection;

    public MaterialSection settingsSection;

    //解决内存泄漏
    private EventFragment eventFragment = null;

    private ManageLabelFragment manageLabelFragment = null;

    private DoneFragment doneFragment = null;

    private List<EventFragment> labelFragmentList = new ArrayList<EventFragment>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.item_search:
                SearchActivity.startSearchActivity(MainActivity.this);
                break;

            //starts a activity to add a new task
            case R.id.item_new:
                EventActivity.startEventActivity(null, MainActivity.this, EventActivity.MODE_NEW);

//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            default:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //init() sets the components of the drawer
    @Override
    public void init(Bundle bundle) {
        dbHelper = new MyDatabaseHelper(this, "ToDo.db", null, 2);

        if(eventFragment == null) {
            eventFragment = new EventFragment();
        }
        eventSection = newSection(getString(R.string.ongoing), R.drawable.ic_action_time, eventFragment);
        int eventNotificationNumber = dbHelper.readFromEvent(dbHelper).size() + dbHelper.readFromTop(dbHelper).size();
        eventSection.setNotifications(eventNotificationNumber);
        addSection(eventSection);

        if(sectionList != null) {
            sectionList.clear();
        }

        List<Label> labelList = dbHelper.readFromLabel(dbHelper);
        for(Label label : labelList){

            boolean isInList = false;

            if(labelFragmentList.size() > 0) {
                for (EventFragment labelFrag : labelFragmentList) {
                    if (labelFrag.getFilterLabelId() == label.getLabelId()) {
                        Log.d("new event fragment", "in list, labelId:" + label.getLabelId()
                                + ";fragment id:" + labelFrag.getFilterLabelId());
                        isInList = true;
                        MaterialSection<EventFragment> labelSection = newSection(label.getName(),
                                R.drawable.ic_action_labels, labelFrag);
                        labelSection.setSectionColor(getResources().getColor(label.getColorId()));
                        int labelNumber = dbHelper.filter(label.getLabelId(), dbHelper).size()
                                + dbHelper.filterTop(label.getLabelId(), dbHelper).size();
                        labelSection.setNotifications(labelNumber);
                        sectionList.add(labelSection);
                    }
                }
            }

            if(!isInList) {
                Log.d("new event fragment", "not in list, creating... labelId:" + label.getLabelId());
                EventFragment labelFragment = new EventFragment();
                labelFragment.setFilterLabelId(label.getLabelId());
                labelFragmentList.add(labelFragment);
                MaterialSection<EventFragment> labelSection = newSection(label.getName(),
                        R.drawable.ic_action_labels, labelFragment);
                labelSection.setSectionColor(getResources().getColor(label.getColorId()));
                int labelNumber = dbHelper.filter(label.getLabelId(), dbHelper).size()
                        + dbHelper.filterTop(label.getLabelId(), dbHelper).size();
                labelSection.setNotifications(labelNumber);
                sectionList.add(labelSection);
            }
/*
            EventFragment labelFragment = new EventFragment();
            labelFragment.setFilterLabelId(label.getLabelId());
            MaterialSection labelSection = newSection(label.getName(), R.drawable.ic_action_labels, labelFragment);
            labelSection.setSectionColor(getResources().getColor(label.getColorId()));
            int labelNumber = dbHelper.filter(label.getLabelId(), dbHelper).size()
                    + dbHelper.filterTop(label.getLabelId(), dbHelper).size();
            labelSection.setNotifications(labelNumber);
            sectionList.add(labelSection);
            */
        }
        for(MaterialSection section : sectionList){
            addSection(section);
        }

        if(doneFragment == null) {
            doneFragment = new DoneFragment();
        }
        doneSection = newSection(getString(R.string.done), R.drawable.ic_action_accept, doneFragment);
        int doneNotificationNumber = dbHelper.readFromDone(dbHelper).size();
        doneSection.setNotifications(doneNotificationNumber);
        addSection(doneSection);

        if(manageLabelFragment == null) {
            manageLabelFragment = new ManageLabelFragment();
        }
        manageLabelSection = newSection(getString(R.string.manage_labels), R.drawable.ic_action_new_label, manageLabelFragment);
        addSection(manageLabelSection);

        settingsSection = newSection(getString(R.string.settings), R.drawable.ic_action_settings,
                new Intent(MainActivity.this, SettingsActivity.class));
        addSection(settingsSection);
    }

    //reInit() updates the drawer in case the numbers and the labels change after action.
    public void reInit(){
        removeSection(eventSection);
        removeSection(doneSection);
        for(MaterialSection section : sectionList) {
            removeSection(section);
        }
        removeSection(manageLabelSection);
        removeSection(settingsSection);
        init(null);
    }

    //calls reInit(). In this way, drawer is refreshed every time MainActivity is shown.
    @Override
    protected void onResume() {
        super.onResume();
        reInit();
        Intent intent = new Intent(MainActivity.this, AlarmService.class);
        startService(intent);
        Log.v("ToDoMain", "onResume");

        //to check the heap size: 96MB
//        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
//        int heapSize = manager.getMemoryClass();
//        Log.v("HeapSize", heapSize + "");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("ToDoMain", "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("ToDoMain", getIntent().getAction());
        Log.v("ToDoMain", "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("ToDoMain", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        finish();
        Log.v("ToDoMain", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("MainActivity", "onDestroy");
    }
}
