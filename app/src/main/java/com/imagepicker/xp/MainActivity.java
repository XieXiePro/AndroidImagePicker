package com.imagepicker.xp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class MainActivity extends BaseActivity {
    private static final int SIZE_PHOTO_NUM = 4;
    private static final int PHOTO_NUM = 3;

    //当前的位置
    private final int TAKE_PICTURE = 0x000001;
    private final int GET_PICTURE = 0x000002;
    private final int GET_LOCATION = 0x000003;


    //问题类型的id值、匿名状态、定位相关
    private String id, anonymous = 0 + "", latitudtmp, longitudtmp, myaddress, mylongitud, mylatitude;

    private final ImageLoader mImageLoader = ImageLoader.getInstance();

    private static final String TAG = MainActivity.class.getSimpleName();
    //    private int mPhotoItemWidth = (DisplayUtil.getScreenWidth() - DisplayUtil.dip2px(10) * 2) / SIZE_PHOTO_NUM;
    private int mPhotoItemWidth = (DisplayUtil.getScreenWidth()) / SIZE_PHOTO_NUM;
    private FixGridLayout mPhotoContainer;

    private ArrayList<ImageItem> mImageselectList = new ArrayList<>();
    private ArrayList<Object> files;
    private Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        testForNet();

    }

    private void testForNet() {
        mImageselectList = ImageNetUtil.getNetImageList();
        refreshPhotoContentView();

    }

    /**
     * 初始化控件
     */
    private void initView() {
        setTitle("图片选择器");

        mBtn = (Button) findViewById(R.id.btn_save);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                testForNet();
            }
        });
        //图片容器
        mPhotoContainer = (FixGridLayout) findViewById(R.id.id_layout_fix_grid);
        mPhotoContainer.setmCellHeight(mPhotoItemWidth);
        mPhotoContainer.setmCellWidth(mPhotoItemWidth);
        mPhotoContainer.setmCellCount(SIZE_PHOTO_NUM);

        initPhotoContainer();
    }


    private void initPhotoContainer() {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mPhotoItemWidth, mPhotoItemWidth + 30);
        View photoView = LayoutInflater.from(this).inflate(R.layout.item_picker_grid, null);
        ImageView imageView = (ImageView) photoView.findViewById(R.id.item_grid_img);
        imageView.setImageResource(R.mipmap.icon_publish_bt);
        ImageView deleteImage = (ImageView) photoView.findViewById(R.id.item_grid_img_delete);
        deleteImage.setVisibility(View.GONE);
        View imageLayout = photoView.findViewById(R.id.item_grid_img_layout);
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
//                            intent.putParcelableArrayListExtra(AlbumActivity.KEY_PREVIEW_PHOTO, null);
                        }
                        startActivityForResultByAnimation(intent, GET_PICTURE);
                        hideSelectDialog();
                    }
                });
                showSelectDialog("选择图片", null, "拍照", "相册");
            }
        });
        mPhotoContainer.addView(photoView, layoutParams);
    }

    /**
     * 调用系统拍照
     */
    public void photo() {
        //Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResultByAnimation(openCameraIntent, TAKE_PICTURE);
        //onTakePhoto();

        Intent intent = new Intent(MainActivity.this, TakePictureActivity.class);
//        intent.putExtra("PictureLimit", PHOTO_NUM - mImageselectList.size());
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
        if (null != mImageselectList && mImageselectList.size() > 0) {
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
                addPhotoItem(i, mImageselectList.get(i));
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


    private void addPhotoItem(int position, ImageItem imageItem) {
        if (imageItem == null || TextUtils.isEmpty(imageItem.getImagePath())) {
            return;
        }

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mPhotoItemWidth, mPhotoItemWidth + 30);
        View photoView = LayoutInflater.from(this).inflate(R.layout.item_picker_grid, null);
        ImageView imageView = (ImageView) photoView.findViewById(R.id.item_grid_img);
        //mImageLoader.display(imageView, imageItem.getUri());
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
//                        photo_num_tv.setText(mImageselectList.size() + "");
                    }
                    if (index < mPhotoContainer.getChildCount()) {
                        mPhotoContainer.removeViewAt(index);
                        switchPlusItemStatus();
                    }
                }
            }
        });
        if (mPhotoContainer.getChildCount() > SIZE_PHOTO_NUM) {

        }
        mPhotoContainer.addView(photoView, mPhotoContainer.getChildCount() - 1, layoutParams);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (mImageselectList.size() < PHOTO_NUM && resultCode == RESULT_OK) {
//                    if (!TextUtils.isEmpty(phonePath)) {
//                        ImageItem takePhoto = new ImageItem();
//                        takePhoto.setUri(Uri.fromFile(new File(phonePath)));
//                        takePhoto.setImagePath(phonePath);
//                        takePhoto.setImageId(System.currentTimeMillis()+"");
//                        mImageselectList.add(takePhoto);
//                        refreshPhotoContentView();
//                    }else {
//                        // If here some stupid things happen;
//                        showNotifyMessage("拍摄失败...");
//                    }

                    List<ImageBean> images = (List<ImageBean>) data
                            .getSerializableExtra("images");
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

            case GET_LOCATION:
                myaddress = data.getStringExtra("address");
                mylongitud = data.getStringExtra("longitud");
                mylatitude = data.getStringExtra("latitude");

                if (TextUtils.isEmpty(mylongitud) || TextUtils.isEmpty(mylatitude)) {
                    showNotifyMessage("定位信息获取失败，请重试..");
                    return;
                }

                if (!TextUtils.isEmpty(myaddress) && !TextUtils.isEmpty(mylongitud) && !TextUtils.isEmpty(mylatitude)) {
//                    mylocation_tv.setText(myaddress);
                    latitudtmp = mylongitud;
                    longitudtmp = mylatitude;
                    System.out.println("address=" + myaddress + "longitud="
                            + latitudtmp + "latitude=" + longitudtmp);
                } else {
                    Log.e(TAG, "longitud=" + latitudtmp + "latitude=" + longitudtmp);
                }
                break;
        }
        if (null != mImageselectList) {
//            photo_num_tv.setText(String.valueOf(mImageselectList.size()));
        }
    }


}
