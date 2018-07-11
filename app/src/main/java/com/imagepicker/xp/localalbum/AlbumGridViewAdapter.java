package com.imagepicker.xp.localalbum;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.imagepicker.xp.R;
import com.imagepicker.xp.bean.ImageItem;
import com.imagepicker.xp.utils.ImageLoader;

import java.util.ArrayList;

/**
 * @Description 这个是显示一个文件夹里面的所有图片时用的适配器
 */
public class AlbumGridViewAdapter extends BaseAdapter {
    final String TAG = getClass().getSimpleName();
    private Context mContext;
    private ArrayList<ImageItem> dataList;
    private ArrayList<ImageItem> selectedDataList;
    //private DisplayMetrics dm;
    private ImageLoader mImageLoader = ImageLoader.getInstance();

    public AlbumGridViewAdapter(Context c, ArrayList<ImageItem> dataList,
                                ArrayList<ImageItem> selectedDataList) {
        mContext = c;
        this.dataList = dataList;
        this.selectedDataList = selectedDataList;
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
    }

    public int getCount() {
        return dataList.size();
    }

    public Object getItem(int position) {
        return dataList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

//    ImageCallback callback = new ImageCallback() {
//        @Override
//        public void imageLoad(ImageView imageView, Bitmap bitmap,
//                              Object... params) {
//            if (imageView != null && bitmap != null) {
//                String url = (String) params[0];
//                if (url != null && url.equals((String) imageView.getTag())) {
//                    ((ImageView) imageView).setImageBitmap(bitmap);
//                } else {
//                    Log.e(TAG, "callback, bmp not match");
//                }
//            } else {
//                Log.e(TAG, "callback, bmp null");
//            }
//        }
//    };

    /**
     * 存放列表项控件句柄
     */
    private class ViewHolder {
        public ImageView imageView;
        public ToggleButton toggleButton;
        public Button choosetoggle;
        public TextView textView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.plugin_camera_select_imageview, parent, false);
            viewHolder.imageView = (ImageView) convertView
                    .findViewById(R.id.image_view);
            viewHolder.toggleButton = (ToggleButton) convertView
                    .findViewById(R.id.toggle_button);
            viewHolder.choosetoggle = (Button) convertView
                    .findViewById(R.id.choosedbt);
//			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,dipToPx(65)); 
//			lp.setMargins(50, 0, 50,0); 
//			viewHolder.imageView.setLayoutParams(lp);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String path;
        if (dataList != null && dataList.size() > position)
            path = dataList.get(position).imagePath;
        else
            path = "camera_default";
        if (path.contains("camera_default")) {
            viewHolder.imageView.setImageResource(R.mipmap.plugin_camera_no_pictures);
        } else {
            final ImageItem item = dataList.get(position);
            //viewHolder.imageView.setTag(item.imagePath);
//            cache.displayBmp(viewHolder.imageView, item.thumbnailPath, item.imagePath,
//                    callback);

            //mImageLoader.display(viewHolder.imageView, item.getUri());
            mImageLoader.display(viewHolder.imageView, item.getUri(), 0.8f);
        }
        viewHolder.toggleButton.setTag(position);
        viewHolder.choosetoggle.setTag(position);
        viewHolder.toggleButton.setOnClickListener(new ToggleClickListener(viewHolder.choosetoggle));
        if (isHadSelected(position, dataList.get(position))) {
            viewHolder.toggleButton.setChecked(true);
            viewHolder.choosetoggle.setVisibility(View.VISIBLE);
        } else {
            viewHolder.toggleButton.setChecked(false);
            viewHolder.choosetoggle.setVisibility(View.GONE);
        }
        return convertView;
    }

    private boolean isHadSelected(int position, ImageItem imageItem) {
        if (imageItem != null && selectedDataList != null) {
            for (ImageItem item : selectedDataList) {
                if (item.getImageId() != null && item.getImageId().equals(imageItem.getImageId())) {
                    return true;
                }
            }
        }
        return false;
    }


    private class ToggleClickListener implements OnClickListener {
        Button chooseBt;

        public ToggleClickListener(Button choosebt) {
            this.chooseBt = choosebt;
        }

        @Override
        public void onClick(View view) {
            if (view instanceof ToggleButton) {
                ToggleButton toggleButton = (ToggleButton) view;
                int position = (Integer) toggleButton.getTag();
                if (dataList != null && mOnItemClickListener != null
                        && position < dataList.size()) {
                    mOnItemClickListener.onItemClick(toggleButton, position, toggleButton.isChecked(), chooseBt);
                }
            }
        }
    }


    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    public interface OnItemClickListener {
         void onItemClick(ToggleButton view, int position,
                          boolean isChecked, Button chooseBt);
    }

}
