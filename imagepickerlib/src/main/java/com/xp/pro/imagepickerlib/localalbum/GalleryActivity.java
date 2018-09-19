package com.xp.pro.imagepickerlib.localalbum;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xp.pro.imagepickerlib.R;
import com.xp.pro.imagepickerlib.base.BaseActivity;
import com.xp.pro.imagepickerlib.bean.ImageItem;
import com.xp.pro.imagepickerlib.utils.ImageLoader;
import com.xp.pro.imagepickerlib.widgets.PhotoView;
import com.xp.pro.imagepickerlib.widgets.ViewPagerFixed;

import java.util.ArrayList;

/**
 * @Description 这个是用于进行图片浏览时的界面
 * @note
 */
public class GalleryActivity extends BaseActivity {
    // 发送按钮
    private TextView send_bt;
    private FrameLayout focus_send;
    //    //删除按钮
//    private Button del_bt;
    //顶部显示预览图片位置的textview
    private TextView positionTextView;
    //获取前一个activity传过来的position
    private int position;
    //当前的位置
    private int location = 0;

    private ArrayList<View> listViews = null;
    private ViewPagerFixed pager;
    private MyPageAdapter adapter;
    private ArrayList<ImageItem> imageItemList;
    private String complete;
    private ImageLoader mImageLoader = ImageLoader.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_camera_gallery);// 切屏到主界面
        //mContext = this;
        initView();
        Intent intent = getIntent();
        position = Integer.parseInt(intent.getStringExtra("position"));
        imageItemList = intent.getParcelableArrayListExtra(AlbumActivity.KEY_PREVIEW_PHOTO);
        if (imageItemList == null) {
            return;
        }
        complete = "(" + imageItemList.size() + "/" + intent.getIntExtra("photo_num", 0) + ")" + getResources().getString(R.string.finish);
        send_bt = (TextView) findViewById(R.id.send_button);
        focus_send = (FrameLayout) findViewById(R.id.focus_send);
        focus_send.setOnClickListener(new GallerySendListener());
        isShowOkBt();
        // 为发送按钮设置文字
        pager = (ViewPagerFixed) findViewById(R.id.gallery01);
        pager.setOnPageChangeListener(pageChangeListener);
        for (int i = 0; i < imageItemList.size(); i++) {
            initListViews(imageItemList.get(i).getUri());
        }

        adapter = new MyPageAdapter(listViews);
        pager.setAdapter(adapter);
        int id = intent.getIntExtra("ID", 0);
        pager.setCurrentItem(id);

        setRightImageView(R.mipmap.icon_delete, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listViews.size() == 1) {
                    imageItemList.clear();
                    send_bt.setText(complete);
                    Intent data = new Intent();
                    data.putParcelableArrayListExtra(AlbumActivity.KEY_PREVIEW_PHOTO, imageItemList);
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    imageItemList.remove(location);
                    pager.removeAllViews();
                    listViews.remove(location);
                    adapter.setListViews(listViews);
                    send_bt.setText(complete);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        setBackButtonShow("返回",new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        setTitle("预览");
    }

    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        public void onPageSelected(int arg0) {
            location = arg0;
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void initListViews(Uri uri) {
        if (listViews == null)
            listViews = new ArrayList<View>();
        PhotoView img = new PhotoView(this);
        img.setBackgroundColor(0xff000000);
        mImageLoader.display(img, uri);
        img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        listViews.add(img);
    }

    // 完成按钮的监听
    private class GallerySendListener implements OnClickListener {
        public void onClick(View v) {
            Intent data = new Intent();
            data.putParcelableArrayListExtra(AlbumActivity.KEY_PREVIEW_PHOTO, imageItemList);
            setResult(RESULT_OK, data);
            GalleryActivity.this.finish();
        }
    }

    public void isShowOkBt() {
        if (null != imageItemList && !imageItemList.isEmpty()) {
            send_bt.setText(complete);
            focus_send.setClickable(true);
        } else {
            focus_send.setClickable(false);
        }
    }

    class MyPageAdapter extends PagerAdapter {

        private ArrayList<View> listViews;

        private int size;

        public MyPageAdapter(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public void setListViews(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public int getCount() {
            return size;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
        }

        public void finishUpdate(View arg0) {
        }

        public Object instantiateItem(View arg0, int arg1) {
            try {
                ((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

            } catch (Exception e) {
            }
            return listViews.get(arg1 % size);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }
}
