package com.xp.pro.imagepickerlib.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * 屏幕显示工具类
 */
public class DisplayUtil {

    DisplayUtil(Context context){
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            sScreenWidth = wm.getDefaultDisplay().getWidth();
            sScreenHeight = wm.getDefaultDisplay().getHeight();
        }catch (Exception e){
            Log.d("getDefaultDisplay", "static initializer: error");
        }
    }
    /**
     * Covert dp to px
     *
     * @param dp
     * @param context
     * @return pixel
     */
    public static float convertDpToPixel(float dp, Context context) {
        float px = dp * getDensity(context);
        return px;
    }

    /**
     * Covert px to dp
     *
     * @param px
     * @param context
     * @return dp
     */
    public static float convertPixelToDp(float px, Context context) {
        float dp = px / getDensity(context);
        return dp;
    }

    /**
     * Covert px to dp
     *
     * @param px
     * @return dp
     */
    public static float convertPixelToDp(float px) {
        return convertPixelToDp(px, null);
    }

    /**
     * 取得螢幕密度
     * 120dpi = 0.75
     * 160dpi = 1 (default)
     * 240dpi = 1.5
     *
     * @param
     * @return
     */
    public static float getDensity(Context context) {
        float density;
        try {
            density = context.getResources().getDisplayMetrics().density;
        } catch (Exception e) {
            density = 1;
        }
        return density;
    }

    private static int sScreenWidth = 720;// 屏幕宽px
    private static int sScreenHeight = 1280;// 屏幕高px

    public static int getScreenWidth() {
        return sScreenWidth;
    }

    public static int getScreenHeight() {
        return sScreenHeight;
    }

    /**
     * dip 2 px
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(float dipValue,Context context) {
        // return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.getResources().getDisplayMetrics())+0.5f);
        return (int) (dipValue * getDensity(context) + 0.5f);
    }

    /**
     * px 2 dip
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(float pxValue,Context context) {
        return (int) (pxValue / getDensity(context) + 0.5f);
    }

    public static float getHeightScale(int srcHeight) {
        float scaleYValue = sScreenHeight / 1136f;
        return scaleYValue * srcHeight;
    }

    public static float getWidthScale(int srcWidth) {
        float scaleXValue = sScreenWidth / 640f;
        return scaleXValue * srcWidth;
    }

    /**
     * 在activity中获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return statusHeight;
    }
}
