package com.xp.pro.imagepickerlib.app;

import android.app.Application;
import android.content.Context;

public class PickerApplication extends Application {

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

    }

    public static Context getAppContext() {

        return mContext;
    }
}
