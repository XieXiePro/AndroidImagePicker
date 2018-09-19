package com.xp.pro.imagepickerlib.localalbum;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.xp.pro.imagepickerlib.R;
import com.xp.pro.imagepickerlib.base.BaseActivity;
import com.xp.pro.imagepickerlib.bean.Folder;
import com.xp.pro.imagepickerlib.bean.ImageBucket;
import com.xp.pro.imagepickerlib.bean.ImageItem;
import com.xp.pro.imagepickerlib.bean.ImageModel;
import com.xp.pro.imagepickerlib.global.Params;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @Description 这个是进入相册显示所有图片的界面
 */
public class AlbumActivity extends BaseActivity {
    GridView mGridView;
    // gridView的adapter
    private AlbumGridViewAdapter gridImageAdapter;
    // 完成按钮
    private TextView okButton;
    // 预览按钮
    private TextView preview;
    private FrameLayout focus_ok_button, focus_preview;
    //private Context mContext;
    private ArrayList<ImageItem> dataList = new ArrayList<>();
    /**
     * 被选中的图片集合
     */
    private ArrayList<ImageItem> mImageselectList = new ArrayList<ImageItem>();
    //private AlbumHelper helper;
    public static List<ImageBucket> contentList;
    //public static Bitmap bitmap;

    private final static int REQUEST_CODE_PREVIEW = 1;

    public final static String KEY_PREVIEW_PHOTO = "key_preview_photo";

