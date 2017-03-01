package de.daschubbm.alkchievements.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import de.daschubbm.alkchievements.Application;
import de.daschubbm.alkchievements.R;

/**
 * Created by Maxl on 24.02.2017.
 */

public final class DataManager {
    public static final SharedPreferences defaultStorage;

    private DataManager() {}

    static {
        Context c = Application.getContext();

        defaultStorage = c.getSharedPreferences(c.getString(R.string.shared_preferences_name),
                Context.MODE_PRIVATE);
    }

    public static void writeLocal(Activity c, String key, String value) {
        SharedPreferences.Editor e = c.getPreferences(Context.MODE_PRIVATE).edit();
        e.putString(key, value);
        e.apply();
    }

    public static void writeLocal(Activity c, String key, int value) {
        SharedPreferences.Editor e = c.getPreferences(Context.MODE_PRIVATE).edit();
        e.putInt(key, value);
        e.apply();
    }

    public static void writeLocal(Activity c, String key, boolean value) {
        SharedPreferences.Editor e = c.getPreferences(Context.MODE_PRIVATE).edit();
        e.putBoolean(key, value);
        e.apply();
    }

    public static void writeLocal(Activity c, String key, long value) {
        SharedPreferences.Editor e = c.getPreferences(Context.MODE_PRIVATE).edit();
        e.putLong(key, value);
        e.apply();
    }

    public static void writeLocal(Activity c, String key, float value) {
        SharedPreferences.Editor e = c.getPreferences(Context.MODE_PRIVATE).edit();
        e.putFloat(key, value);
        e.apply();
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor e = defaultStorage.edit();
        e.putString(key, value);
        e.apply();
    }

    public static void write(String key, int value) {
        SharedPreferences.Editor e = defaultStorage.edit();
        e.putInt(key, value);
        e.apply();
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor e = defaultStorage.edit();
        e.putBoolean(key, value);
        e.apply();
    }

    public static void write(String key, long value) {
        SharedPreferences.Editor e = defaultStorage.edit();
        e.putLong(key, value);
        e.apply();
    }

    public static void write(String key, float value) {
        SharedPreferences.Editor e = defaultStorage.edit();
        e.putFloat(key, value);
        e.apply();
    }
}
