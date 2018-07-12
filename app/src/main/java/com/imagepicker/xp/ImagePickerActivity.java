package com.imagepicker.xp;

import android.os.Bundle;

import com.imagepicker.xp.base.BaseActivity;
import com.imagepicker.xp.widgets.ImagePickerLayout;

public class ImagePickerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_picker_activity);

        ImagePickerLayout idImagePickerContainer =(ImagePickerLayout)findViewById(R.id.id_image_picker_container);
        idImagePickerContainer.setTitle("上传图片");
        idImagePickerContainer.setTip("最多3张");
        idImagePickerContainer.setSizePhotoNum(3);
    }
}