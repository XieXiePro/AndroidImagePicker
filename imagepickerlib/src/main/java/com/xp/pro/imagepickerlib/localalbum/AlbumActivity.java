package com.xp.pro.imagepickerlib.localalbum;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import com.xp.pro.imagepickerlib.bean.ImageBucket;
import com.xp.pro.imagepickerlib.bean.ImageItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @Description 这个是进入相册显示所有图片的界面
 */
public class AlbumActivity extends BaseActivity {
    // 显示手机里的所有图片的列表控件
    //private GridView mGridView;
    // 当手机里没有图片时，提示用户没有图片的控件
    //private TextView tv;
    // gridView的adapter
    private AlbumGridViewAdapter gridImageAdapter;
    // 完成按钮
    private TextView okButton;
    // 预览按钮
    private TextView preview;
    private FrameLayout focus_ok_button, focus_preview;
    //private Context mContext;
    private ArrayList<ImageItem> dataList;
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
        init();
        initListener();
        // 这个函数主要用来控制预览和完成按钮的状态
        isShowOkBt();
    }

    // 预览按钮的监听
    private class PreviewListener implements OnClickListener {
        public void onClick(View v) {
            if (mImageselectList.size() > 0) {
                Intent intent = new Intent(AlbumActivity.this, GalleryActivity.class);
                intent.putExtra("position", "1");
                intent.putExtra("photo_num", photo_num);
                intent.putParcelableArrayListExtra(KEY_PREVIEW_PHOTO, mImageselectList);
                startActivityForResultByAnimation(intent, REQUEST_CODE_PREVIEW);
            }
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
    private void init() {
        setTitle("选择图片");
        Intent data = getIntent();
        mImageselectList = data.getParcelableArrayListExtra(KEY_PREVIEW_PHOTO);
        photo_num = data.getIntExtra("photo_num", 0);
        AlbumHelper helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());

        contentList = helper.getImagesBucketList(false);
        dataList = new ArrayList<>();
        for (int i = 0; i < contentList.size(); i++) {
            dataList.addAll(contentList.get(i).imageList);
        }

        preview = (TextView) findViewById(R.id.preview);
        if (mImageselectList != null && !mImageselectList.isEmpty()) {
            preview.setVisibility(View.VISIBLE);
        } else {
            preview.setVisibility(View.GONE);
        }

        focus_preview = (FrameLayout) findViewById(R.id.focus_preview);
        focus_preview.setOnClickListener(new PreviewListener());
        GridView mGridView = (GridView) findViewById(R.id.myGrid);
        gridImageAdapter = new AlbumGridViewAdapter(this, dataList, mImageselectList);
        mGridView.setAdapter(gridImageAdapter);
        mGridView.setEmptyView(findViewById(R.id.myText));
        okButton = (TextView) findViewById(R.id.ok_button);
        focus_ok_button = (FrameLayout) findViewById(R.id.focus_ok_button);
        okButton.setText("(" + getSeleteImageCount() + "/" + photo_num + ")" + "完成");

        setRightButtonShow("取消", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setBackButtonShow(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private int getSeleteImageCount() {
        int imageCount = (mImageselectList == null) ? 0 : mImageselectList.size();
        return imageCount;
    }

    private void initListener() {

        gridImageAdapter
                .setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(final ToggleButton toggleButton,
                                            int position, boolean isChecked, Button chooseBt) {
                        if (isChecked) {
                            if (getSeleteImageCount() >= photo_num) {
                                toggleButton.setChecked(false);
                                chooseBt.setVisibility(View.GONE);
                                if (!removeOneData(dataList.get(position))) {
                                    showNotifyMessage("超出可选图片张数");
                                }
                                return;
                            }
                            chooseBt.setVisibility(View.VISIBLE);
                            if (mImageselectList == null) {
                                mImageselectList = new ArrayList<ImageItem>();
                            }
                            mImageselectList.add(dataList.get(position));
                            okButton.setText("(" + getSeleteImageCount() + "/"
                                    + photo_num + ")" + getString(R.string.finish));
                        } else {
                            removeItemFromList(dataList.get(position));
                            chooseBt.setVisibility(View.GONE);
                            okButton.setText("(" + getSeleteImageCount() + "/"
                                    + photo_num + ")" + getString(R.string.finish));
                        }
                        if (getSeleteImageCount() > 0) {
                            preview.setVisibility(View.VISIBLE);
                        } else {
                            preview.setVisibility(View.GONE);
                        }
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
            okButton.setText("(" + getSeleteImageCount() + "/"
                    + photo_num + ")" + getString(R.string.finish));
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
