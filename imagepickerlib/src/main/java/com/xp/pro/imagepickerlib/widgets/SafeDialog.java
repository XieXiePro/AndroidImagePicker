package com.xp.pro.imagepickerlib.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * show()方法进行activity.isFinish判断，防止crash
 */
public class SafeDialog extends Dialog {

    private WeakReference<Context> mContext = null;

    /**
     * @param context
     */
    public SafeDialog(Context context) {
        super(context);
        mContext = new WeakReference<Context>(context);
    }

    /**
     * @param context
     * @param theme
     */
    public SafeDialog(Context context, int theme) {
        super(context, theme);
        mContext = new WeakReference<Context>(context);
    }

    /**
     * @param context
     * @param cancelable
     * @param cancelListener
     */
    protected SafeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = new WeakReference<Context>(context);
    }

    public boolean isActivityFinishing() {
        Context c = mContext.get();
        return c instanceof Activity && ((Activity) c).isFinishing();
    }

    @Override
    public void show() {
        if (isActivityFinishing()) {
            return;
        }
        super.show();
    }

    public Context getHoldActivity() {
        if (null != mContext && !isActivityFinishing()) {
            return mContext.get();
        }
        return null;
    }
}
