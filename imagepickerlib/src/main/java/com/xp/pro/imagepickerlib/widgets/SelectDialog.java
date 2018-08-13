package com.xp.pro.imagepickerlib.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.xp.pro.imagepickerlib.R;
import com.xp.pro.imagepickerlib.base.SuperBaseAdpter;
import com.xp.pro.imagepickerlib.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

public class SelectDialog extends SafeDialog {

    private Context context;
    private SelectDialogAdapter mDialogAdapter;
    private List<String> mListDatas;
    private List<View.OnClickListener> mListeners;
    private boolean isMarked = false;
    private String mTitle;
    private TextView mTitletText;
    private LayoutInflater layoutInflater;
    private String markedItem;

    public SelectDialog(Context context) {
        this(context, R.style.base_alert_dialog);
    }

    public SelectDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        View rootView = layoutInflater.inflate(R.layout.layout_dialog_item_select, null);
        setContentView(rootView);
    }

    public SelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    public static SelectDialog createDialog(Context context, String title) {
        SelectDialog dialog = new SelectDialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitleContent(title);
        return dialog;
    }

    public void setTitleContent(String title) {
        mTitle = title;
    }

    public void setMarked(boolean isMarked, String markedItem) {
        this.isMarked = isMarked;
        this.markedItem = markedItem;
    }

    public void setListDatas(List<String> stringList) {
        mListDatas = stringList;
    }

    public void setItemDatas(String... strings) {
        if (mListDatas == null) {
            mListDatas = new ArrayList<>();
        }
        for (String item : strings) {
            mListDatas.add(item);
        }
    }

    public void setItemListener(View.OnClickListener... listeners) {
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        for (View.OnClickListener listener : listeners) {
            mListeners.add(listener);
        }
    }

    public int getListenerCount() {
        if (mListeners != null) {
            return mListeners.size();
        }
        return 0;
    }

    @Override
    public void show() {
        if (!isShowing()) {
            initData();
            // 设置显示属性
            Window window = getWindow();
            window.setGravity(Gravity.CENTER);
            window.setWindowAnimations(R.style.dialog_center_show_animation_style);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.width = DisplayUtil.getScreenWidth(context) / 5 * 4; //设置宽度
            getWindow().setAttributes(lp);

            super.show();
        }
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        mTitletText = (TextView) view.findViewById(R.id.id_layout_dialog_item_select_title);
        ListView mListview = (ListView) view.findViewById(R.id.id_layout_dialog_item_select_listview);
        mDialogAdapter = new SelectDialogAdapter(context);
        mListview.setAdapter(mDialogAdapter);
    }

    private void initData() {
        mTitletText.setText(mTitle);

        mDialogAdapter.setListenerList(mListeners);
        mDialogAdapter.setData(mListDatas);
    }

    private class SelectDialogAdapter extends SuperBaseAdpter<String> {

        private List<View.OnClickListener> listenerList = new ArrayList<>();

        public SelectDialogAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String content = getItem(position);
            if (TextUtils.isEmpty(content)) {
                return convertView;
            }

            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.layout_dialog_item_select_item, null);
                viewHolder.mTextView = (TextView) convertView.findViewById(R.id.id_layout_dialog_item_textview);
                viewHolder.mMakeView = (TextView) convertView.findViewById(R.id.id_layout_dialog_item_mark);
                viewHolder.mLineView = convertView.findViewById(R.id.id_layout_dialog_item_line);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (isMarked && content.equals(markedItem)) {
                viewHolder.mMakeView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mMakeView.setVisibility(View.GONE);
            }

            if (position < getCount() - 1) {
                viewHolder.mLineView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mLineView.setVisibility(View.GONE);
            }

            viewHolder.mTextView.setText(content);
            if (position == mListDatas.size() - 1) {
                viewHolder.mTextView.setBackgroundResource(R.drawable.selector_confirm_dialog_one_btn);
            } else {
                viewHolder.mTextView.setBackgroundResource(R.drawable.selector_normal_press_bg);
            }
            viewHolder.mTextView.setOnClickListener(getListenerByPosition(position));

            return convertView;
        }

        public View.OnClickListener getListenerByPosition(int position) {
            if (position < listenerList.size()) {
                return listenerList.get(position);
            } else if (listenerList.size() == 1) {
                return listenerList.get(0);
            }
            return null;
        }

        public void setListenerList(List<View.OnClickListener> clickListeners) {
            listenerList = clickListeners;
        }

        private class ViewHolder {
            TextView mTextView;
            TextView mMakeView;
            View mLineView;
        }
    }

}
