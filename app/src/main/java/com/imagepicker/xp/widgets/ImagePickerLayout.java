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
    }

    public ImagePickerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ImagePickerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs,defStyle);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String tip;
        FixGridLayout imagePickerContainer;
        /**
         * 每行展示图片数
         */
        private static final int SIZE_PHOTO_NUM = 4;

        private int mPhotoItemWidth = DisplayUtil.getScreenWidth() / SIZE_PHOTO_NUM;

        public Builder(Context context) {
            this.context = context;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTip() {
            return tip;
        }

        public void setTip(String tip) {
            this.tip = tip;
        }

        public ImagePickerLayout create() {
            final ImagePickerLayout imagePickerLayout = new ImagePickerLayout(context);
            View layout = View.inflate(context, R.layout.image_picker_layout, null);
            ((TextView) layout.findViewById(R.id.image_picker_title_tv)).setText(title);
            ((TextView) layout.findViewById(R.id.image_picker_tip_tv)).setText(tip);

            //setPhotoLayout
            imagePickerContainer = (FixGridLayout) layout.findViewById(R.id.image_picker_container);
            imagePickerContainer.setmCellHeight(mPhotoItemWidth);
            imagePickerContainer.setmCellWidth(mPhotoItemWidth);
            imagePickerContainer.setmCellCount(SIZE_PHOTO_NUM);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mPhotoItemWidth, mPhotoItemWidth + 10);
            View photoView = LayoutInflater.from(context).inflate(R.layout.item_picker_grid, null);
            ImageView addImage = (ImageView) photoView.findViewById(R.id.item_grid_img);
            addImage.setImageResource(R.mipmap.icon_publish_bt);
            ImageView deleteImage = (ImageView) photoView.findViewById(R.id.item_grid_img_delete);
            deleteImage.setVisibility(View.GONE);
            View imageLayout = photoView.findViewById(R.id.item_grid_img_layout);
            imageLayout.setVisibility(View.VISIBLE);
            imagePickerContainer.addView(photoView, layoutParams);
            return imagePickerLayout;
        }
    }
}