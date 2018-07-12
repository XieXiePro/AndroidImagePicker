package com.imagepicker.xp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.imagepicker.xp.base.BaseActivity;
import com.imagepicker.xp.bean.ImageBean;
import com.imagepicker.xp.bean.ImageItem;
import com.imagepicker.xp.localalbum.AlbumActivity;
import com.imagepicker.xp.utils.DisplayUtil;
import com.imagepicker.xp.utils.ImageLoader;
import com.imagepicker.xp.utils.ImageNetUtil;
import com.imagepicker.xp.utils.PhotoFileUtils;
import com.imagepicker.xp.widgets.FixGridLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    /**
     * 每行展示图片数
     */
    private static final int SIZE_PHOTO_NUM = 4;
    /**
     * 限制最大图片数
     */
    private static final int PHOTO_NUM = 9;
    /**
     * 拍照
     */
    private final int TAKE_PICTURE = 0x000001;
    /**
     * 选择图片
     */
    private final int GET_PICTURE = 0x000002;

    /**
     * 单张图片宽度
     */
    private int mPhotoItemWidth = (DisplayUtil.getScreenWidth() - DisplayUtil.dip2px(10) * 2) / SIZE_PHOTO_NUM;
//    private int mPhotoItemWidth = (DisplayUtil.getScreenWidth()) / SIZE_PHOTO_NUM;

    private final ImageLoader mImageLoader = ImageLoader.getInstance();

    private FixGridLayout mPhotoContainer;

    private ArrayList<ImageItem> mImageselectList = new ArrayList<>();
    private ArrayList<Object> files;
    private Button mBtn;

    /**
     * 添加图标
     */
    ImageView addImage;
    /**
     * 图片装载容器
     */
    View imageLayout;
    /**
     * 1，查询；2，修改；3，新增
     */
    int viewMode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_picker_layout);
        initView();
        testForNet();
    }

    private void testForNet() {
        mImageselectList = ImageNetUtil.getNetImageList();
        refreshPhotoContentView();
    }

    /**
     * @param flag 1，查询；2，修改；3，新增
     */
    private void setViewByFlag(int flag) {
        if (flag == 1) {
            addImage.setVisibility(View.GONE);
            imageLayout.setOnClickListener(null);
            refreshPhotoContentView();
        } else if (flag == 2 || flag == 3) {
            addImage.setVisibility(View.VISIBLE);
            imageLayout.setOnClickListener(this);
            refreshPhotoContentView();
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        setTitle("图片选择器");

//        mBtn = (Button) findViewById(R.id.btn_save);
//
//        mBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                testForNet();
//                if (viewMode == 1) {
//                    viewMode = 2;
//                    setViewByFlag(2);
//                } else if (viewMode == 2 || viewMode == 3) {
//                    viewMode = 1;
//                    setViewByFlag(1);
//                }
//            }
//        });
        //图片容器
        mPhotoContainer = (FixGridLayout) findViewById(R.id.image_picker_container);
        mPhotoContainer.setmCellHeight(mPhotoItemWidth);
        mPhotoContainer.setmCellWidth(mPhotoItemWidth);
        mPhotoContainer.setmCellCount(SIZE_PHOTO_NUM);

        initPhotoContainer();
    }


    private void initPhotoContainer() {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mPhotoItemWidth, mPhotoItemWidth + 10);
        View photoView = LayoutInflater.from(this).inflate(R.layout.item_picker_grid, null);
        addImage = (ImageView) photoView.findViewById(R.id.item_grid_img);
        addImage.setImageResource(R.mipmap.icon_publish_bt);
        ImageView deleteImage = (ImageView) photoView.findViewById(R.id.item_grid_img_delete);
        deleteImage.setVisibility(View.GONE);

        if (viewMode == 1) {
            addImage.setVisibility(View.GONE);
        } else {
            addImage.setVisibility(View.VISIBLE);
        }
        imageLayout = photoView.findViewById(R.id.item_grid_img_layout);
        mPhotoContainer.addView(photoView, layoutParams);
    }

    private void setSelectDialog() {
        setSelectDialogListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拍照处理
                photo();
                hideSelectDialog();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //相片处理
                Intent intent = new Intent(MainActivity.this,
                        AlbumActivity.class);
                intent.putExtra("photo_num", PHOTO_NUM);

                if (mImageselectList != null && mImageselectList.size() > 0) {
                    intent.putParcelableArrayListExtra(AlbumActivity.KEY_PREVIEW_PHOTO, mImageselectList);
                }
                startActivityForResultByAnimation(intent, GET_PICTURE);
                hideSelectDialog();
            }
        });
        showSelectDialog("选择图片", null, "拍照", "相册");
    }

    /**
     * 调用系统拍照
     */
    public void photo() {
        Intent intent = new Intent(MainActivity.this, TakePictureActivity.class);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    private void clearPhotoItem() {
        if (mPhotoContainer != null && mPhotoContainer.getChildCount() > 1) {
            for (int i = 0; i < mPhotoContainer.getChildCount() - 1; i++) {
                mPhotoContainer.removeViewAt(i);
                i--;
            }
        }
    }

    /**
     * 刷新发布界面中，发布图片信息
     */
    private void refreshPhotoContentView() {
        if (null != mImageselectList && !mImageselectList.isEmpty()) {
            int size = mImageselectList.size();
            files = null;
            files = new ArrayList<>();
            clearPhotoItem();
            for (int i = 0; i < size; i++) {
                if (!TextUtils.isEmpty(mImageselectList.get(i).imagePath) && mImageselectList.get(i).getType() != 1) {
                    files.add(PhotoFileUtils.saveBitmap(this,
                            mImageselectList.get(i).uri,
                            mImageselectList.get(i).imageId));
                }
                addPhotoItem(mImageselectList.get(i));
                switchPlusItemStatus();
            }
        } else {
            clearPhotoItem();
        }
    }

    private void switchPlusItemStatus() {
        if (mPhotoContainer.getChildCount() > PHOTO_NUM) {
            mPhotoContainer.getChildAt(mPhotoContainer.getChildCount() - 1).setVisibility(View.GONE);
        } else {
            mPhotoContainer.getChildAt(mPhotoContainer.getChildCount() - 1).setVisibility(View.VISIBLE);
        }
    }


    private void addPhotoItem(ImageItem imageItem) {
        if (imageItem == null || TextUtils.isEmpty(imageItem.getImagePath())) {
            return;
        }

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mPhotoItemWidth, mPhotoItemWidth + 30);
        View photoView = LayoutInflater.from(this).inflate(R.layout.item_picker_grid, null);
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
                int index = mPhotoContainer.indexOfChild((View) v.getTag());
                if (index > -1 && index < mImageselectList.size()) {
                    toPhotoPreviewFragment(index, mImageselectList);//大图显示
                }
            }
        });

        ImageView deleteImage = (ImageView) photoView.findViewById(R.id.item_grid_img_delete);
        if (viewMode == 1) {
            deleteImage.setVisibility(View.INVISIBLE);
        } else {
            deleteImage.setVisibility(View.VISIBLE);
        }
        deleteImage.setTag(photoView);
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = mPhotoContainer.indexOfChild((View) v.getTag());
                if (index > -1) {
                    if (files != null) {
                        if (index < files.size()) {
                            files.remove(index);
                        }
                    }
                    if (index < mImageselectList.size()) {
                        mImageselectList.remove(index);
                    }
                    if (index < mPhotoContainer.getChildCount()) {
                        mPhotoContainer.removeViewAt(index);
                        switchPlusItemStatus();
                    }
                }
            }
        });
        mPhotoContainer.addView(photoView, mPhotoContainer.getChildCount() - 1, layoutParams);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (mImageselectList.size() < PHOTO_NUM && resultCode == RESULT_OK) {
                    List<ImageBean> images = (List<ImageBean>) data.getSerializableExtra("images");
                    for (ImageBean b : images) {
                        ImageItem imageItem = new ImageItem();
                        String path = b.path;
                        imageItem.setImagePath(path);
                        imageItem.setType(2);
                        imageItem.setImageId(System.currentTimeMillis() + "");
                        imageItem.setUri(Uri.fromFile(new File(path)));
                        mImageselectList.add(imageItem);
                    }
                    refreshPhotoContentView();
                }
                break;

            case GET_PICTURE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        mImageselectList = data.getParcelableArrayListExtra(AlbumActivity.KEY_PREVIEW_PHOTO);
                    }
                    refreshPhotoContentView();
                }
                break;
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.item_grid_img_layout) {
            setSelectDialog();
        }
    }
}
