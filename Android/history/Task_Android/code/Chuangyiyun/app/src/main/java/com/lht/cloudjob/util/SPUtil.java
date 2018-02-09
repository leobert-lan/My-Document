package com.lht.cloudjob.util;

import android.content.SharedPreferences;

public class SPUtil {

    public static void modifyBoolean(SharedPreferences sp, String key, boolean value) {
        sp.edit().putBoolean(key, value).commit();
    }

    public static void modifyString(SharedPreferences sp, String key, String value) {
        sp.edit().putString(key, value).commit();
    }

    public static void modifyInt(SharedPreferences sp, String key, int value) {
        sp.edit().putInt(key, value).commit();
    }
}
