package com.lht.creationspace.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Description：SharedPreferences的管理类
 * 存放通用设置
 */
public class AppPreference {

    private SharedPreferences mSharedPreferences = null;
    private static Editor mEditor = null;
    private static AppPreference ourInstance;

    private AppPreference(Context context) {
        if (null == mSharedPreferences) {
            mSharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public static AppPreference getInstance(Application app) {
        if (ourInstance == null) {
            ourInstance = new AppPreference(app.getApplicationContext());
        }
        return ourInstance;
    }


    public void removeKey(String key) {
        mEditor = mSharedPreferences.edit();
        mEditor.remove(key);
        mEditor.commit();
    }

    public void removeAll() {
        mEditor = mSharedPreferences.edit();
        mEditor.clear();
        mEditor.commit();
    }

    public void commitString(String key, String value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public String getString(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    public void commitInt(String key, int value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public int getInt(String key, int failValue) {
        return mSharedPreferences.getInt(key, failValue);
    }

    public void commitLong(String key, long value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    public long getLong(String key, long failValue) {
        return mSharedPreferences.getLong(key, failValue);
    }

    public void commitBoolean(String key, boolean value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public Boolean getBoolean(String key, boolean failValue) {
        return mSharedPreferences.getBoolean(key, failValue);
    }
}
