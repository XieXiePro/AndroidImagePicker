package com.imagepicker.xp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.xp.pro.imagepickerlib.ImagePickerBaseActivity;
import com.xp.pro.imagepickerlib.TakePictureActivity;
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
        getImagePickerData();
    }

    private void setImagePickerIDCard() {
        ArrayList<ImageItem> mIdCardImageselectList = new ArrayList<>();
        idImagePickerContainerIdcard = (ImagePickerLayout) findViewById(R.id.id_image_picker_container_idcard);
        idImagePickerContainerIdcard.setTitle("身份证图片");
        idImagePickerContainerIdcard.setTip("(最多1张)");
        idImagePickerContainerIdcard.setTitleVisibility(View.GONE);
        idImagePickerContainerIdcard.setTipVisibility(View.GONE);
        idImagePickerContainerIdcard.setImagePicker(this);
        idImagePickerContainerIdcard.setSizePhotoNum(3);
        idImagePickerContainerIdcard.setMaxPhotoNum(3);
        idImagePickerContainerIdcard.setmImageselectList(mIdCardImageselectList);
        idImagePickerContainerIdcard.setImagePickerView(idImagePickerContainerIdcard);
        idImagePickerContainerIdcard.setOlnyViewMode(true);

        mIdCardImageselectList = ImageNetData.getNetImageList();
        idImagePickerContainerIdcard.setmImageselectList(mIdCardImageselectList);
        idImagePickerContainerIdcard.refreshPhotoContentView(mIdCardImageselectList);
    }

    private void setImagePickerOther() {
        ArrayList<ImageItem> mOtherImageselectList = new ArrayList<>();
        idImagePickerContainerOther = (ImagePickerLayout) findViewById(R.id.id_image_picker_container_other);
        idImagePickerContainerOther.setTitle("其它图片");
        idImagePickerContainerOther.setTip("(最多1张)");
        idImagePickerContainerOther.setImagePicker(this);
        idImagePickerContainerOther.setSizePhotoNum(3);
        idImagePickerContainerOther.setMaxPhotoNum(3);
        idImagePickerContainerOther.setmImageselectList(mOtherImageselectList);
        idImagePickerContainerOther.setImagePickerView(idImagePickerContainerOther);
    }

    @Override
    public void setSelectDialog(int selectType,View imagePickerView) {
        idImagePickerContainer = (ImagePickerLayout) imagePickerView;
        setSelectDialogListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拍照处理
                hideSelectDialog();
                takePhoto();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //相片处理
                hideSelectDialog();
                getPictrue(idImagePickerContainer);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSelectDialog();
            }
        });
        showSelectDialog("选择图片", null, "相机拍照", "本地相册", "取消");
    }

    @Override
    public void toPhotoPreview(int index, ArrayList<ImageItem> mImageselectList) {
        toPhotoPreviewFragment(index, mImageselectList);//大图显示
    }

    /**
     * 调用系统拍照
     */
    protected void takePhoto() {
        Intent intent = new Intent(this, TakePictureActivity.class);
        intent.putExtra("authority", "com.imagepicker.xp");
        startActivityForResultByAnimation(intent, Params.TAKE_PICTURE);
    }

    @SuppressLint("LongLogTag")
    private void getImagePickerData() {
        findViewById(R.id.get_image_picker_data_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("idImagePickerContainerIdcard:", idImagePickerContainerIdcard.getmImageselectList().toString());
                Log.d("idImagePickerContainerOther:", idImagePickerContainerOther.getmImageselectList().toString());
            }
        });
    }
}