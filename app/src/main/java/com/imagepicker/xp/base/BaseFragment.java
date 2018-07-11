package com.imagepicker.xp.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.bumptech.glide.Glide;


public class BaseFragment extends Fragment {

    private boolean isAttached = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        isAttached = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.with(this).onLowMemory();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isAttached()) {
            if (isVisibleToUser) {
                Glide.with(this).onStart();
            } else {
                Glide.with(this).onStop();
            }
        }
    }

    protected void showNotifyMessage(CharSequence msg) {
        if (getUserVisibleHint()) {
            Activity activity = getActivity();
            if (activity == null || activity.isFinishing()) {
                return;
            }
//            ToastUtils.show(activity, msg);
        }
    }

    public final boolean isAlive() {
        Activity activity = getActivity();
        return activity != null && !activity.isFinishing() && !isRemoving() && !isDetached() && isAdded();
    }

    public final boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

//    @Override
//    public void onBusinessResult(final ResultData result) {
//        if (isAlive()) {
//            if (isMainThread()) {
//                onBusinessResultAtMainThread(result);
//            } else {
//                ThreadUtils.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (isAlive()) {
//                            // double check.
//                            onBusinessResultAtMainThread(result);
//                        }
//                    }
//                });
//            }
//        }
//    }


    @Override
    public void startActivity(Intent intent) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).startActivity(intent);
        } else {
            super.startActivity(intent);
        }
    }

    protected void showWaitingDialog(String msg) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
//            ((BaseActivity) getActivity()).showWaitDialog(msg);
        }
    }

    protected void hideWaitingDialog() {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
//            ((BaseActivity) getActivity()).hideWaitDialog();
        }
    }

//    protected static final String EMPTY_TEXT_TITLE = GroupContext.getApplication().getResources().getString(R.string.app_empty_text_title);
//    protected static final String EMPTY_TEXT_INFO = GroupContext.getApplication().getResources().getString(R.string.app_empty_text_info);


    /**
     * 设置HPullToRefreshListView空数据显示页面
     * @param refreshView
     */
//    protected void setNoDataEmptyView(HPullToRefreshListView refreshView) {
//        setNoDataEmptyView(refreshView, R.drawable.icon_no_data_yoyo, EMPTY_TEXT_TITLE, EMPTY_TEXT_INFO, null, null);
//    }

//    protected void setNoDataEmptyView(HPullToRefreshListView refreshView, String title, String btnText, View.OnClickListener btnListener) {
//        setNoDataEmptyView(refreshView, R.drawable.icon_no_data_yoyo, title, EMPTY_TEXT_INFO, btnText, btnListener);
//    }

//    /**
//     * 设置HPullToRefreshListView空数据显示页面
//     * @param refreshView
//     * @param resourceID 空数据 默认图片
//     * @param msg
//     * @param subMsg
//     * @param btnMsg    按钮显示文字
//     * @param listener  按钮事件
//     */
//    protected void setNoDataEmptyView(final HPullToRefreshListView refreshView, int resourceID,
//                                      String msg, String subMsg, String btnMsg, View.OnClickListener listener) {
//        if (refreshView != null) {
//            refreshView.setNoDataEmptyViewEnabled(true);
//
//            NoDataEmptyView noDataEmptyView = refreshView.getNoDataEmptyView();
//            if (noDataEmptyView == null) {
//                return;
//            }
//
//            if (resourceID != -1) {
//                noDataEmptyView.setNoDataEmptyBackground(resourceID);
//            }
//            if (!TextUtils.isEmpty(subMsg) && subMsg.equals(EMPTY_TEXT_INFO)) {
//                noDataEmptyView.setNoDataIconClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        refreshView.setRefreshing();
//                    }
//                });
//            }
//
//            noDataEmptyView.setMessage(msg, subMsg);
//
//            noDataEmptyView.setBtnOnClickListener(btnMsg, listener);
//            refreshView.setRefreshComplete(true);
//        }
//    }

    /**
     * 设置HPullToRefreshGridView空数据显示页面
     * @param refreshView
     */
//    protected void setNoDataEmptyView(HPullToRefreshGridView refreshView) {
//        setNoDataEmptyView(refreshView, R.drawable.icon_no_data_yoyo, EMPTY_TEXT_TITLE, EMPTY_TEXT_INFO, null, null);
//    }
//
//    protected void setNoDataEmptyView(HPullToRefreshGridView refreshView, String title, String btnText, View.OnClickListener btnListener) {
//        setNoDataEmptyView(refreshView, R.drawable.icon_no_data_yoyo, title, EMPTY_TEXT_INFO, btnText, btnListener);
//    }

    /**
     * 设置HPullToRefreshGridView空数据显示页面
     * @param refreshView
     * @param resourceID 空数据 默认图片
     * @param msg
     * @param subMsg
     * @param btnMsg    按钮显示文字
     * @param listener  按钮事件
     */
//    protected void setNoDataEmptyView(final HPullToRefreshGridView refreshView, int resourceID,
//                                      String msg, String subMsg, String btnMsg, View.OnClickListener listener) {
//        if (refreshView != null) {
//            refreshView.setNoDataEmptyViewEnabled(true);
//
//            NoDataEmptyView noDataEmptyView = refreshView.getNoDataEmptyView();
//            if (noDataEmptyView == null) {
//                return;
//            }
//
//            if (resourceID != -1) {
//                noDataEmptyView.setNoDataEmptyBackground(resourceID);
//            }
//            if (!TextUtils.isEmpty(subMsg) && subMsg.equals(EMPTY_TEXT_INFO)) {
//                noDataEmptyView.setNoDataIconClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        refreshView.setRefreshing();
//                    }
//                });
//            }
//
//            noDataEmptyView.setMessage(msg, subMsg);
//
//            noDataEmptyView.setBtnOnClickListener(btnMsg, listener);
//            refreshView.setRefreshComplete(true);
//        }
//    }

    /**
     * 设置控间的大小比例 widthScale : heightScale
     * @param view
     * @param widthScale
     * @param heightScale
     */
    protected void setScaleView(final View view, final int widthScale, final int heightScale) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                int width = view.getWidth();
                layoutParams.width = width;
                layoutParams.height = width * heightScale / widthScale;
                view.setLayoutParams(layoutParams);
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    public boolean isAttached() {
        return isAttached;
    }

}
