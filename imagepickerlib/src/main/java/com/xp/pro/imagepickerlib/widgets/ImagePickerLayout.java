package com.xp.pro.imagepickerlib.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xp.pro.imagepickerlib.R;
import com.xp.pro.imagepickerlib.bean.ImageItem;
import com.xp.pro.imagepickerlib.utils.DisplayUtil;
import com.xp.pro.imagepickerlib.utils.ImageLoader;

import java.util.ArrayList;

/**
 * ImagePickerLayout: 图片选择容器布局
 * Author: xp
 * Date: 18/7/11 23:29
 * Email: xiexiepro@gmail.com
 * Blog: http://XieXiePro.github.io
 */
public class ImagePickerLayout extends LinearLayout {
    private Context context;

    private String title;
    private String tip;
    FixGridLayout imagePickerContainer;
    TextView imagePickerNeedTv;
    TextView imagePickerTitleTv;
    TextView imagePickerTipTv;
    View photoView;

    /**
     * 每行展示图片数，默认3张
     */
    private int sizePhotoNum = 3;
    /**
     * 限制最大图片数，默认9张
     */
    private int maxPhotoNum = 9;

    private int mPhotoItemWidth;

    private final ImageLoader mImageLoader = ImageLoader.getInstance();
    /**
     * 存放选择图片集合,需区分选择控件位置
     */
    private ArrayList<ImageItem> mImageselectList = new ArrayList<>();

    /**
     * 存放选择图片集合,需区分选择控件位置
     */
    private View imagePickerView;
    /**
     * 是否仅显示模式：true:仅显示模式，无法选择、修改或删除
     */
    private boolean olnyViewMode;

    private ImagePicker imagePicker;
    /**
     * 拍照或相册选择类型:0:既可拍照，又可选择相册，1：只可拍照,2:只可选择相册
     */
    private int selectType = 0;

    public interface ImagePicker {
        void setSelectDialog(int selectType, View imagePickerView);

        void toPhotoPreview(int index, ArrayList<ImageItem> mImageselectList);
    }


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

    public int getSelectType() {
        return selectType;
    }

    public void setSelectType(int selectType) {
        this.selectType = selectType;
    }

    public boolean isOlnyViewMode() {
        return olnyViewMode;
    }

    public void setOlnyViewMode(boolean olnyViewMode) {
        this.olnyViewMode = olnyViewMode;
    }

    public View getImagePickerView() {
        return imagePickerView;
    }

    public void setImagePickerView(View imagePickerView) {
        this.imagePickerView = imagePickerView;
    }

    public ArrayList<ImageItem> getmImageselectList() {
        return mImageselectList;
    }

    public void setmImageselectList(ArrayList<ImageItem> mImageselectList) {
        this.mImageselectList = mImageselectList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        imagePickerTitleTv.setText(title);
    }

    public void setTitleVisibility(int visibility) {
        imagePickerTitleTv.setVisibility(visibility);
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
        imagePickerTipTv.setText(tip);
    }

    public void setTipVisibility(int visibility) {
        imagePickerTipTv.setVisibility(visibility);
    }

    public void setNeedVisibility(int visibility) {
        imagePickerNeedTv.setVisibility(visibility);
    }

    public ImagePicker getImagePicker() {
        return imagePicker;
    }

    public void setImagePicker(ImagePicker imagePicker) {
        this.imagePicker = imagePicker;
    }

    public int getMaxPhotoNum() {
        return maxPhotoNum;
    }

    public void setMaxPhotoNum(int maxPhotoNum) {
        this.maxPhotoNum = maxPhotoNum;
    }

    public int getSizePhotoNum() {
        return sizePhotoNum;
    }

    public void setSizePhotoNum(int sizePhotoNum) {
        this.sizePhotoNum = sizePhotoNum;
    }

    private void setAddPickerView() {
        mPhotoItemWidth = DisplayUtil.getScreenWidth(context) / sizePhotoNum;
        imagePickerContainer.setmCellCount(sizePhotoNum);
        imagePickerContainer.setmCellHeight(mPhotoItemWidth);
        imagePickerContainer.setmCellWidth(mPhotoItemWidth);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mPhotoItemWidth, mPhotoItemWidth + 10);
        photoView = LayoutInflater.from(context).inflate(R.layout.item_picker_grid, null);
        ImageView addImage = (ImageView) photoView.findViewById(R.id.item_grid_img);
        addImage.setImageResource(R.mipmap.icon_add_img);
        View imageLayout = photoView.findViewById(R.id.item_grid_img_layout);
        ImageView deleteImage = (ImageView) photoView.findViewById(R.id.item_grid_img_delete);
        deleteImage.setVisibility(View.GONE);

