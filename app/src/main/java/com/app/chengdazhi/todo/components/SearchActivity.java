package com.app.chengdazhi.todo.components;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.chengdazhi.todo.models.Event;
import com.app.chengdazhi.todo.models.Label;
import com.app.chengdazhi.todo.R;
import com.app.chengdazhi.todo.tools.EventUtil;
import com.app.chengdazhi.todo.tools.MyDatabaseHelper;

import java.util.List;


public class SearchActivity extends ActionBarActivity {

    private ImageView backImageView;

    private ImageView searchImageView;

    private EditText searchEdittext;

    private ListView listView;

    private List<Event> searchEventList;

    private SearchAdapter adapter;

    private String filter = "&qpwoei%^&";

    private MyDatabaseHelper dbHelper;

    public static void startSearchActivity(Context context){
        Intent intent = new Intent();
        intent.setClass(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        backImageView = (ImageView) this.findViewById(R.id.search_back_imageview);
        searchImageView = (ImageView) this.findViewById(R.id.search_action_imageview);
        searchEdittext = (EditText) this.findViewById(R.id.search_edittext);
        listView = (ListView) this.findViewById(R.id.search_listview);

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter = searchEdittext.getText().toString().trim();
                initSearchEventList();
                adapter.notifyDataSetChanged();
            }
        });

        searchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter = s.toString();
                initSearchEventList();
                if (filter.equals("")){
                    searchEventList.clear();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dbHelper = new MyDatabaseHelper(this, "ToDo.db", null, 2);
        initSearchEventList();
        adapter = new SearchAdapter();
        listView.setAdapter(adapter);
    }

    private void initSearchEventList() {
        searchEventList = dbHelper.query(filter, dbHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class SearchAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return searchEventList.size();
        }

        @Override
        public Object getItem(int position) {
            return searchEventList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Event event = searchEventList.get(position);
            View view;
            final ViewHolder viewHolder;

            if(convertView == null) {
                view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.event_item, null);
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
            if(event.getIsDone()){
                viewHolder.eventTextView.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG); //中划线
            }

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

            viewHolder.timeTextView.setText(EventUtil.toTimeString(event, SearchActivity.this));

            if(event.getLabelId() != 0) {
                viewHolder.labelNameTextView.setVisibility(View.VISIBLE);
                viewHolder.labelColorTextView.setVisibility(View.VISIBLE);
                Label label = dbHelper.getLabel(event.getLabelId(), dbHelper);
                viewHolder.labelNameTextView.setText(label.getName());
                ColorStateList csl = (ColorStateList) getResources().getColorStateList(label.getColorId());
                viewHolder.labelColorTextView.setTextColor(csl);
            } else {
                viewHolder.labelNameTextView.setVisibility(View.VISIBLE);
                viewHolder.labelColorTextView.setVisibility(View.INVISIBLE);
            }

            return view;
        }

        class ViewHolder{
            TextView statusTextView;

            TextView eventTextView;

            TextView noteTextView;

            TextView timeTextView;

            TextView labelNameTextView;

            TextView labelColorTextView;
        }
    }
}
