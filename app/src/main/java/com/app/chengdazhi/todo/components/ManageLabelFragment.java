package com.app.chengdazhi.todo.components;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.chengdazhi.todo.models.Event;
import com.app.chengdazhi.todo.models.Label;
import com.app.chengdazhi.todo.R;
import com.app.chengdazhi.todo.tools.MyDatabaseHelper;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManageLabelFragment extends Fragment {

    public ListView listView;

    public List<Label> labelList;

    public LabelManageAdapter adapter;

    public MyDatabaseHelper dbHelper;

    public TextView greenTextView;

    public TextView yellowTextView;

    public TextView blueTextView;

    public TextView redTextView;

    public TextView orangeTextView;

    public TextView brownTextView;

    public TextView purpleTextView;

    public TextView pinkTextView;

    public EditText labelNameEditText;

    public TextView cancelButtonTextView;

    public TextView deleteButtonTextView;

    public TextView okButtonTextView;

    public int labelColorState = 1;

    public static final int LABEL_GREEN = 1;

    public static final int LABEL_YELLOW = 2;

    public static final int LABEL_BLUE = 3;

    public static final int LABEL_RED = 4;

    public static final int LABEL_ORANGE = 5;

    public static final int LABEL_BROWN = 6;

    public static final int LABEL_PURPLE = 7;

    public static final int LABEL_PINK = 8;

    public boolean isNewLabel = true;

    public ManageLabelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_label, null);
        dbHelper = new MyDatabaseHelper(getActivity(), "ToDo.db", null, 2);
        initLabelList();
        listView = (ListView) view.findViewById(R.id.manage_fragment_listview);
        adapter = new LabelManageAdapter();
        listView.setAdapter(adapter);

        return view;
    }

    private void initLabelList() {
        labelList = dbHelper.readFromLabel(dbHelper);
        for(Label label : labelList){
            Log.v("ToDo", "label:" + label.getName());
        }
    }

    class LabelManageAdapter extends BaseAdapter {

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            if(position < labelList.size()){
                view = LayoutInflater.from(getActivity()).inflate(R.layout.label_manage_listview_item, null);
                final Label label = labelList.get(position);
                TextView labelNameTextView = (TextView) view.findViewById(R.id.manage_label_name_textview);
                labelNameTextView.setText(label.getName());
                TextView labelColorTextView = (TextView) view.findViewById(R.id.manage_label_color_textview);
                labelColorTextView.setTextColor(getActivity().getResources().getColor(label.getColorId()));
                ImageView labelButtonImageView = (ImageView) view.findViewById(R.id.manage_button);
                labelButtonImageView.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        startDialog(label);
                    }
                });
            } else {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.label_manage_listview_item, null);
                view.findViewById(R.id.manage_label_color_textview).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.manage_label_color_textview).setVisibility(View.INVISIBLE);
                ImageView buttonImage = (ImageView) view.findViewById(R.id.manage_button);
                buttonImage.setImageResource(R.drawable.ic_action_new_dark);
                buttonImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startDialog(null);
                    }
                });
            }
            return view;
        }

        public void startDialog(final Label label){
            isNewLabel = true;
            if(label != null){
                isNewLabel = false;
            }
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.label_edit, null);
            greenTextView = (TextView) view.findViewById(R.id.label_green);
            yellowTextView = (TextView) view.findViewById(R.id.label_yellow);
            blueTextView = (TextView) view.findViewById(R.id.label_blue);
            redTextView = (TextView) view.findViewById(R.id.label_red);
            orangeTextView = (TextView) view.findViewById(R.id.label_orange);
            brownTextView = (TextView) view.findViewById(R.id.label_brown);
            purpleTextView = (TextView) view.findViewById(R.id.label_purple);
            pinkTextView = (TextView) view.findViewById(R.id.label_pink);
            labelNameEditText = (EditText) view.findViewById(R.id.label_name_edittext);
            labelColorState = LABEL_GREEN;
            cancelButtonTextView = (TextView) view.findViewById(R.id.label_edit_cancel);
            deleteButtonTextView = (TextView) view.findViewById(R.id.label_edit_delete);
            okButtonTextView = (TextView) view.findViewById(R.id.label_edit_ok);

            greenTextView.setOnClickListener(new MyViewOnClickListener());
            yellowTextView.setOnClickListener(new MyViewOnClickListener());
            blueTextView.setOnClickListener(new MyViewOnClickListener());
            redTextView.setOnClickListener(new MyViewOnClickListener());
            orangeTextView.setOnClickListener(new MyViewOnClickListener());
            brownTextView.setOnClickListener(new MyViewOnClickListener());
            purpleTextView.setOnClickListener(new MyViewOnClickListener());
            pinkTextView.setOnClickListener(new MyViewOnClickListener());

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            if(label != null){
                labelNameEditText.setText(label.getName());
                deleteButtonTextView.setVisibility(View.VISIBLE);
                unselectCurrentColor();
                switch (label.getColorId()){
                    case R.color.light_green:
                        greenTextView.setBackgroundColor(getResources().getColor(R.color.grey));
                        break;

                    case R.color.light_yellow:
                        yellowTextView.setBackgroundColor(getResources().getColor(R.color.grey));
                        labelColorState = LABEL_YELLOW;
                        break;

                    case R.color.blue:
                        blueTextView.setBackgroundColor(getResources().getColor(R.color.grey));
                        labelColorState = LABEL_BLUE;
                        break;

                    case R.color.light_red:
                        redTextView.setBackgroundColor(getResources().getColor(R.color.grey));
                        labelColorState = LABEL_RED;
                        break;

                    case R.color.orange:
                        orangeTextView.setBackgroundColor(getResources().getColor(R.color.grey));
                        labelColorState = LABEL_ORANGE;
                        break;

                    case R.color.brown:
                        brownTextView.setBackgroundColor(getResources().getColor(R.color.grey));
                        labelColorState = LABEL_BROWN;
                        break;

                    case R.color.dark_purple:
                        purpleTextView.setBackgroundColor(getResources().getColor(R.color.grey));
                        labelColorState = LABEL_PURPLE;
                        break;

                    case R.color.pink:
                        pinkTextView.setBackgroundColor(getResources().getColor(R.color.grey));
                        labelColorState = LABEL_PINK;
                        break;

                }

            }

            final Dialog dialog = builder.create();

            cancelButtonTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            deleteButtonTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Event> labelList = dbHelper.filter(label.getLabelId(), dbHelper);
                    for(Event event : labelList){
                        event.setLabelId(0);
                        dbHelper.updateEvent(event, dbHelper);
                    }
                    dbHelper.deleteLabel(label.getLabelId(), dbHelper);
                    initLabelList();
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                    ((MainActivity) getActivity()).reInit();
                }
            });

            okButtonTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String labelName = labelNameEditText.getText().toString().trim();
                    if(labelName.equals(""))
                        labelName = getString(R.string.untitled_label_name);
                    if (isNewLabel) {
                        Label newLabel = new Label(getColorIdFromState(), labelName);
                        dbHelper.addLabel(newLabel, dbHelper);
                    } else {
                        Label newLabel = new Label(getColorIdFromState(), labelName, label.getLabelId());
                        dbHelper.updateLabel(newLabel, dbHelper);
                    }
                    initLabelList();
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                    ((MainActivity) getActivity()).reInit();
                }
            });

            dialog.show();//此处show()方法必须在getWindow()前调用
            dialog.getWindow().setContentView(view);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        public int getColorIdFromState(){
            switch (labelColorState){
                case LABEL_GREEN:
                    return R.color.light_green;

                case LABEL_YELLOW:
                    return R.color.light_yellow;

                case LABEL_BLUE:
                    return R.color.blue;

                case LABEL_RED:
                    return R.color.light_red;

                case LABEL_ORANGE:
                    return R.color.orange;

                case LABEL_BROWN:
                    return R.color.brown;

                case LABEL_PURPLE:
                    return R.color.dark_purple;

                case LABEL_PINK:
                    return R.color.pink;

                default:
                    Log.e("ToDo", "unrecognized color state");
                    return 0;
            }
        }

        private void unselectCurrentColor() {
            switch (labelColorState){
                case LABEL_GREEN:
                    greenTextView.setBackgroundColor(getResources().getColor(R.color.no_choose_grey));
                    break;

                case LABEL_YELLOW:
                    yellowTextView.setBackgroundColor(getResources().getColor(R.color.no_choose_grey));
                    break;

                case LABEL_BLUE:
                    blueTextView.setBackgroundColor(getResources().getColor(R.color.no_choose_grey));
                    break;

                case LABEL_RED:
                    redTextView.setBackgroundColor(getResources().getColor(R.color.no_choose_grey));
                    break;

                case LABEL_ORANGE:
                    orangeTextView.setBackgroundColor(getResources().getColor(R.color.no_choose_grey));
                    break;

                case LABEL_BROWN:
                    brownTextView.setBackgroundColor(getResources().getColor(R.color.no_choose_grey));
                    break;

                case LABEL_PURPLE:
                    purpleTextView.setBackgroundColor(getResources().getColor(R.color.no_choose_grey));
                    break;

                case LABEL_PINK:
                    pinkTextView.setBackgroundColor(getResources().getColor(R.color.no_choose_grey));
                    break;

                default:
                    Log.e("ToDo", "unrecognized color state");
                    break;
            }
        }

        class MyViewOnClickListener implements View.OnClickListener{

            @Override
            public void onClick(View v) {
                unselectCurrentColor();
                v.setBackgroundColor(getResources().getColor(R.color.grey));
                switch (v.getId()){
                    case R.id.label_green:
                        labelColorState = LABEL_GREEN;
                        break;

                    case R.id.label_yellow:
                        labelColorState = LABEL_YELLOW;
                        break;

                    case R.id.label_blue:
                        labelColorState = LABEL_BLUE;
                        break;

                    case R.id.label_red:
                        labelColorState = LABEL_RED;
                        break;

                    case R.id.label_orange:
                        labelColorState = LABEL_ORANGE;
                        break;

                    case R.id.label_brown:
                        labelColorState = LABEL_BROWN;
                        break;

                    case R.id.label_purple:
                        labelColorState = LABEL_PURPLE;
                        break;

                    case R.id.label_pink:
                        labelColorState = LABEL_PINK;
                        break;


                }
            }
        }

        class ViewHolder {
            TextView labelColor;
            TextView labelName;
            ImageView labelButton;
        }

    }

}
