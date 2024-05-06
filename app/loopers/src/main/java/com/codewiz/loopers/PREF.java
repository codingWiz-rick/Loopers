package com.codewiz.loopers;

import android.content.Context;
import android.content.SharedPreferences;

public class PREF {
    public static PREF pref;
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor edit;

    public PREF(Context context) {
        preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        edit = preferences.edit();
    }

    public static PREF getInstance(Context context) {
        if (pref == null) {
            pref = new PREF(context);
        }
        return pref;
    }

    public static PREF getPref() {
        return pref;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public SharedPreferences.Editor getEdit() {
        return edit;
    }

    public void setTime(String looper, int timeInMils) {
        getEdit().putInt(looper, timeInMils).commit();
    }

    public int getTime(String looper) {
        return getPref().preferences.getInt(looper, 1000);
    }
}
