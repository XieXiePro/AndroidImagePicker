package com.xp.pro.imagepickerlib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 图片处理工具类
 */
public class PhotoFileUtils {
    public static final String TAG = PhotoFileUtils.class.getSimpleName();

    /**
     * 报错bitmap
     *
     * @param context
     * @param uri     图片uri
     * @param picName 图片名
     * @return
     */
    public static String saveBitmap(Context context, Uri uri, String picName) {
        try {
            Bitmap bm = getThumbnail(context, uri);
            //给图片添加水印
            bm = setDateBitmap(bm);
            if (null != bm) {
                //首先在sdcard上创建根目录
                createSDDir("");
                File f = new File(PathConfig.getImagePath(), picName + ".jpg");
                f.deleteOnExit();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
                if (!bm.isRecycled()) {
                    bm.recycle();
                }
                return f.getAbsolutePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 通过uri来转成bitmap
     *
     * @param context
     * @param uri     Uri
     * @return
     * @throws IOException
     */
    public static Bitmap getThumbnail(Context context, Uri uri) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;
        double ratio = 1.0;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

    public static File createSDDir(String dirName) throws IOException {
        File dir = new File(PathConfig.getImagePath() + dirName);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            System.out.println("createSDDir:" + dir.getAbsolutePath());
            System.out.println("createSDDir:" + dir.mkdir());
        }
        return dir;
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(PathConfig.getImagePath() + fileName);
        file.isFile();
        return file.exists();
    }

    public static void delFile(String fileName) {
        File file = new File(PathConfig.getImagePath() + fileName);
        if (file.isFile()) {
            file.delete();
        }
        file.exists();
    }

    public static void deleteDir() {
        File dir = new File(PathConfig.getImagePath());
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteDir();
        }
        dir.delete();
    }

    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }


    /**
     * @return Bitmap 返回类型
     * 添加水印
     */
    public static Bitmap setDateBitmap(Bitmap bmp) {
        Bitmap temp = null;
        try {
            //Android默认的颜色模式为ARGB_8888，这个颜色模式色彩最细腻，显示质量最高。
            //但同样的，占用的内存//也最大。也就意味着一个像素点占用4个字节的内存。我们来做一个简单的计算题：3200*2400*4 bytes //=30M。如此惊人的数字！哪怕生命周期超不过10s，Android也不会答应的。
            temp = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(temp);
            // 建立画笔
            Paint paint = new Paint();
            paint.setDither(true);
            paint.setFilterBitmap(true);
            Rect src = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
            Rect dst = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
            canvas.drawBitmap(bmp, src, dst, paint);

            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
                    | Paint.DEV_KERN_TEXT_FLAG);
            textPaint.setTextSize(80.0f);
            // 采用默认的宽度
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            textPaint.setColor(Color.RED);

            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
            String str = sdf.format(now);
            String d = str.split("-")[0];
            String t = str.split("-")[1];
            Rect m = new Rect();
            Rect c = new Rect();
            textPaint.getTextBounds(d, 0, d.length() - 1, m);
            textPaint.getTextBounds(t, 0, t.length() - 1, c);
            canvas.drawText(d, bmp.getWidth() - m.width() - 100, bmp.getHeight() - 20, textPaint);
            canvas.drawText(t, bmp.getWidth() - c.width() - 100, bmp.getHeight() - 100, textPaint);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            temp = null;
            Log.e("PhotoUtil", "setDateBitmap: ", e);
        }
        return temp;
    }
}
