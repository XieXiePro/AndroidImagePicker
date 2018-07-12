package com.imagepicker.xp;

import android.os.Bundle;

import com.imagepicker.xp.base.BaseActivity;
import com.imagepicker.xp.widgets.ImagePickerLayout;

public class ImagePickerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_picker_activity);

        final ImagePickerLayout.Builder builder = new ImagePickerLayout.Builder(this);
        builder.setTitle("上传图片");
        builder.setTip("最多3张");
        builder.create();
    }
}
