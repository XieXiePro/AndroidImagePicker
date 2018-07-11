package com.imagepicker.xp.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ThreadUtils {

    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    private static Handler sMainHandler = new Handler(Looper.getMainLooper());

    /**
     * 非UI线程执行
     * @param runnable
     */
    public static void runOnNonUIthread(Runnable runnable) {
        threadPool.submit(runnable);
    }

    /**
     * UI线程执行
     * @param runnable
     */
    public static void runOnUiThread(Runnable runnable) {
        sMainHandler.post(runnable);
    }


    public static void postDelayedOnUiThread(Runnable runnable, long delayMillis) {
        sMainHandler.postDelayed(runnable, delayMillis);
    }

    public static boolean isMainThread(){
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 获取当前线程名称
     * @return
     */
    public static String getCurrentThreadName() {
        String result = "UNKNOWN";
        Looper looper = Looper.myLooper();
        if (looper != null) {
            result = looper.getThread().getName();
        }
        return result;
    }

    public static final Handler getMainHander() {
        return sMainHandler;
    }
}
