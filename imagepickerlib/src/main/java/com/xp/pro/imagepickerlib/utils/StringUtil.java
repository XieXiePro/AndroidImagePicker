
package com.xp.pro.imagepickerlib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static String getUtf8String(String str) {
        try {
            return new String(str.getBytes(), "gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 不为null，也不是全是空白字符
     */
    public static boolean isNotNullAndNotEmpty(String string) {
        return string != null && string.trim().length() > 0;
    }


    /**
     * null或空字符串判断
     *
     * @param string
     * @return hechuan / 2015年7月27日 下午11:25:05
     */
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.trim().length() == 0;
    }

    /**
     * 生成MD5
     */
    public static String getMD5(String source) {
        return getMD5(source.getBytes());
    }

    /**
     * 生成MD5
     */
    public static String getMD5(byte[] source) {

        String s = null;

        // 用来将字节转换成 16 进制表示的字符
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {

            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            md.update(source);

			/*
             *  MD5 的计算结果是一个 128 位的长整数，用字节表示就是 16 个字节，
			 *  每个字节用 16 进制表示的话，使用两个字符，所以表示成 16 进制需要 32 个字符。
			 *  不采用String.format进行格式化，String.format比较慢，慢差不多50倍。
			 */
            byte tmp[] = md.digest();

            char str[] = new char[16 * 2];

            int k = 0;
            for (int i = 0; i < 16; i++) {
                // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 日期轉換為字符串
     *
     * @param dt
     * @return hechuan / 2015年7月27日 下午11:25:35
     */
    @SuppressLint("SimpleDateFormat")
    public static String DateToString(final Date dt) {
        String str = "";
        if (dt != null) {
            str = new SimpleDateFormat("yyyy-MM").format(dt);
        }
        return str;
    }

    /**
     * 字符串轉為日期
     *
     * @param str
     * @return hechuan / 2013-11-4 上午9:22:19
     */
    @SuppressLint("SimpleDateFormat")
    public static Date StringToDate(final String str) {
        if (null == str || str.equals("")) {
            return null;
        }
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
        Date date1 = null;
        try {
            date1 = df.parse(str);
        } catch (final Exception Ex) {
            return null;
        }
        return new Date(date1.getTime());
    }

    /**
     * 只含有汉字、数字、字母、下划线不能以下划线开头和结尾
     *
     * @param str
     * @return hechuan / 2013-12-23
     */
    public static boolean StringFilter(String str) {
        String regEx = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]+$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 过滤特殊字符
     *
     * @param str
     * @return hechuan/ 2013-12-23
     */
    public static String StringReplace(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").replaceAll("[\\[\\]]", "").trim();
    }

    public static int StringToInt(String src, int defaultValue) {
        int result = defaultValue;
        try {
            result = Integer.parseInt(src);
        } catch (Exception ex) {
            //catch exception
        }

        return result;
    }

    public static boolean StringToBoolean(String src, boolean defaultValue) {
        boolean result = defaultValue;
        try {
            result = Boolean.parseBoolean(src);
        } catch (Exception ex) {

        }
        return result;
    }

    // 电话号码正则表达式
    public static final String PHONEURL_STRING = "((?<=\\D|^)((\\d{7,13})|((\\d{3,4}-)?\\d{7,8}))(?=\\D|$))";

    public static final Pattern patternPhoneNumber = Pattern.compile(PHONEURL_STRING, Pattern.CASE_INSENSITIVE);

    public static boolean matchPhoneNumber(String input) {
        final Matcher matcher = patternPhoneNumber.matcher(input);
        return matcher.matches();
    }

    //public static final String regUrl = "((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";// "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?";

    public static final String regUrl = "^(http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$";
    public static final Pattern patternUrl = Pattern.compile(regUrl, Pattern.CASE_INSENSITIVE);

    public static boolean isUrl(final String url) {
//        final Matcher matcher = patternUrl.matcher(url);
//        return matcher.matches();

        if( url.startsWith("http://") || url.startsWith("https://") || url.startsWith("ftp://")){
            return true;
        }
        return false;
    }

    public static SpannableStringBuilder highlight(String text, String target, int color) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        if (!TextUtils.isEmpty(target)) {
//            if(target.length() == 1) {
                CharacterStyle span = null;
                Pattern p = Pattern.compile(target);
                Matcher m = p.matcher(text);
                while (m.find()) {
                    span = new ForegroundColorSpan(color);// 需要重复！
                    spannable.setSpan(span, m.start(), m.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
//            } else {
//                fo
//            }
        }
        return spannable;
    }

    /**
     * 拿到Mainfest中写的常量
     * @param name
     * @return
     */
    public static String getMetaData(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(name);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
