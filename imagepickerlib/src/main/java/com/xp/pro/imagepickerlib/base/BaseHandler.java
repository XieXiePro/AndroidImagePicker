package com.xp.pro.imagepickerlib.base;

import android.os.Handler;
import android.os.Looper;


public class BaseHandler extends Handler {

//    private static final String TAG = "BaseHandler";
    private Callback mCallbackEx;

    public BaseHandler() {
        super();
    }

    public BaseHandler(Looper looper) {
        super(looper);
    }

    public BaseHandler(Looper looper, Callback callback) {
        super(looper, callback);
        mCallbackEx = callback;
    }

    public Callback getCallbackEx() {
        return mCallbackEx;
    }

}
