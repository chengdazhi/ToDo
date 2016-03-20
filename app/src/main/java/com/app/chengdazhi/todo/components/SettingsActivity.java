package com.app.chengdazhi.todo.components;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.app.chengdazhi.todo.R;
import com.app.chengdazhi.todo.tools.SettingsUtil;

import java.io.IOException;


public class SettingsActivity extends ActionBarActivity {
    private ActionBar actionBar;

    private Vibrator vibrator;//for vibration

    private MediaPlayer mediaPlayer;// for play sound

    private ImageView showNotificationImageView;

    private ImageView soundImageView;

    private ImageView vibrateImageView;

    private ImageView breathLightImageView;

    private TextView showNotificationTitleTextView;

//    private TextView showNotificationDetailTextView;

    private TextView soundTextView;

    private TextView vibrateTextView;

    private TextView breathLightTextView;

    private Switch showNotificationSwitch;

    private Switch soundSwitch;

    private Switch vibrateSwitch;

    private Switch breathLightSwitch;

    private Switch instantAddSwitch;

    private boolean showNotificationBoolean;

    private boolean soundBoolean;

    private boolean vibrateBoolean;

    private boolean breathLightBoolean;

    private boolean instantAddBoolean;

    public static void startSettingsActivity(Context context){
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initViews();
    }

    private void initViews() {
        showNotificationImageView = (ImageView) findViewById(R.id.show_notification_imageview);
        soundImageView = (ImageView) findViewById(R.id.sound_imageview);
        vibrateImageView = (ImageView) findViewById(R.id.vibrate_imageview);
        breathLightImageView = (ImageView) findViewById(R.id.breath_light_imageview);
        showNotificationTitleTextView = (TextView) findViewById(R.id.show_notification_title_textview);
//        showNotificationDetailTextView = (TextView) findViewById(R.id.show_notification_detail_textview);
        soundTextView = (TextView) findViewById(R.id.sound_textview);
        vibrateTextView = (TextView) findViewById(R.id.vibrate_textview);
        breathLightTextView = (TextView) findViewById(R.id.breath_light_textview);
        showNotificationSwitch = (Switch) findViewById(R.id.show_notification_switch);
        soundSwitch = (Switch) findViewById(R.id.sound_switch);
        vibrateSwitch = (Switch) findViewById(R.id.vibration_switch);
        breathLightSwitch = (Switch) findViewById(R.id.breath_light_switch);
        instantAddSwitch = (Switch) findViewById(R.id.instant_add_switch);

        showNotificationBoolean = SettingsUtil.getNotification(this);
        soundBoolean = SettingsUtil.getSound(this);
        vibrateBoolean = SettingsUtil.getVibrate(this);
        breathLightBoolean = SettingsUtil.getBreathLight(this);
        instantAddBoolean = SettingsUtil.getInstantAdd(this);

        showNotificationSwitch.setChecked(showNotificationBoolean);
        if(showNotificationBoolean){
            soundSwitch.setChecked(soundBoolean);
            vibrateSwitch.setChecked(vibrateBoolean);
            breathLightSwitch.setChecked(breathLightBoolean);
        } else {
            switchOffNotificationAttributes();
        }
        instantAddSwitch.setChecked(instantAddBoolean);

        showNotificationSwitch.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        soundSwitch.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        vibrateSwitch.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        breathLightSwitch.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        instantAddSwitch.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
    }

    public void switchOnNotificationAttributes(){
        soundSwitch.setChecked(soundBoolean);
        soundSwitch.setClickable(true);
        soundImageView.setImageResource(R.drawable.volume);
        soundTextView.setTextColor(getResources().getColor(R.color.font_black));

        vibrateSwitch.setChecked(vibrateBoolean);
        vibrateSwitch.setClickable(true);
        vibrateImageView.setImageResource(R.drawable.vibrate);
        vibrateTextView.setTextColor(getResources().getColor(R.color.font_black));

        breathLightSwitch.setChecked(breathLightBoolean);
        breathLightSwitch.setClickable(true);
        breathLightImageView.setImageResource(R.drawable.ic_action_brightness_high);
        breathLightTextView.setTextColor(getResources().getColor(R.color.font_black));
    }

    public void switchOffNotificationAttributes(){
        soundSwitch.setChecked(false);
        soundSwitch.setClickable(false);
        soundImageView.setImageResource(R.drawable.volume_dark);
        soundTextView.setTextColor(getResources().getColor(R.color.grey));

        vibrateSwitch.setChecked(false);
        vibrateSwitch.setClickable(false);
        vibrateImageView.setImageResource(R.drawable.vibrate_dark);
        vibrateTextView.setTextColor(getResources().getColor(R.color.grey));

        breathLightSwitch.setChecked(false);
        breathLightSwitch.setClickable(false);
        breathLightImageView.setImageResource(R.drawable.brightness_dark);
        breathLightTextView.setTextColor(getResources().getColor(R.color.grey));
    }

    class MyOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            switch (buttonView.getId()){

                case R.id.show_notification_switch:
                    showNotificationBoolean = isChecked;
                    SettingsUtil.setNotification(SettingsActivity.this, isChecked);
                    if(isChecked){
                        switchOnNotificationAttributes();
                    } else {
                        switchOffNotificationAttributes();
                    }
                    break;

                case R.id.sound_switch:
                    soundBoolean = isChecked;
                    SettingsUtil.setSound(SettingsActivity.this, isChecked);

                    //add code to play sound
                    mediaPlayer = MediaPlayer.create(SettingsActivity.this,
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    mediaPlayer.setLooping(false);
                    if(isChecked) {
                        try {
                            mediaPlayer.prepare();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                    }

                    break;

                case R.id.vibration_switch:
                    vibrateBoolean = isChecked;
                    SettingsUtil.setVibrate(SettingsActivity.this, isChecked);

                    //add code to vibrate
                    if(isChecked) {
                        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        long[] pattern = {0, 500, 500, 500};
                        vibrator.vibrate(pattern, -1);
                    }

                    break;

                case R.id.breath_light_switch:
                    breathLightBoolean = isChecked;
                    SettingsUtil.setBreathLight(SettingsActivity.this, isChecked);
                    break;

                case R.id.instant_add_switch:
                    instantAddBoolean = isChecked;
                    SettingsUtil.setInstantAdd(SettingsActivity.this, isChecked);

                    //dismiss or activate the notification



                    break;

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