        if (isOlnyViewMode()) {
            imageLayout.setVisibility(View.GONE);
        } else {
            imageLayout.setVisibility(View.VISIBLE);
        }
        //弹出拍照或选择图片对话框
        imageLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != imagePickerView) {
                    imagePicker.setSelectDialog(getSelectType(), imagePickerView);
                }
            }
        });
        imagePickerContainer.addView(photoView, layoutParams);
    }

    private void init(Context context) {
        View layout = LayoutInflater.from(context).inflate(R.layout.image_picker_layout, this, true);
        imagePickerNeedTv = (TextView) layout.findViewById(R.id.image_picker_need_tv);
        imagePickerTitleTv = (TextView) layout.findViewById(R.id.image_picker_title_tv);
        imagePickerTipTv = (TextView) layout.findViewById(R.id.image_picker_tip_tv);
        //setPhotoLayout
        imagePickerContainer = (FixGridLayout) layout.findViewById(R.id.image_picker_container);
        setAddPickerView();
    }

    /**
     * 刷新发布界面中，发布图片信息
     */
    public void refreshPhotoContentView(final ArrayList<ImageItem> mImageselectList) {
        //处理耗时操作
//        ThreadUtils.runOnNonUIthread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        if (null != mImageselectList && !mImageselectList.isEmpty()) {
//                            for (ImageItem imageItem : mImageselectList) {
//                                if (!TextUtils.isEmpty(imageItem.imagePath) && imageItem.getType() != 1) {
//                                    if (imageItem.getType() == 0 && !imageItem.isMark()) {
//                                        //选择图片后，设置显示路径为Cache路径
//                                        imageItem.setImagePath(PhotoFileUtils.saveBitmap(context, imageItem.uri, imageItem.imageId));
//                                        imageItem.setMark(true);
//                                    } else if (imageItem.getType() == 2 && !imageItem.isMark()) {
//                                        //如果是拍照，直接修改图片
//                                        PhotoFileUtils.savePhotoBitmap(context, imageItem.uri, imageItem.imagePath);
//                                        imageItem.setMark(true);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//        );
        //处理界面操作
        if (null != mImageselectList && !mImageselectList.isEmpty()) {
            clearPhotoItem();
            for (ImageItem imageItem : mImageselectList) {
                addPhotoItem(mImageselectList, imageItem);
                switchPlusItemStatus();
            }
        } else {
            clearPhotoItem();
        }
    }

    private void switchPlusItemStatus() {
        if (imagePickerContainer.getChildCount() > maxPhotoNum) {
            imagePickerContainer.getChildAt(imagePickerContainer.getChildCount() - 1).setVisibility(View.GONE);
            imagePickerContainer.setOverMaxSize(true);
        } else {
            imagePickerContainer.getChildAt(imagePickerContainer.getChildCount() - 1).setVisibility(View.VISIBLE);
            imagePickerContainer.setOverMaxSize(false);
        }
    }

    private void addPhotoItem(final ArrayList<ImageItem> mImageselectList, ImageItem imageItem) {
        if (imageItem == null || TextUtils.isEmpty(imageItem.getImagePath())) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mPhotoItemWidth, mPhotoItemWidth + 30);
        View photoView = LayoutInflater.from(context).inflate(R.layout.item_picker_grid, null);
        ImageView imageView = (ImageView) photoView.findViewById(R.id.item_grid_img);
        if (imageItem.getType() == 1) {
            mImageLoader.display(imageView, imageItem.getImgneturl(), 0.8f); //显示缩略图
        } else {
            mImageLoader.display(imageView, imageItem.getUri()); //显示缩略图
        }
        View imageLayout = photoView.findViewById(R.id.item_grid_img_layout);
        imageLayout.setTag(photoView);
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = imagePickerContainer.indexOfChild((View) v.getTag());
                if (index > -1 && index < mImageselectList.size()) {
                    imagePicker.toPhotoPreview(index, mImageselectList);//大图显示
                }
            }
        });

        ImageView deleteImage = (ImageView) photoView.findViewById(R.id.item_grid_img_delete);
        if (isOlnyViewMode() || imageItem.isSingleModify()) {
            deleteImage.setVisibility(View.GONE);
        } else {
            deleteImage.setVisibility(View.VISIBLE);
        }

        deleteImage.setTag(photoView);
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = imagePickerContainer.indexOfChild((View) v.getTag());
                if (index > -1) {
                    if (index < mImageselectList.size()) {
                        mImageselectList.remove(index);
                    }
                    if (index < imagePickerContainer.getChildCount()) {
                        imagePickerContainer.removeViewAt(index);
                        switchPlusItemStatus();
                    }
                }
            }
        });
        imagePickerContainer.setOlnyViewMode(isOlnyViewMode());
        imagePickerContainer.addView(photoView, imagePickerContainer.getChildCount() - 1, layoutParams);
    }

    private void clearPhotoItem() {
        if (imagePickerContainer != null && imagePickerContainer.getChildCount() > 1) {
            for (int i = 0; i < imagePickerContainer.getChildCount() - 1; i++) {
                imagePickerContainer.removeViewAt(i);
                i--;
            }
        }
    }
}