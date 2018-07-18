package com.xp.pro.imagepickerlib.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xp.pro.imagepickerlib.R;
import com.xp.pro.imagepickerlib.app.PickerApplication;
import com.xp.pro.imagepickerlib.bean.ImageItem;
import com.xp.pro.imagepickerlib.utils.ToastUtils;
import com.xp.pro.imagepickerlib.widgets.SelectDialog;
import com.xp.pro.imagepickerlib.widgets.photoview.LocalFloatPhotoPreview;

import java.util.List;

public class BaseActivity extends FragmentActivity {

    private SelectDialog mSelectDialog;

    public static final String BACK_ANIMATION_ENTER = "enter_animation";
    public static final String BACK_ANIMATION_EXIT = "exit_animation";

    public static final int ANIMATION_TYPE_DETAIL = 1;
    public static final int ANIMATION_TYPE_PICTURE_VIEWER = 2;
    public static final int ANIMATION_TYPE_HOMEPAGE = 3;

    private static final String TAG = BaseActivity.class.getSimpleName();

    /**
     * 当前Activity是否在最上层
     */
    private boolean isActivityOnTop = false;
    private LocalFloatPhotoPreview mLocalFloatPhotoPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isActivityOnTop = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityOnTop = true;
    }


    /**
     * 启动activity时带动画
     *
     * @param intent
     * @param animationType
     */
    public void startActivityByAnimation(Intent intent, int animationType) {
        startActivityForResultByAnimation(intent, -1, animationType);
    }

    public void startActivityForResultByAnimation(Intent intent, int requestCode) {
        startActivityForResultByAnimation(intent, requestCode, ANIMATION_TYPE_DETAIL);
    }

    public void startActivityForResultByAnimation(Intent intent, int requestCode, int animationType) {
        int forwardEnterAnim = -1;
        int forwardExitAnim = -1;
        int backEnterAnim = -1;
        int backExitAnim = -1;
        switch (animationType) {
            case ANIMATION_TYPE_DETAIL:
                forwardEnterAnim = R.anim.qz_comm_slide_in_from_right;
                forwardExitAnim = R.anim.qz_comm_stack_push;
                backEnterAnim = R.anim.qz_comm_stack_pop;
                backExitAnim = R.anim.qz_comm_slide_out_to_right;
                break;
            case ANIMATION_TYPE_PICTURE_VIEWER:
                forwardEnterAnim = R.anim.qz_comm_slide_in_from_right;
                forwardExitAnim = R.anim.qz_comm_slide_out_to_left;
                backEnterAnim = R.anim.qz_comm_slide_in_from_left;
                backExitAnim = R.anim.qz_comm_slide_out_to_right;
                break;
            case ANIMATION_TYPE_HOMEPAGE:
                forwardEnterAnim = R.anim.qz_comm_alpha_fade_in;
                forwardExitAnim = R.anim.qz_comm_alpha_fade_out;
                backEnterAnim = R.anim.qz_comm_alpha_fade_in;
                backExitAnim = R.anim.qz_comm_alpha_fade_out;
                break;
            default:
                break;
        }
        startActivityForResultByAnimation(intent, requestCode, forwardEnterAnim, forwardExitAnim, backEnterAnim, backExitAnim);
    }

    private void startActivityForResultByAnimation(Intent intent, int requestCode, int forwardEnterAnim, int forwardExitAnim, int backEnterAnim, int backExitAnim) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
            intent.putExtra(BACK_ANIMATION_ENTER, backEnterAnim);
            intent.putExtra(BACK_ANIMATION_EXIT, backExitAnim);
            Activity activity = this;
            while (activity.getParent() != null) {
                activity = activity.getParent();
            }
            activity.startActivityForResult(intent, requestCode);
            activity.overridePendingTransition(forwardEnterAnim, forwardExitAnim);
        } else {
            startActivityForResult(intent, requestCode);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        isActivityOnTop = false;
    }

    public boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


    protected void showSelectDialog(String title, View.OnClickListener itemListener, String... selectItems) {
        showSelectDialog(title, false, "", itemListener, selectItems);
    }

    protected void showSelectDialog(String title, boolean isMarked, String markedStr,
                                    View.OnClickListener listener, String... selectItems) {
        if (mSelectDialog == null) {
            mSelectDialog = SelectDialog.createDialog(this, title);
        }

        mSelectDialog.setTitleContent(title);
        mSelectDialog.setMarked(isMarked, markedStr);
        mSelectDialog.setItemDatas(selectItems);
        if (mSelectDialog.getListenerCount() <= 1) {
            mSelectDialog.setItemListener(listener);
        }
        mSelectDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mSelectDialog = null;
            }
        });
        if (!mSelectDialog.isShowing()) {
            mSelectDialog.show();
        }
    }


    protected void setSelectDialogListeners(View.OnClickListener... clickListeners) {
        if (mSelectDialog == null) {
            mSelectDialog = new SelectDialog(this);
        }
        mSelectDialog.setItemListener(clickListeners);
    }

    protected void hideSelectDialog() {
        try {
            if (mSelectDialog != null) {
                mSelectDialog.dismiss();
                mSelectDialog = null;
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }


    public void showNotifyMessage(final CharSequence msg) {
        if (!isActivityOnTop) {
            return;
        }
        if (isMainThread()) {
            ToastUtils.show(PickerApplication.getAppContext(), msg);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.show(PickerApplication.getAppContext(), msg);
                }
            });
        }
    }
    public void setTitle(CharSequence title) {
        TextView titleLayout = (TextView)findViewById(R.id.id_global_title_bar_title_textview);
        if (title != null) {
            titleLayout.setText(title);
        }
    }

    protected View setBackButtonShow(View.OnClickListener listener) {
        View leftLayout = findViewById(R.id.id_global_title_bar_lefe_layout);
        if (leftLayout != null) {
            leftLayout.setVisibility(View.VISIBLE);
            if (listener != null) {
                leftLayout.setOnClickListener(listener);
            }
        }
        return leftLayout;
    }

    protected void setRightButtonShow(String msg, View.OnClickListener listener) {
        TextView rightText = (TextView) findViewById(R.id.id_global_title_bar_right_textview);
        if (rightText != null) {
            rightText.setText(msg);
            findViewById(R.id.id_global_title_bar_right_textview_layout).setVisibility(View.VISIBLE);
            if (listener != null) {
                findViewById(R.id.id_global_title_bar_right_textview_layout).setOnClickListener(listener);
            }
        }
    }

    protected void setRightImageView(int resourdID, View.OnClickListener listener) {
        ImageView rightImage = (ImageView) findViewById(R.id.id_global_title_bar_right_imageview);
        if (rightImage != null) {
            rightImage.setImageResource(resourdID);
            findViewById(R.id.id_global_title_bar_right_imageview_layout).setVisibility(View.VISIBLE);
            if (listener != null) {
                findViewById(R.id.id_global_title_bar_right_imageview_layout).setOnClickListener(listener);
            }
        }
    }

    /**
     * 大图浮层展示
     *
     * @param index
     * @param images
     */
    public void toPhotoPreviewFragment(final int index, final List<ImageItem> images) {
        if (null == mLocalFloatPhotoPreview) {
            mLocalFloatPhotoPreview = new LocalFloatPhotoPreview();
            mLocalFloatPhotoPreview.setCurIndex(index);
            mLocalFloatPhotoPreview.setAllImages(images);
            final FragmentManager fragmentManager = getSupportFragmentManager();
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.qz_comm_scale_in, 0);
            transaction.add(android.R.id.content, mLocalFloatPhotoPreview);
            transaction.commitAllowingStateLoss();
        } else {
            removePhotoPreviewFragment();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isFinishing()) {
            if (!removePhotoPreviewFragment()) {
                super.onBackPressed();
            }
        }
    }

    /**
     * 移除大图浮层
     *
     * @return
     */
    public boolean removePhotoPreviewFragment() {
        if (null != mLocalFloatPhotoPreview) {
//            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            final FragmentManager fragmentManager = getSupportFragmentManager();
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(0, R.anim.qz_comm_scale_out);
            transaction.remove(mLocalFloatPhotoPreview);
            transaction.commitAllowingStateLoss();
            mLocalFloatPhotoPreview = null;
            return true;
        }
        return false;
    }


    /**
     * 设置带分隔符的文本
     *
     * @param msg
     * @param listener if listener is null,it will no set click event
     */
    protected void setRightSeperatorText(String msg, View.OnClickListener listener) {
        View rightLayout = findViewById(R.id.id_global_title_bar_right_layout);
        TextView rightText = (TextView) findViewById(R.id.id_global_title_bar_right_seperator_textview);
        if (rightLayout != null && rightText != null) {
            rightText.setText(msg);
            rightLayout.setVisibility(View.VISIBLE);
            if (listener != null) {
                rightLayout.setOnClickListener(listener);
            }
        }
    }

    /**
     * 设置带分隔符的文本 只设置文本不设置点击事件
     *
     * @param msg
     */
    protected void setRightSeperatorText(String msg) {
        View rightLayout = findViewById(R.id.id_global_title_bar_right_layout);
        TextView rightText = (TextView) findViewById(R.id.id_global_title_bar_right_seperator_textview);
        if (rightLayout != null && rightText != null) {
            rightText.setText(msg);
            rightLayout.setVisibility(View.VISIBLE);
        }
    }
}