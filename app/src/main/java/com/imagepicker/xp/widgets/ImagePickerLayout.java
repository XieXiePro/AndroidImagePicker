package com.imagepicker.xp.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imagepicker.xp.R;
import com.imagepicker.xp.utils.DisplayUtil;

/**
 * ImagePickerLayout:
 * Author: xp
 * Date: 18/7/11 23:29
 * Email: xiexiepro@gmail.com
 * Blog: http://XieXiePro.github.io
 */
public class ImagePickerLayout extends LinearLayout {
    public ImagePickerLayout(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public ImagePickerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public ImagePickerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(context);
    }

    private Context context;

    private String title;
    private String tip;
    FixGridLayout imagePickerContainer;
    TextView imagePickerTitleTv;
    TextView imagePickerTipTv;
    View photoView;
    /**
     * 每行展示图片数
     */
    private int sizePhotoNum = 3;

    private int mPhotoItemWidth = DisplayUtil.getScreenWidth() / sizePhotoNum;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        imagePickerTitleTv.setText(title);
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
        imagePickerTipTv.setText(tip);
    }

    public int getSizePhotoNum() {
        return sizePhotoNum;
    }

    public void setSizePhotoNum(int sizePhotoNum) {
        this.sizePhotoNum = sizePhotoNum;
        imagePickerContainer.setmCellHeight(mPhotoItemWidth);
        imagePickerContainer.setmCellWidth(mPhotoItemWidth);
        imagePickerContainer.setmCellCount(sizePhotoNum);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mPhotoItemWidth, mPhotoItemWidth + 10);
        photoView = LayoutInflater.from(context).inflate(R.layout.item_picker_grid, null);
        ImageView addImage = (ImageView) photoView.findViewById(R.id.item_grid_img);
        addImage.setImageResource(R.mipmap.icon_publish_bt);
        ImageView deleteImage = (ImageView) photoView.findViewById(R.id.item_grid_img_delete);
        deleteImage.setVisibility(View.GONE);
        View imageLayout = photoView.findViewById(R.id.item_grid_img_layout);
        imageLayout.setVisibility(View.VISIBLE);
        imagePickerContainer.addView(photoView, layoutParams);
    }

    private void init(Context context) {
        View layout = LayoutInflater.from(context).inflate(R.layout.image_picker_layout, this, true);
        imagePickerTitleTv = (TextView) layout.findViewById(R.id.image_picker_title_tv);
        imagePickerTipTv = (TextView) layout.findViewById(R.id.image_picker_tip_tv);
        //setPhotoLayout
        imagePickerContainer = (FixGridLayout) layout.findViewById(R.id.image_picker_container);
    }
}