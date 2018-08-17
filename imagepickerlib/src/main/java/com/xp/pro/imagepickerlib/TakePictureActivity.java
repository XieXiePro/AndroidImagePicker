package com.xp.pro.imagepickerlib;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.xp.pro.imagepickerlib.base.BaseActivity;
import com.xp.pro.imagepickerlib.bean.ImageBean;
import com.xp.pro.imagepickerlib.utils.PathConfig;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.FileProvider.getUriForFile;

public class TakePictureActivity extends BaseActivity {

    public static final String KEY_TAKE_IMAGES = "images";

    private String authority = "authority";

    private static final int PHOTO_GRAPH = 1;
    private String fileName; // 图片文件名
    private String dirPath; // 文件路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            dirPath = savedInstanceState.getString("photoPath");
            fileName = savedInstanceState.getString("photoName");
//            intentTakePicture();
        }
        setContentView(R.layout.activity_take_picture);
        authority = getIntent().getStringExtra("authority");
        takePhoto(authority);

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
//        if (!TextUtils.isEmpty(fileName)) {
//            intentTakePicture();
//        }
//    }

    private void intentTakePicture() {
        List<ImageBean> selecteds = new ArrayList<ImageBean>();
        selecteds.add(new ImageBean(null, 0l, null, dirPath + "/" + fileName + ".jpg", false));
        Intent intent = new Intent();
        intent.putExtra(KEY_TAKE_IMAGES, (Serializable) selecteds);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 拍照
     */
    private void takePhoto(String authority) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            fileName = System.currentTimeMillis() + "";
            //拍照路径设置为应用根目录
            dirPath = PathConfig.getBasePath();
            File tempFile = new File(dirPath);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            File saveFile = new File(tempFile, fileName + ".jpg");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //7.0及其以后版本使用升级后的代码处理
                //判断是否有相机应用
                if (intent.resolveActivity(getPackageManager()) != null) {
                    Uri photoURI = getUriForFile(this, authority, saveFile);
                    //添加这一句表示对目标应用临时授权该Uri所代表的文件
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, PHOTO_GRAPH);
                }
            } else {
                //7.0之前还保持原来方案进行处理即可
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(saveFile));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, PHOTO_GRAPH);
                }
            }
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
