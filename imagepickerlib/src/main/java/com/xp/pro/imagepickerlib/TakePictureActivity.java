package com.xp.pro.imagepickerlib;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import com.xp.pro.imagepickerlib.base.BaseActivity;
import com.xp.pro.imagepickerlib.bean.ImageBean;
import com.xp.pro.imagepickerlib.utils.PathConfig;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TakePictureActivity extends BaseActivity {

    public final static String KEY_TAKE_IMAGES = "images";

    private static final int PHOTO_GRAPH = 1;
    private String fileName; // 图片文件名
    private String dirPath; // 文件路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            dirPath = savedInstanceState.getString("photoPath");
            fileName = savedInstanceState.getString("photoName");
            //intentTakePicture();
        }
        setContentView(R.layout.activity_take_picture);
        takePhoto();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("photoPath", dirPath);
        outState.putString("photoName", fileName);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        dirPath = savedInstanceState.getString("photoPath");
        fileName = savedInstanceState.getString("photoName");
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(!TextUtils.isEmpty(fileName)){
//            intentTakePicture();
//        }
//    }

    private void intentTakePicture() {
        List<ImageBean> selecteds = new ArrayList<ImageBean>();
        selecteds.add(new ImageBean(null, 0l, null, dirPath + "/"
                + fileName + ".jpg", false));
        Intent intent = new Intent();
        intent.putExtra(KEY_TAKE_IMAGES, (Serializable) selecteds);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            fileName = System.currentTimeMillis() + "";
            dirPath = PathConfig.getImagePath();
            File tempFile = new File(dirPath);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            File saveFile = new File(tempFile, fileName + ".jpg");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(saveFile));
            startActivityForResult(intent, PHOTO_GRAPH);
        } else {
            showNotifyMessage("未检测到CDcard，拍照不可用!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PHOTO_GRAPH:
                if (resultCode == Activity.RESULT_CANCELED) {
                    finish();
                    return;
                }

                if (resultCode == Activity.RESULT_OK) {
                    intentTakePicture();
                }
                break;
        }
    }
}
