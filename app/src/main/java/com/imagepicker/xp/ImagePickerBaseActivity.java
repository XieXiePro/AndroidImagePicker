package com.imagepicker.xp;

import android.content.Intent;
import android.net.Uri;
import com.imagepicker.xp.base.BaseActivity;
import com.imagepicker.xp.bean.ImageBean;
import com.imagepicker.xp.bean.ImageItem;
import com.imagepicker.xp.global.Params;
import com.imagepicker.xp.localalbum.AlbumActivity;
import com.imagepicker.xp.widgets.ImagePickerLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagePickerBaseActivity extends BaseActivity{

    ArrayList<ImageItem> mImageselectList = new ArrayList<>();


    ImagePickerLayout idImagePickerContainer;


    /**
     * 选择图片
     */
    protected void getPictrue() {
        mImageselectList = idImagePickerContainer.getmImageselectList();
        Intent intent = new Intent(ImagePickerBaseActivity.this, AlbumActivity.class);
        intent.putExtra("photo_num", idImagePickerContainer.getMaxPhotoNum());
        if (mImageselectList != null && mImageselectList.size() > 0) {
            intent.putParcelableArrayListExtra(AlbumActivity.KEY_PREVIEW_PHOTO, mImageselectList);
        }
        startActivityForResultByAnimation(intent, Params.GET_PICTURE);
    }

    /**
     * 调用系统拍照
     */
    protected void takePhoto() {
        Intent intent = new Intent(ImagePickerBaseActivity.this, TakePictureActivity.class);
        startActivityForResult(intent, Params.TAKE_PICTURE);
    }

    protected void refreshAfterTakePicture(Intent data, ArrayList<ImageItem> mImageselectList, ImagePickerLayout idImagePickerContainer) {
        List<ImageBean> images = data.getParcelableArrayListExtra(TakePictureActivity.KEY_TAKE_IMAGES);
        for (ImageBean b : images) {
            ImageItem imageItem = new ImageItem();
            String path = b.path;
            imageItem.setImagePath(path);
            imageItem.setType(2);
            imageItem.setImageId(System.currentTimeMillis() + "");
            imageItem.setUri(Uri.fromFile(new File(path)));
            mImageselectList.add(imageItem);
        }
        idImagePickerContainer.setmImageselectList(mImageselectList);
        idImagePickerContainer.refreshPhotoContentView(mImageselectList);
    }

    protected void refreshAfterGetPicture(Intent data, ArrayList<ImageItem> mImageselectList, ImagePickerLayout idImagePickerContainer) {
        mImageselectList = data.getParcelableArrayListExtra(AlbumActivity.KEY_PREVIEW_PHOTO);
        idImagePickerContainer.setmImageselectList(mImageselectList);
        idImagePickerContainer.refreshPhotoContentView(mImageselectList);
    }
}