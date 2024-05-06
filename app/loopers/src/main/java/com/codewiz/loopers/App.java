package com.codewiz.loopers;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PREF.getInstance(this);
    }
}