    private int photo_num = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_camera_album);
        //mContext = this;
        // 注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
        IntentFilter filter = new IntentFilter("data.broadcast.action");
        //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plugin_camera_no_pictures);
        initView();
        getImageList(mGridView);
        initListener();
        // 这个函数主要用来控制预览和完成按钮的状态
        isShowOkBt();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // 预览按钮的监听
    private class PreviewListener implements OnClickListener {
        public void onClick(View v) {
            if (null != mImageselectList && !mImageselectList.isEmpty()) {
                Intent intent = new Intent(AlbumActivity.this, GalleryActivity.class);
                intent.putExtra("position", "1");
                intent.putExtra("photo_num", photo_num);
                intent.putParcelableArrayListExtra(KEY_PREVIEW_PHOTO, mImageselectList);
                startActivityForResultByAnimation(intent, REQUEST_CODE_PREVIEW);
            }
        }
    }

    // 完成按钮的监听
    private class AlbumSendListener implements OnClickListener {
        public void onClick(View v) {
            overridePendingTransition(R.anim.activity_translate_in,
                    R.anim.activity_translate_out);
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(KEY_PREVIEW_PHOTO, mImageselectList);
            setResult(RESULT_OK, intent);
            finish();
        }

    }

    // 初始化，给一些对象赋值
    private void initView() {
        setTitle("相机胶卷");

        //有权限，加载图片。
//        loadImageForSDCard();

        preview = (TextView) findViewById(R.id.preview);
        focus_preview = (FrameLayout) findViewById(R.id.focus_preview);
        focus_preview.setOnClickListener(new PreviewListener());
        focus_preview.setVisibility(View.GONE);
        mGridView = (GridView) findViewById(R.id.myGrid);
        mGridView.setEmptyView(findViewById(R.id.myText));
        okButton = (TextView) findViewById(R.id.ok_button);
        focus_ok_button = (FrameLayout) findViewById(R.id.focus_ok_button);
        okButton.setText("(" + getSeleteImageCount() + "/" + photo_num + ")" + "完成");
        rvFolder = (RecyclerView) findViewById(R.id.rv_folder);
        masking = findViewById(R.id.masking);
//        setRightButtonShow("取消", new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        setBackButtonShow("返回", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                if (isInitFolder) {
//                    if (isOpenFolder) {
//                        closeFolder();
//                    } else {
//                        openFolder();
//                    }
//                }
            }
        });
    }

    private void getImageList(GridView mGridView) {
        Intent data = getIntent();
        mImageselectList = data.getParcelableArrayListExtra(KEY_PREVIEW_PHOTO);
        photo_num = data.getIntExtra("photo_num", 0);
//        if (mImageselectList != null && !mImageselectList.isEmpty()) {
//            preview.setVisibility(View.VISIBLE);
//        } else {
//            preview.setVisibility(View.GONE);
//        }

        AlbumHelper helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());

        contentList = helper.getImagesBucketList(false);
        //清空相册数据，重新加载
        dataList.clear();
        for (int i = 0; i < contentList.size(); i++) {
            dataList.addAll(contentList.get(i).imageList);
        }
        gridImageAdapter = new AlbumGridViewAdapter(this, dataList, mImageselectList);
        mGridView.setAdapter(gridImageAdapter);
    }

    /**
     * 从SDCard加载图片。
     */
    private void loadImageForSDCard() {
        ImageModel.loadImageForSDCard(AlbumActivity.this, new ImageModel.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Folder> folders) {
                mFolders = folders;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mFolders != null && !mFolders.isEmpty()) {
                            initFolderList();
                            setFolder(mFolders.get(0));
//                            if (mSelectedImages != null && mAdapter != null) {
//                                mAdapter.setSelectedImages(mSelectedImages);
//                                mSelectedImages = null;
//                            }
                        }
                    }
                });
            }
        });
    }

    private Folder mFolder;

    private ArrayList<Folder> mFolders;
    private RecyclerView rvFolder;
    private View masking;
    private boolean isInitFolder;
    private boolean isOpenFolder;

    /**
     * 初始化图片文件夹列表
     */
    private void initFolderList() {
        if (mFolders != null && !mFolders.isEmpty()) {
            isInitFolder = true;
            rvFolder.setLayoutManager(new LinearLayoutManager(this));
            FolderAdapter adapter = new FolderAdapter(this, mFolders);
            adapter.setOnFolderSelectListener(new FolderAdapter.OnFolderSelectListener() {
                @Override
                public void OnFolderSelect(Folder folder) {
                    setFolder(folder);
                    closeFolder();
                }
            });
            rvFolder.setAdapter(adapter);
        }
    }

    /**
     * 刚开始的时候文件夹列表默认是隐藏的
     */
    private void hideFolderList() {
        rvFolder.post(new Runnable() {
            @Override
            public void run() {
                rvFolder.setTranslationY(rvFolder.getHeight());
                rvFolder.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 设置选中的文件夹，同时刷新图片列表
     *
     * @param folder
     */
    private void setFolder(Folder folder) {
        if (folder != null && gridImageAdapter != null && !folder.equals(mFolder)) {
            mFolder = folder;
            setTitle(folder.getName());
            gridImageAdapter.refresh(folder.getImages());
        }
    }

    /**
     * 弹出文件夹列表
     */
    private void openFolder() {
        if (!isOpenFolder) {
            masking.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(rvFolder, "translationY",
                    rvFolder.getHeight(), 0).setDuration(300);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    rvFolder.setVisibility(View.VISIBLE);
                }
            });
            animator.start();
            isOpenFolder = true;
        }
    }

    /**
     * 收起文件夹列表
     */
    private void closeFolder() {
        if (isOpenFolder) {
            masking.setVisibility(View.GONE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(rvFolder, "translationY",
                    0, rvFolder.getHeight()).setDuration(300);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    rvFolder.setVisibility(View.GONE);
                }
            });
            animator.start();
            isOpenFolder = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PREVIEW: {
                if (resultCode == RESULT_OK) {
                    ArrayList<ImageItem> imageItems = null;
                    if (data != null) {
                        imageItems = data.getParcelableArrayListExtra(KEY_PREVIEW_PHOTO);
                    }
                    if (imageItems != null && mImageselectList.size() != imageItems.size()) {
                        Iterator itr = mImageselectList.iterator();
                        boolean isEqual = false;
                        while (itr.hasNext()) {
                            isEqual = false;
                            ImageItem item = (ImageItem) itr.next();
                            for (ImageItem temp : imageItems) {
                                if (item.getImageId().equals(temp.getImageId())) {
                                    isEqual = true;
                                    break;
                                }
                            }
                            if (!isEqual) {
                                itr.remove();
                            }
                        }
                        gridImageAdapter.notifyDataSetChanged();
                        mImageselectList = imageItems;
                    }
                }
            }
            break;
            case Params.PHOTO_REQUEST_GALLERY:
                // 当选择从本地获取图片时
                if (resultCode == Activity.RESULT_OK) {
                    String imgPath;
                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        //安卓7.0以上，4才是路径，但低版本安卓，1是路径。
                        imgPath = cursor.getString(4);
                        //如果不是路径，就再找找。
                        if (!imgPath.contains("/storage/")) {
                            //找个6次应该就差不多了，7、8.。。。后面一般都是null
                            for (int i = 0; i < 7; i++) {
                                if (cursor.getString(i).contains("/storage/")) {
                                    imgPath = cursor.getString(i);
                                    break;
                                }
                            }
                        }
                        cursor.close();
                    } else {
                        imgPath = data.getDataString().replace("file://", "");
                    }
//                    String s = DateUtil.format(new Date(), "yyyyMMddHHmmss") + ".jpg";
//                    Files.FileCache.copyFile(imgPath, Files.getPhotoPath() + s);
                }
                break;
            default:
                break;
        }
    }

    private int getSeleteImageCount() {
        int imageCount = (mImageselectList == null) ? 0 : mImageselectList.size();
        return imageCount;
    }

    private void initListener() {
        gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(final ToggleButton toggleButton,
                                    int position, boolean isChecked, Button chooseBt) {
                if (isChecked) {
                    if (getSeleteImageCount() >= photo_num) {
                        toggleButton.setChecked(false);
                        chooseBt.setVisibility(View.GONE);
                        if (!removeOneData(dataList.get(position))) {
                            showNotifyMessage("最多可选" + photo_num + "张图片");
                        }
                        return;
                    }
                    chooseBt.setVisibility(View.VISIBLE);
                    if (mImageselectList == null) {
                        mImageselectList = new ArrayList<>();
                    }
                    mImageselectList.add(dataList.get(position));
                    okButton.setText("(" + getSeleteImageCount() + "/" + photo_num + ")" + getString(R.string.finish));
                } else {
                    removeItemFromList(dataList.get(position));
                    chooseBt.setVisibility(View.GONE);
                    okButton.setText("(" + getSeleteImageCount() + "/" + photo_num + ")" + getString(R.string.finish));
                }
//                if (getSeleteImageCount() > 0) {
//                    preview.setVisibility(View.VISIBLE);
//                } else {
//                    preview.setVisibility(View.GONE);
//                }
                isShowOkBt();
            }
        });
        focus_ok_button.setOnClickListener(new AlbumSendListener());
    }

    private void removeItemFromList(ImageItem imageItem) {
        Iterator itr = mImageselectList.iterator();
        while (itr.hasNext()) {
            ImageItem item = (ImageItem) itr.next();
            if (item != null && !TextUtils.isEmpty(item.getImageId())
                    && item.getImageId().equals(imageItem.getImageId())) {
                itr.remove();
            }
        }
    }

    private boolean removeOneData(ImageItem imageItem) {
        if (mImageselectList.contains(imageItem)) {
            mImageselectList.remove(imageItem);
            okButton.setText("(" + getSeleteImageCount() + "/" + photo_num + ")" + getString(R.string.finish));
            return true;
        }
        return false;
    }

    public void isShowOkBt() {
        if (getSeleteImageCount() > 0) {
            okButton.setText("(" + getSeleteImageCount() + "/"
                    + photo_num + ")" + getString(R.string.finish));
//            focus_preview.setPressed(true);
//            focus_ok_button.setPressed(true);
            focus_preview.setClickable(true);
            focus_ok_button.setClickable(true);
        } else {
            okButton.setText("(" + getSeleteImageCount() + "/"
                    + photo_num + ")" + getString(R.string.finish));
//            focus_preview.setPressed(false);
            focus_preview.setClickable(false);
//            focus_ok_button.setPressed(false);
            focus_ok_button.setClickable(true);
        }
    }

    @Override
    protected void onRestart() {
        isShowOkBt();
        super.onRestart();
    }
}
