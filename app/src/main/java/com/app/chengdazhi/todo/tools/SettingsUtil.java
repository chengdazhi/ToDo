package com.app.chengdazhi.todo.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by chengdazhi on 11/20/15.
 *
 * A static class to save the preferences
 */
public class SettingsUtil {

    private static final String preferencesName = "settings";

    private static final String notificationName = "notification";

    private static final String soundName = "sound";

    private static final String vibrateName = "vibrate";

    private static final String breathLightName = "breathLight";

    private static final String instantAddName = "instantAdd";

    public static void setNotification(Context context, boolean notification){
        SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(notificationName, notification);
        editor.commit();
    }

    public static void setSound(Context context, boolean sound){
        SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(soundName, sound);
        editor.commit();
    }

    public static void setVibrate(Context context, boolean vibrate){
        SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(vibrateName, vibrate);
        editor.commit();
    }

    public static void setBreathLight(Context context, boolean breathLight){
        SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(breathLightName, breathLight);
        editor.commit();
    }

    public static void setInstantAdd(Context context, boolean instantAdd){
        SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(instantAddName, instantAdd);
        editor.commit();
    }

    public static boolean getNotification(Context context){
        SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_APPEND);
        return preferences.getBoolean(notificationName, true);
    }

    public static boolean getSound(Context context){
        SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_APPEND);
        return preferences.getBoolean(soundName, false);
    }

    public static boolean getVibrate(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_APPEND);
        return preferences.getBoolean(vibrateName, true);
    }

    public static boolean getBreathLight(Context context){
        SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_APPEND);
        return preferences.getBoolean(breathLightName, true);
    }

    public static boolean getInstantAdd(Context context){
        SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_APPEND);
        return preferences.getBoolean(instantAddName, true);
    }
}
