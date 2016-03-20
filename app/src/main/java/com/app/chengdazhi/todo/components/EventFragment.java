package com.app.chengdazhi.todo.components;


import android.app.Fragment;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.chengdazhi.todo.R;
import com.app.chengdazhi.todo.models.Event;
import com.app.chengdazhi.todo.models.Label;
import com.app.chengdazhi.todo.tools.EventUtil;
import com.app.chengdazhi.todo.tools.MyDatabaseHelper;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    private int filterLabelId = 0;

    private SwipeMenuListView listView;

    private EventAdapter adapter;

    private MyDatabaseHelper dbHelper;

    private List<Event> totalList;

    private int color;

    private TextView blankTitleTextview;

    private TextView blankAddText;

    private TextView blankAddPicture;

    private View eventView;

    private LinearLayout blankLinearLayout;

    private int topCount;

    public final int VIEWTYPE_TOP = 1;

    public final int VIEWTYPE_EVENT = 2;

    private int colorArray[] = {0x8B, 0xC3, 0x4A, 0x4C, 0xAF, 0x50, 0x38, 0x8E, 0x3C};

    public EventFragment() {
        // Required empty public constructor
    }

    public void setFilterLabelId(int labelId) {
        this.filterLabelId = labelId;

    }

    public int getFilterLabelId(){
        return filterLabelId;
    }

    //方便刷新fragment中的数据
    @Override
    public void onResume() {
        super.onResume();
        initList();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //处理内存泄露问题
        dbHelper = new MyDatabaseHelper(getActivity(), "ToDo.db", null, 2);
        adapter = new EventAdapter();

        topCount = 0;


    }

    private void initColorArray() {
        Log.d("EventFragment", "labelid:" + filterLabelId);
        Label label = dbHelper.getLabel(filterLabelId, dbHelper);
        int colorId = label.getColorId();
        switch(colorId) {
            case R.color.light_green:
                colorArray[0] = 0x8B;
                colorArray[1] = 0xC3;
                colorArray[2] = 0x4A;
                colorArray[3] = 0x4C;
                colorArray[4] = 0xAF;
                colorArray[5] = 0x50;
                colorArray[6] = 0x38;
                colorArray[7] = 0x8E;
                colorArray[8] = 0x3C;
                break;

            case R.color.light_yellow:
                colorArray[0] = 0xFF;
                colorArray[1] = 0xEB;
                colorArray[2] = 0x3B;
                colorArray[3] = 0xFF;
                colorArray[4] = 0xC1;
                colorArray[5] = 0x07;
                colorArray[6] = 0xFF;
                colorArray[7] = 0xA0;
                colorArray[8] = 0x00;
                break;

            case R.color.blue:
                colorArray[0] = 0x21;
                colorArray[1] = 0x96;
                colorArray[2] = 0xF3;
                colorArray[3] = 0x44;
                colorArray[4] = 0x8A;
                colorArray[5] = 0xFF;
                colorArray[6] = 0x19;
                colorArray[7] = 0x76;
                colorArray[8] = 0xD2;
                break;

            case R.color.light_red:
                colorArray[0] = 0xFF;
                colorArray[1] = 0x52;
                colorArray[2] = 0x52;
                colorArray[3] = 0xF4;
                colorArray[4] = 0x43;
                colorArray[5] = 0x36;
                colorArray[6] = 0xD3;
                colorArray[7] = 0x2F;
                colorArray[8] = 0x2F;
                break;

            case R.color.orange:
                colorArray[0] = 0xFF;
                colorArray[1] = 0x98;
                colorArray[2] = 0x00;
                colorArray[3] = 0xFF;
                colorArray[4] = 0x57;
                colorArray[5] = 0x22;
                colorArray[6] = 0xE6;
                colorArray[7] = 0x4A;
                colorArray[8] = 0x19;
                break;

            case R.color.brown:
                colorArray[0] = 0x9E;
                colorArray[1] = 0x9E;
                colorArray[2] = 0x9E;
                colorArray[3] = 0x79;
                colorArray[4] = 0x55;
                colorArray[5] = 0x48;
                colorArray[6] = 0x5D;
                colorArray[7] = 0x40;
                colorArray[8] = 0x37;
                break;


            case R.color.dark_purple:
                colorArray[0] = 0x7C;
                colorArray[1] = 0x4D;
                colorArray[2] = 0xFF;
                colorArray[3] = 0x67;
                colorArray[4] = 0x3A;
                colorArray[5] = 0xB7;
                colorArray[6] = 0x51;
                colorArray[7] = 0x2D;
                colorArray[8] = 0xA8;
                break;

            case R.color.pink:
                colorArray[0] = 0xFF;
                colorArray[1] = 0x40;
                colorArray[2] = 0x81;
                colorArray[3] = 0xE9;
                colorArray[4] = 0x1E;
                colorArray[5] = 0x63;
                colorArray[6] = 0xC2;
                colorArray[7] = 0x18;
                colorArray[8] = 0x5B;
                break;

            default:
                Log.e("ToDo", "Event Fragment color not right!");
                break;

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        initList();

        eventView = inflater.inflate(R.layout.fragment_event, null);

        blankLinearLayout = (LinearLayout) eventView.findViewById(R.id.blank_linearlayout);
        blankTitleTextview = (TextView) eventView.findViewById(R.id.blank_title);
        blankAddText = (TextView) eventView.findViewById(R.id.blank_add_text);
        blankAddPicture = (TextView) eventView.findViewById(R.id.blank_add_picture);

        if(filterLabelId != 0){
            Label mLabel = dbHelper.getLabel(filterLabelId, dbHelper);
            blankAddPicture.setTextColor(getResources().getColor(mLabel.getColorId()));
            blankAddText.setText(getString(R.string.label_blank_text));
            blankTitleTextview.setText(getString(R.string.label_blank_title_1) + " " + mLabel.getName() + " " +  getString(R.string.label_blank_title_2));
        } else {
            blankTitleTextview.setText(getString(R.string.event_blank_title));
            blankAddText.setText(getString(R.string.event_blank_text));
            blankAddPicture.setTextColor(getResources().getColor(R.color.light_green));
        }

        blankAddText.setOnClickListener(new BlankOnClickListener());
        blankAddPicture.setOnClickListener(new BlankOnClickListener());

        listView = (SwipeMenuListView) eventView.findViewById(R.id.listView_event);
        listView.setAdapter(adapter);

        if(filterLabelId != 0){
            initColorArray();
        }

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem doneItem, topItem, deleteItem;

                /*
                doneItem = new SwipeMenuItem(getActivity());
                doneItem.setBackground(new ColorDrawable(Color.rgb(139, 195, 74)));
                doneItem.setWidth(dp2px(90));
                doneItem.setIcon(R.drawable.ic_action_accept);
                menu.addMenuItem(doneItem);

                topItem = new SwipeMenuItem(getActivity());
                topItem.setBackground(new ColorDrawable(Color.rgb(255, 235, 59)));
                topItem.setWidth(dp2px(90));
                topItem.setIcon(R.drawable.ic_action_top);
                menu.addMenuItem(topItem);

                deleteItem = new SwipeMenuItem(getActivity());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF4, 0x43, 0x36)));
                deleteItem.setWidth(dp2px(90));
                deleteItem.setIcon(R.drawable.ic_action_discard);
                menu.addMenuItem(deleteItem);
                */

                switch (menu.getViewType()) {
                    case VIEWTYPE_EVENT:
                        doneItem = new SwipeMenuItem(getActivity());
                        doneItem.setBackground(new ColorDrawable(Color.rgb(colorArray[0], colorArray[1], colorArray[2])));
                        doneItem.setWidth(dp2px(90));
                        doneItem.setIcon(R.drawable.ic_action_accept);
                        menu.addMenuItem(doneItem);

                        topItem = new SwipeMenuItem(getActivity());
                        topItem.setBackground(new ColorDrawable(Color.rgb(colorArray[3], colorArray[4], colorArray[5])));
                        topItem.setWidth(dp2px(90));
                        topItem.setIcon(R.drawable.ic_action_top);
                        menu.addMenuItem(topItem);

                        deleteItem = new SwipeMenuItem(getActivity());
                        deleteItem.setBackground(new ColorDrawable(Color.rgb(colorArray[6], colorArray[7], colorArray[8])));
                        deleteItem.setWidth(dp2px(90));
                        deleteItem.setIcon(R.drawable.ic_action_discard);
                        menu.addMenuItem(deleteItem);
                        break;

                    case VIEWTYPE_TOP:
                        doneItem = new SwipeMenuItem(getActivity());
                        doneItem.setBackground(new ColorDrawable(Color.rgb(colorArray[0], colorArray[1], colorArray[2])));
                        doneItem.setWidth(dp2px(90));
                        doneItem.setIcon(R.drawable.ic_action_accept);
                        menu.addMenuItem(doneItem);

                        topItem = new SwipeMenuItem(getActivity());
                        topItem.setBackground(new ColorDrawable(Color.rgb(colorArray[3], colorArray[4], colorArray[5])));
                        topItem.setWidth(dp2px(90));
                        topItem.setIcon(R.drawable.ic_action_untop);
                        menu.addMenuItem(topItem);

                        deleteItem = new SwipeMenuItem(getActivity());
                        deleteItem.setBackground(new ColorDrawable(Color.rgb(colorArray[6], colorArray[7], colorArray[8])));
                        deleteItem.setWidth(dp2px(90));
                        deleteItem.setIcon(R.drawable.ic_action_discard);
                        menu.addMenuItem(deleteItem);
                        break;

                    default:
                        Log.e("ToDo", "VIEW type can be right!!! at EventFragment.java");
                        break;
                }

            }
        };
        listView.setMenuCreator(creator);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= topCount) {
                    EventActivity.startEventActivity(totalList.get(position), getActivity(), EventActivity.MODE_EVENT);
                } else {
                    EventActivity.startEventActivity(totalList.get(position), getActivity(), EventActivity.MODE_TOP);
                }
            }
        });
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // done
                        if (position >= topCount) {
                            dbHelper.eventDone(totalList.get(position).getEventId(), dbHelper);
                        } else {
                            dbHelper.topDone(totalList.get(position).getEventId(), dbHelper);
                        }
                        initList();
                        adapter.notifyDataSetChanged();
                        ((MainActivity) getActivity()).reInit();
                        break;

                    case 1:
                        // atop/de-top
                        if (position >= topCount) {
                            dbHelper.eventTop(totalList.get(position).getEventId(), dbHelper);
                        } else {
                            dbHelper.topEvent(totalList.get(position).getEventId(), dbHelper);
                        }
                        initList();
                        adapter.notifyDataSetChanged();
                        //no need to reInit MainActivity
                        break;

                    case 2:
                        //delete
                        if (position >= topCount) {
                            dbHelper.deleteFromEvent(totalList.get(position).getEventId(), dbHelper);
                        } else {
                            dbHelper.deleteFromTop(totalList.get(position).getEventId(), dbHelper);
                        }
                        initList();
                        adapter.notifyDataSetChanged();
                        ((MainActivity) getActivity()).reInit();
                        break;
                    default:
                        Log.e("ToDo", "ListView menu item not handled, very odd. At EventFragment.java");
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        return eventView;
    }

    public class BlankOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            EventActivity.startEventActivity(null, getActivity(), EventActivity.MODE_NEW);
        }
    }

    //初始化
    private void initList() {
        Log.d("initList", "initList start");
        List<Event> eventList, topList;
        if(filterLabelId == 0){
            color = getActivity().getResources().getColor(R.color.light_green);
            eventList = dbHelper.readFromEvent(dbHelper);
            topList = dbHelper.readFromTop(dbHelper);
            for(Event event : topList) {
                event.setStatus(Event.STATUS_NONE);
            }
            if(topList.size() > 0) {
                topList.get(0).setStatus(Event.STATUS_TOP);
            }
        } else {
            color = getActivity().getResources().getColor(dbHelper.getLabel(filterLabelId, dbHelper).getColorId());
            eventList = dbHelper.filter(filterLabelId, dbHelper);
            topList = dbHelper.filterTop(filterLabelId, dbHelper);
            for(Event event : topList) {
                event.setStatus(Event.STATUS_NONE);
            }
            if(topList.size() > 0) {
                topList.get(0).setStatus(Event.STATUS_TOP);
            }
        }
        Collections.sort(eventList);
        EventUtil.markEventList(eventList);

        topCount = topList.size();
        topList.addAll(eventList);
        totalList = topList;
        Log.d("EventFragment", "top list contains " + topList.size() + " items");
        Log.d("EventFragment", "event list contains " + eventList.size() + " items");

        Log.d("EventFragment", "event view == null?" + (eventView == null));
        if(eventView != null) {
            if (totalList.size() == 0) {
                Log.d("EventFragment", "totalList size  = 0");
                listView.setVisibility(View.GONE);
                blankLinearLayout.setVisibility(View.VISIBLE);
                Log.d("EventFragment", "listView visibility == gone?" + (listView.getVisibility() == View.GONE));
            } else {
                Log.d("EventFragment", "totalList size : " + totalList.size());
                blankLinearLayout.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                Log.d("EventFragment", "listView visibility == gone?" + (listView.getVisibility() == View.GONE));
            }
        }

        Log.d("EventFragment", "totalList contains " + totalList.size() + " items");
        Log.d("initList", "initList end");
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }


    public class EventAdapter extends BaseAdapter {


        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            if(position >= topCount) {
                return VIEWTYPE_EVENT;
            } else {
                return VIEWTYPE_TOP;
            }
        }


        @Override
        public int getCount() {
            return totalList.size();
        }

        @Override
        public Object getItem(int position) {
            return totalList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Event event = totalList.get(position);
            View view;
            final ViewHolder viewHolder;

            if(convertView == null) {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.event_item, null);
                viewHolder = new ViewHolder();
                viewHolder.statusTextView = (TextView) view.findViewById(R.id.status_textview);
                viewHolder.eventTextView = (TextView) view.findViewById(R.id.event_textview);
                viewHolder.noteTextView = (TextView) view.findViewById(R.id.note_textview);
                viewHolder.timeTextView = (TextView) view.findViewById(R.id.item_time_textview);
                viewHolder.labelNameTextView = (TextView) view.findViewById(R.id.item_label_name_textview);
                viewHolder.labelColorTextView = (TextView) view.findViewById(R.id.item_label_color_textview);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();//retrieve the ViewHolder Object
            }

            viewHolder.statusTextView.setTextColor(color);
            switch (event.getStatus()) {
                case Event.STATUS_NONE:
                    viewHolder.statusTextView.setVisibility(View.GONE);
                    break;

                case Event.STATUS_UNDONE:
                    viewHolder.statusTextView.setVisibility(View.VISIBLE);
                    viewHolder.statusTextView.setText(getString(R.string.undone));
                    break;

                case Event.STATUS_TODAY:
                    viewHolder.statusTextView.setVisibility(View.VISIBLE);
                    viewHolder.statusTextView.setText(getString(R.string.today));
                    break;

                case Event.STATUS_TOMORROW:
                    viewHolder.statusTextView.setVisibility(View.VISIBLE);
                    viewHolder.statusTextView.setText(getString(R.string.tomorrow));
                    break;

                case Event.STATUS_THIS_WEEK:
                    viewHolder.statusTextView.setVisibility(View.VISIBLE);
                    viewHolder.statusTextView.setText(getString(R.string.this_week));
                    break;

                case Event.STATUS_AFTER:
                    viewHolder.statusTextView.setVisibility(View.VISIBLE);
                    viewHolder.statusTextView.setText(getString(R.string.after));
                    break;

                case Event.STATUS_TOP:
                    viewHolder.statusTextView.setVisibility(View.VISIBLE);
                    viewHolder.statusTextView.setText(getString(R.string.top));
                    break;

                default:
                    Log.e("ToDo", "event's statusCode out of range!");
                    break;
            }

            viewHolder.eventTextView.setText(event.getEvent());

            if(event.getNote().equals("")){
                viewHolder.noteTextView.setVisibility(View.GONE);
            } else {
                viewHolder.noteTextView.setVisibility(View.VISIBLE);
            }
            viewHolder.noteTextView.setText(event.getNote());
            viewHolder.noteTextView.setOnClickListener(new OnClickListener() {

                boolean isOpenFlag = false;

                @Override
                public void onClick(View v) {
                    if(isOpenFlag){
                        isOpenFlag = !isOpenFlag;
                        viewHolder.noteTextView.setEllipsize(TextUtils.TruncateAt.END);
                        viewHolder.noteTextView.setMaxLines(2);
                    } else {
                        isOpenFlag = !isOpenFlag;
                        viewHolder.noteTextView.setEllipsize(null);
                        viewHolder.noteTextView.setMaxLines(100);
                    }
                }
            });

            viewHolder.timeTextView.setText(EventUtil.toTimeString(event, getActivity()));

            if(event.getLabelId() != 0) {
                viewHolder.labelNameTextView.setVisibility(View.VISIBLE);
                viewHolder.labelColorTextView.setVisibility(View.VISIBLE);
                Label label = dbHelper.getLabel(event.getLabelId(), dbHelper);
                viewHolder.labelNameTextView.setText(label.getName());
                ColorStateList csl = (ColorStateList) getActivity().getResources().getColorStateList(label.getColorId());
                viewHolder.labelColorTextView.setTextColor(csl);
            } else {
                viewHolder.labelNameTextView.setVisibility(View.INVISIBLE);
                viewHolder.labelColorTextView.setVisibility(View.INVISIBLE);
            }

            return view;
        }

        class ViewHolder {
            TextView statusTextView;

            TextView eventTextView;

            TextView noteTextView;

            TextView timeTextView;

            TextView labelNameTextView;

            TextView labelColorTextView;
        }
    }

}
