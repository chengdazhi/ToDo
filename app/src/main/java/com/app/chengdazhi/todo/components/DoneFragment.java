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

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 *
 * This fragment shows the events that are already done.
 */
public class DoneFragment extends Fragment {
    private SwipeMenuListView listView;

    private DoneAdapter adapter;

    private MyDatabaseHelper dbHelper;

    private List<Event> doneList;

    private TextView blankTitleTextview;

    private TextView blankAddText;

    private TextView blankAddPicture;

    private LinearLayout blankLinearLayout;

    private View view;

    public DoneFragment() {
        // Required empty public constructor
    }

    //refresh the content every time the fragment's view is brought to the front.
    @Override
    public void onResume() {
        super.onResume();
        initDoneList();
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dbHelper = new MyDatabaseHelper(getActivity(), "ToDo.db", null, 2);

        initDoneList();

        //view contains two parts:
        //  1. the listview to display all the events.
        //  2. a linearLayout to be shown when there is no data.
        view = inflater.inflate(R.layout.fragment_done, null);

        blankTitleTextview = (TextView) view.findViewById(R.id.blank_title);
        blankAddText = (TextView) view.findViewById(R.id.blank_add_text);
        blankAddPicture = (TextView) view.findViewById(R.id.blank_add_picture);
        blankLinearLayout = (LinearLayout) view.findViewById(R.id.blank_linearlayout_done);

        blankTitleTextview.setText(getString(R.string.done_blank_title));
        blankAddText.setText(getString(R.string.done_blank_text));
        blankAddPicture.setTextColor(getResources().getColor(R.color.light_green));

        //opens the EventActivity when clicked.
        blankAddText.setOnClickListener(new BlankOnClickListener());
        blankAddPicture.setOnClickListener(new BlankOnClickListener());

        listView = (SwipeMenuListView) view.findViewById(R.id.listView_done);
        adapter = new DoneAdapter();
        listView.setAdapter(adapter);

        //creator manages the content of the swipe drawer
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                //Item to push the event which is already done back to ongoing list.
                SwipeMenuItem undoItem = new SwipeMenuItem(getActivity());
                undoItem.setBackground(new ColorDrawable(Color.rgb(0x4C, 0xAF, 0x50)));
                undoItem.setWidth(dp2px(90));
                undoItem.setIcon(R.drawable.ic_action_make_available_offline);
                menu.addMenuItem(undoItem);

                //Item to delete the event.
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0x38, 0x8E, 0x3C)));
                deleteItem.setWidth(dp2px(90));
                deleteItem.setIcon(R.drawable.ic_action_discard);
                menu.addMenuItem(deleteItem);
            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventActivity.startEventActivity(doneList.get(position), getActivity(), EventActivity.MODE_DONE);
            }
        });

        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // undo
                        dbHelper.doneToEvent(doneList.get(position).getEventId(), dbHelper);
                        initDoneList();
                        adapter.notifyDataSetChanged();
                        ((MainActivity) getActivity()).reInit();
                        break;
                    case 1:
                        // delete
                        dbHelper.deleteFromDone(doneList.get(position).getEventId(), dbHelper);
                        initDoneList();
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

        //check whether there is data
        if(doneList.size() == 0){
            listView.setVisibility(View.GONE);
            blankLinearLayout.setVisibility(View.VISIBLE);
        } else {
            blankLinearLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public class BlankOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            EventActivity.startEventActivity(null, getActivity(), EventActivity.MODE_NEW);
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void initDoneList() {
        doneList = dbHelper.readFromDone(dbHelper);

        if(view != null){
            if(doneList.size() == 0){
                listView.setVisibility(View.GONE);
                blankLinearLayout.setVisibility(View.VISIBLE);
            } else {
                blankLinearLayout.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        }
    }

    class DoneAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return doneList.size();
        }

        @Override
        public Object getItem(int position) {
            return doneList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Event event = doneList.get(position);
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

            viewHolder.eventTextView.setText(event.getEvent());

            if(event.getNote().equals("")){
                viewHolder.noteTextView.setVisibility(View.GONE);
            } else {
                viewHolder.noteTextView.setVisibility(View.VISIBLE);
            }
            viewHolder.noteTextView.setText(event.getNote());
            viewHolder.noteTextView.setOnClickListener(new View.OnClickListener() {

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
