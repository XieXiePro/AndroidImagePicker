package com.xp.pro.imagepickerlib.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedInputStream;
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
            if (null != bm) {
                //首先在sdcard上创建根目录
                createSDDir("");
                File f = new File(PathConfig.getImagePath(), picName + ".jpg");
                f.deleteOnExit();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //如果图片尺寸过大，对图片进行缩放
                revitionImageSize(f.getAbsolutePath());
                //给图片添加水印
                bm = setDateBitmap(bm);
                //压缩图片
                bm.compress(Bitmap.CompressFormat.JPEG, 10, baos);
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
     * 报错bitmap
     *
     * @param context
     * @param uri     图片uri
     * @param picName 图片名
     * @return
     */
    public static String savePhotoBitmap(Context context, Uri uri, String picName) {
        try {
            Bitmap bm = getThumbnail(context, uri);
            if (null != bm) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int degree = getBitmapDegree(picName);
                //图片被系统旋转,那我们就要把它转回来
                if (degree != 0) {
                    bm = rotateBitmapByDegree(bm, degree);
                }
                //如果图片尺寸过大，对图片进行缩放
                revitionImageSize(picName);
                //给图片添加水印
                bm = setDateBitmap(bm);
                //压缩图片
                bm.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                FileOutputStream fos = new FileOutputStream(picName);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
                if (!bm.isRecycled()) {
                    bm.recycle();
                }
                return picName;
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
            textPaint.setTextSize(60.0f);
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
            canvas.drawText(d, bmp.getWidth() - m.width() - 80, bmp.getHeight() - 20, textPaint);
            canvas.drawText(t, bmp.getWidth() - c.width() - 80, bmp.getHeight() - 80, textPaint);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            temp = null;
            Log.e("PhotoUtil", "setDateBitmap: ", e);
        }
        return temp;
    }

    /**
     * 读取图片的旋转的角度　　Sasukeより
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转　　Sasukeより
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    /**
     * 修改图片尺寸
     *
     * @param path 图片路径
     */
    public static Bitmap revitionImageSize(String path) {
        Bitmap bitmap = null;
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(new File(path)));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            int i = 0;
            while (true) {
                if ((options.outWidth >> i <= 1000)
                        && (options.outHeight >> i <= 1000)) {
                    in = new BufferedInputStream(new FileInputStream(new File(
                            path)));
                    options.inSampleSize = (int) Math.pow(2.0D, i);
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    break;
                }
                i += 1;
            }

        } catch (IOException e) {
            Log.e("PhotoUtils", "revitionImageSize: ", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("PhotoUtils", "revitionImageSize: ", e);
                }
            }
        }
        return bitmap;
    }
}
