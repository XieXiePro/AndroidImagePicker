package com.xp.pro.imagepickerlib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片处理工具类
 */
public class PhotoFileUtils {
    public static final String TAG = PhotoFileUtils.class.getSimpleName();

    public static String SDPATH = Environment.getExternalStorageDirectory()
            + "/Photo_LJ/";

//    public static String saveBitmap(Bitmap bm, String picName) {
////		try {
////			if (!isFileExist("")) {
////				File tempf = createSDDir("");
////			}
////			File f = new File(SDPATH, picName + ".JPEG");
////			if (f.exists()) {
////				f.delete();
////			}
////			FileOutputStream out = new FileOutputStream(f);
////			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
////			out.flush();
////			out.close();
////					return f;
////		} catch (FileNotFoundException e) {
////			e.printStackTrace();
////		} catch (IOException e) {
////			e.printStackTrace();
////		}
////		return null;
//
////        if (!isFileExist("")) {
////            try {
////                File tempf = createSDDir("");
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
//        File f = new File(SDPATH, picName + ".JPEG");
//        f.deleteOnExit();
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        int options = 100;
//        //bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        //int imgSize = baos.toByteArray().length / 1024;
//        //Log.i(TAG, "imgSize1======" + imgsize);
//        while (baos.toByteArray().length / 1024 > 300) {
//            baos.reset();
//            options -= 10;
//            bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
//        }
//        try {
//            FileOutputStream fos = new FileOutputStream(f);
//            fos.write(baos.toByteArray());
//            fos.flush();
//            fos.close();
//            return f.getAbsolutePath();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * 报错bitmap
     * @param context
     * @param uri 图片uri
     * @param picName 图片名
     * @return
     */
    public static String saveBitmap(Context context, Uri uri, String picName) {
        try {
            Bitmap bm = getThumbnail(context, uri, 600);
            if(null != bm){
                //针对三星s4，拍出来的图片会自动旋转
//                if(DeviceFitUtil.getInstance().isSamsumgSC_I959()) {
//                    Matrix m = new Matrix();
//                    int width = bm.getWidth();
//                    int height = bm.getHeight();
//                    m.setRotate(90.0f); // 旋转angle度
//                    bm = Bitmap.createBitmap(bm, 0, 0, width, height,
//                            m, true);// 从新生成图片
//                }
                //首先在sdcard上创建根目录
                createSDDir("");
                //dirFile.mkdir();

                File f = new File(SDPATH, picName + ".jpg");
                f.deleteOnExit();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
                if(!bm.isRecycled()){
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
     * @param context
     * @param uri Uri
     * @param size 图片宽和高的最大值
     * @return
     * @throws IOException
     */
    public static Bitmap getThumbnail(Context context, Uri uri, int size) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig= Bitmap.Config.ARGB_4444;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;
        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth)
                ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
        double ratio = (originalSize > size) ? (originalSize / size) : 1.0;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither=true;//optional
        bitmapOptions.inPreferredConfig= Bitmap.Config.ARGB_4444;//optional
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }
    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }

    public static File createSDDir(String dirName) throws IOException {
        File dir = new File(SDPATH + dirName);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            System.out.println("createSDDir:" + dir.getAbsolutePath());
            System.out.println("createSDDir:" + dir.mkdir());
        }
        return dir;
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        file.isFile();
        return file.exists();
    }

    public static void delFile(String fileName) {
        File file = new File(SDPATH + fileName);
        if (file.isFile()) {
            file.delete();
        }
        file.exists();
    }

    public static void deleteDir() {
        File dir = new File(SDPATH);
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

}
