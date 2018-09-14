package com.xp.pro.imagepickerlib.localalbum;

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
import android.widget.ToggleButton;

import com.xp.pro.imagepickerlib.R;
import com.xp.pro.imagepickerlib.bean.ImageItem;
import com.xp.pro.imagepickerlib.utils.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 这个是显示一个文件夹里面的所有图片时用的适配器
 */
public class AlbumGridViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ImageItem> dataList;
    private ArrayList<ImageItem> selectedDataList;
    private ImageLoader mImageLoader = ImageLoader.getInstance();

    AlbumGridViewAdapter(Context context, ArrayList<ImageItem> dataList, ArrayList<ImageItem> selectedDataList) {
        this.context = context;
        this.dataList = dataList;
        this.selectedDataList = selectedDataList;
        //每创建一次相册，加载一次图片

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        //将已选中的图片加入对应图片中
        for (int position = 0; position < dataList.size(); position++) {
            dataList.get(position).setSelected(isHadSelected(dataList.get(position)));
        }
        //相册图片按照文件创建时间排序
        Collections.sort(dataList, new Comparator<ImageItem>() {
            @Override
            public int compare(ImageItem o1, ImageItem o2) {
                long o1Time = new File(o1.getUri().getPath()).lastModified();
                long o2Time = new File(o2.getUri().getPath()).lastModified();
                if (o1Time < o2Time) {
                    return 1;
                } else if (o1Time == o2Time) {
                    return 0;
                } else {
                    return -1;
                }
//                return Long.compare(o1Time, o2Time);
            }
        });
    }

    private boolean isHadSelected(ImageItem imageItem) {
        if (imageItem != null && selectedDataList != null) {
            for (ImageItem item : selectedDataList) {
                if (item.getImageId() != null && item.getImageId().equals(imageItem.getImageId())) {
                    return true;
                }
            }
        }
        return false;
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

    /**
     * 存放列表项控件句柄
     */
    private class ViewHolder {
        ImageView imageView;
        ToggleButton toggleButton;
        Button choosetoggle;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        convertView = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.plugin_camera_select_imageview, parent, false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
            viewHolder.toggleButton = (ToggleButton) convertView.findViewById(R.id.toggle_button);
            viewHolder.choosetoggle = (Button) convertView.findViewById(R.id.choosedbt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String path;
        if (dataList != null && dataList.size() > position) {
            path = dataList.get(position).imagePath;
        } else {
            path = "camera_default";
        }
        if (path.contains("camera_default")) {
            viewHolder.imageView.setImageResource(R.mipmap.plugin_camera_no_pictures);
        } else {
            if (dataList != null) {
                final ImageItem item = dataList.get(position);
                mImageLoader.display(viewHolder.imageView, item.getUri(), 0.8f);
            }
        }
        viewHolder.toggleButton.setTag(position);
        viewHolder.choosetoggle.setTag(position);
        viewHolder.toggleButton.setOnClickListener(new ToggleClickListener(viewHolder.choosetoggle));
        if (dataList != null) {
            if (dataList.get(position).isSelected()) {
                viewHolder.toggleButton.setChecked(true);
                viewHolder.choosetoggle.setVisibility(View.VISIBLE);
            } else {
                viewHolder.toggleButton.setChecked(false);
                viewHolder.choosetoggle.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    private class ToggleClickListener implements OnClickListener {
        Button chooseBt;

        ToggleClickListener(Button choosebt) {
            this.chooseBt = choosebt;
        }

        @Override
        public void onClick(View view) {
            if (view instanceof ToggleButton) {
                ToggleButton toggleButton = (ToggleButton) view;
                int position = (Integer) toggleButton.getTag();
                if (dataList != null && mOnItemClickListener != null && position < dataList.size()) {
                    mOnItemClickListener.onItemClick(toggleButton, position, toggleButton.isChecked(), chooseBt);
                    dataList.get(position).setSelected(toggleButton.isChecked());
                }
            }
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(ToggleButton view, int position, boolean isChecked, Button chooseBt);
    }
}