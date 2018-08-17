package com.xp.pro.imagepickerlib.widgets;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xp.pro.imagepickerlib.R;
import com.xp.pro.imagepickerlib.utils.DisplayUtil;

public class TipDialog extends SafeDialog {

    private Context context;
    private String mTitle;
    private String mContent;
    private LayoutInflater layoutInflater;
    private TextView tipDialogTitleTv;
    private TextView tipDialogContent;

    public TipDialog(Context context) {
        this(context, R.style.base_alert_dialog);
    }

    public TipDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        View rootView = layoutInflater.inflate(R.layout.layout_dialog_item_tip, null);
        setContentView(rootView);
    }

    public TipDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    public static TipDialog createDialog(Context context, String title) {
        TipDialog dialog = new TipDialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle(title);
        return dialog;
    }

    public static TipDialog createDialog(Context context, String title, String content) {
        TipDialog dialog = new TipDialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle(title);
        dialog.setContent(content);
        return dialog;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setContent(String content) {
        mContent = content;
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

    private void initData() {
        tipDialogTitleTv.setText(mTitle);
        tipDialogContent.setText(mContent);
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
        tipDialogTitleTv = (TextView) findViewById(R.id.tip_dialog_title_tv);
        tipDialogContent = (TextView) findViewById(R.id.tip_dialog_content);
        TextView tipDialogCancel = (TextView) findViewById(R.id.tip_dialog_cancel);
        tipDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
