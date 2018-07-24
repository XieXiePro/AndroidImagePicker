package com.xp.pro.imagepickerlib;

import android.content.Intent;
import android.net.Uri;

import com.xp.pro.imagepickerlib.base.BaseActivity;
import com.xp.pro.imagepickerlib.bean.ImageBean;
import com.xp.pro.imagepickerlib.bean.ImageItem;
import com.xp.pro.imagepickerlib.global.Params;
import com.xp.pro.imagepickerlib.localalbum.AlbumActivity;
import com.xp.pro.imagepickerlib.widgets.ImagePickerLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagePickerBaseActivity extends BaseActivity {

    protected ArrayList<ImageItem> mImageselectList = new ArrayList<>();

    protected ImagePickerLayout idImagePickerContainer;

    /**
     * 选择图片
     */
    protected void getPictrue(ImagePickerLayout idImagePickerContainer) {
        ArrayList<ImageItem>  imageselectList = idImagePickerContainer.getmImageselectList();
        Intent intent = new Intent(ImagePickerBaseActivity.this, AlbumActivity.class);
        intent.putExtra("photo_num", idImagePickerContainer.getMaxPhotoNum());
        if (imageselectList != null && !imageselectList.isEmpty()) {
            intent.putParcelableArrayListExtra(AlbumActivity.KEY_PREVIEW_PHOTO, imageselectList);
        }
        startActivityForResultByAnimation(intent, Params.GET_PICTURE);
    }

    /**
     * 调用系统拍照
     */
    protected void takePhoto() {
        Intent intent = new Intent(ImagePickerBaseActivity.this, TakePictureActivity.class);
        startActivityForResultByAnimation(intent, Params.TAKE_PICTURE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != data && resultCode == RESULT_OK) {
            switch (requestCode) {
                case Params.TAKE_PICTURE:
                    refreshAfterTakePicture(data, idImagePickerContainer.getmImageselectList(), idImagePickerContainer);
                    break;
                case Params.GET_PICTURE:
                    refreshAfterGetPicture(data, idImagePickerContainer.getmImageselectList(), idImagePickerContainer);
                    break;
                default:
                    break;
            }
        }
    }
}