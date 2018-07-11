package com.imagepicker.xp.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by dell on 2017/12/23.
 */

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
