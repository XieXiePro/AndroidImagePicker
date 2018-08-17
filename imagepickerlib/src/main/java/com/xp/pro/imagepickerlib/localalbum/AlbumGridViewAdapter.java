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

import java.util.ArrayList;

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
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        //将已选中的图片加入对应图片中
        for (int position = 0; position < dataList.size(); position++) {
            dataList.get(position).setSelected(isHadSelected(dataList.get(position)));
        }
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