package com.imagepicker.xp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.xp.pro.imagepickerlib.ImagePickerBaseActivity;
import com.xp.pro.imagepickerlib.bean.ImageItem;
import com.xp.pro.imagepickerlib.global.Params;
import com.xp.pro.imagepickerlib.widgets.ImagePickerLayout;

import java.util.ArrayList;

public class ImagePickerDemo extends ImagePickerBaseActivity implements ImagePickerLayout.ImagePicker {


    ImagePickerLayout idImagePickerContainerIdcard;

    ImagePickerLayout idImagePickerContainerOther;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_picker_activity);
        setImagePickerIDCard();
        setImagePickerOther();
    }

    private void setImagePickerIDCard() {
        /*
         * 存放选择图片集合,需区分选择控件位置
         */
        ArrayList<ImageItem> mIdCardImageselectList = new ArrayList<>();

        idImagePickerContainerIdcard = (ImagePickerLayout) findViewById(R.id.id_image_picker_container_idcard);
        idImagePickerContainerIdcard.setTitle("上传身份证图片");
        idImagePickerContainerIdcard.setTip("(最多1张)");
        idImagePickerContainerIdcard.setImagePicker(this);
        idImagePickerContainerIdcard.setSizePhotoNum(3);
        idImagePickerContainerIdcard.setMaxPhotoNum(1);
        idImagePickerContainerIdcard.setmImageselectList(mIdCardImageselectList);
        idImagePickerContainerIdcard.setImagePickerView(idImagePickerContainerIdcard);
        idImagePickerContainerIdcard.setOlnyViewMode(true);

        mIdCardImageselectList = ImageNetData.getNetImageList();
        idImagePickerContainerIdcard.setmImageselectList(mIdCardImageselectList);
        idImagePickerContainerIdcard.setImagePickerView(idImagePickerContainerIdcard);
        idImagePickerContainerIdcard.refreshPhotoContentView(mIdCardImageselectList);
    }

    private void setImagePickerOther() {
        /*
         * 存放选择图片集合,需区分选择控件位置
         */
        ArrayList<ImageItem> mOtherImageselectList = new ArrayList<>();
        idImagePickerContainerOther = (ImagePickerLayout) findViewById(R.id.id_image_picker_container_other);
        idImagePickerContainerOther.setTitle("上传其它图片");
        idImagePickerContainerOther.setTip("(最多1张)");
        idImagePickerContainerOther.setImagePicker(this);
        idImagePickerContainerOther.setSizePhotoNum(3);
        idImagePickerContainerOther.setMaxPhotoNum(1);
        idImagePickerContainerOther.setmImageselectList(mOtherImageselectList);
        idImagePickerContainerOther.setImagePickerView(idImagePickerContainerOther);
    }

    @Override
    public void setSelectDialog(View imagePickerView) {
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
                getPictrue();
                hideSelectDialog();
            }
        });
        showSelectDialog("选择图片", null, "拍照", "相册");
    }

    @Override
    public void toPhotoPreview(int index, ArrayList<ImageItem> mImageselectList) {
        toPhotoPreviewFragment(index, mImageselectList);//大图显示
    }
}