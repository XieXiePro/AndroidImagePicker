package com.imagepicker.xp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.imagepicker.xp.base.BaseActivity;
import com.imagepicker.xp.bean.ImageBean;
import com.imagepicker.xp.bean.ImageItem;
import com.imagepicker.xp.global.Params;
import com.imagepicker.xp.localalbum.AlbumActivity;
import com.imagepicker.xp.utils.ImageNetUtil;
import com.imagepicker.xp.widgets.ImagePickerLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagePickerActivity extends BaseActivity implements ImagePickerLayout.ImagePicker {
    /**
     * 存放选择图片集合,需区分选择控件位置
     */
    ArrayList<ImageItem> mIdCardImageselectList = new ArrayList<>();
    ImagePickerLayout idImagePickerContainerIdcard;
    /**
     * 存放选择图片集合,需区分选择控件位置
     */
    ArrayList<ImageItem> mOtherImageselectList = new ArrayList<>();

    ImagePickerLayout idImagePickerContainerOther;

    ImagePickerLayout idImagePickerContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_picker_activity);
        setImagePickerIDCard();
        setImagePickerOther();
    }

    private void setImagePickerIDCard() {
        idImagePickerContainerIdcard = (ImagePickerLayout) findViewById(R.id.id_image_picker_container_idcard);
        idImagePickerContainerIdcard.setTitle("上传身份证图片");
        idImagePickerContainerIdcard.setTip("最多3张");
        idImagePickerContainerIdcard.setImagePicker(this);
        idImagePickerContainerIdcard.setSizePhotoNum(3);
        idImagePickerContainerIdcard.setMaxPhotoNum(3);
        idImagePickerContainerIdcard.setmImageselectList(mIdCardImageselectList);
        idImagePickerContainerIdcard.setImagePickerView(idImagePickerContainerIdcard);

        mIdCardImageselectList = ImageNetUtil.getNetImageList();
        idImagePickerContainerIdcard.refreshPhotoContentView(mIdCardImageselectList);
    }

    private void setImagePickerOther() {
        idImagePickerContainerOther = (ImagePickerLayout) findViewById(R.id.id_image_picker_container_other);
        idImagePickerContainerOther.setTitle("上传其它图片");
        idImagePickerContainerOther.setTip("最多9张");
        idImagePickerContainerOther.setImagePicker(this);
        idImagePickerContainerOther.setSizePhotoNum(3);
        idImagePickerContainerOther.setMaxPhotoNum(9);
        idImagePickerContainerOther.setmImageselectList(mOtherImageselectList);
        idImagePickerContainerOther.setImagePickerView(idImagePickerContainerOther);
    }

    @Override
    public void setSelectDialog(final View imagePickerView, final ArrayList<ImageItem> mImageselectList) {
        idImagePickerContainer = (ImagePickerLayout) imagePickerView;
        setSelectDialogListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拍照处理
                takePhoto();
                hideSelectDialog();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //相片处理
                getPictrue(mImageselectList);
                hideSelectDialog();
            }
        });
        showSelectDialog("选择图片", null, "拍照", "相册");
    }

    @Override
    public void toPhotoPreview(int index, ArrayList<ImageItem> mImageselectList) {
        toPhotoPreviewFragment(index, mImageselectList);//大图显示
    }

    /**
     * 选择图片
     */
    private void getPictrue(ArrayList<ImageItem> mImageselectList) {
        Intent intent = new Intent(ImagePickerActivity.this, AlbumActivity.class);
        intent.putExtra("photo_num", idImagePickerContainer.getMaxPhotoNum());
        if (mImageselectList != null && mImageselectList.size() > 0) {
            intent.putParcelableArrayListExtra(AlbumActivity.KEY_PREVIEW_PHOTO, mImageselectList);
        }
        startActivityForResultByAnimation(intent, Params.GET_PICTURE);
    }

    /**
     * 调用系统拍照
     */
    public void takePhoto() {
        Intent intent = new Intent(ImagePickerActivity.this, TakePictureActivity.class);
        startActivityForResult(intent, Params.TAKE_PICTURE);
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

    private void refreshAfterTakePicture(Intent data, ArrayList<ImageItem> mImageselectList, ImagePickerLayout idImagePickerContainer) {
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
        idImagePickerContainer.refreshPhotoContentView(mImageselectList);
    }

    private void refreshAfterGetPicture(Intent data, ArrayList<ImageItem> mImageselectList, ImagePickerLayout idImagePickerContainer) {
        mImageselectList = data.getParcelableArrayListExtra(AlbumActivity.KEY_PREVIEW_PHOTO);
        idImagePickerContainer.refreshPhotoContentView(mImageselectList);
    }
}