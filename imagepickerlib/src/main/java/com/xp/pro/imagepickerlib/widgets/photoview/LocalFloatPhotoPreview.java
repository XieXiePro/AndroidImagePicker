package com.xp.pro.imagepickerlib.widgets.photoview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.xp.pro.imagepickerlib.R;
import com.xp.pro.imagepickerlib.base.BaseActivity;
import com.xp.pro.imagepickerlib.base.BaseFragment;
import com.xp.pro.imagepickerlib.bean.ImageItem;
import com.xp.pro.imagepickerlib.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 大图浮层预览
 */
public class LocalFloatPhotoPreview extends BaseFragment {
    private final static String TAG = LocalFloatPhotoPreview.class.getSimpleName();

    private LayoutInflater mInflater;
    private ImgViewPager mViewPager;

    private ArrayList<ImageItem> mAllImages;
    private int mCurIndex;

    private ImageLoader mImageLoader = ImageLoader.getInstance();

    public LocalFloatPhotoPreview() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);
        initUI(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected int getLayoutId() {
        return R.layout.fragment_local_photo_preview;
    }

    private void initUI(View rootView) {
        mInflater = getActivity().getLayoutInflater();

        mViewPager = (ImgViewPager) rootView.findViewById(R.id.id_photo_preview_viewpager);
        mViewPager.setPageMargin(48);
        ImageAdapter imageAdapter = new ImageAdapter();
        mViewPager.setAdapter(imageAdapter);
        mViewPager.setCurrentItem(mCurIndex);
    }

    public void setAllImages(List<ImageItem> images) {
        if (null != images) {
            mAllImages = new ArrayList<>(images);
        }
    }

    public void setCurIndex(int index) {
        mCurIndex = index;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            View focusView = getActivity().getCurrentFocus();
            if (focusView != null) {
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void onKeyDown() {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) getActivity();
            activity.onBackPressed();
        }
    }

    class ImageAdapter extends ImgViewPager.PagerAdapter implements MultiTransformImgPositionController.OnGestureListener, MultiTransformImgPositionController.OnDoubleTapListener {

        class RycycleBin {
            private static final int DEFAULT_CAPACITY = 5;
            private ArrayList<View> mScrapViews = new ArrayList<>();

            public void addScrapView(View view) {
                MultiTransformImageView imageView = (MultiTransformImageView) view.findViewById(R.id.imageviewphoto);
                imageView.setImageDrawable(null);
                if (mScrapViews.size() >= DEFAULT_CAPACITY) {
                    return;
                }
                view.layout(0, 0, 0, 0);
                mScrapViews.add(view);
            }

            /**
             * @return
             */
            public View getScrapView() {
                int size = mScrapViews.size();
                if (size > 0) {
                    return mScrapViews.remove(0);
                } else {
                    return null;
                }
            }

            public void clearScrapViews() {
                mScrapViews.clear();
            }

            public int getSize() {
                return mScrapViews.size();
            }
        }

        private RycycleBin mRycycleBin = new RycycleBin();
        private int mScrollState = ImgViewPager.SCROLL_STATE_IDLE;

        private SparseArray<MultiTransformImageView> viewList = new SparseArray<>();

        public ImageAdapter() {

        }

        public void clearScrapViews() {
            mRycycleBin.clearScrapViews();
        }

        public int getScrollState() {
            return mScrollState;
        }


        @Override
        public int getCount() {
            return mAllImages == null ? 0 : mAllImages.size();
        }

        @Override
        public void startUpdate(View container) {

        }

        @Override
        public Object instantiateItem(View container, int position) {
            View photoView = mRycycleBin.getScrapView();
            if (photoView == null) {
                photoView = mInflater.inflate(R.layout.mlsz_item_local_photoviewer, null);
            }
            photoView.setTag(position);
            MultiTransformImageView imageView = (MultiTransformImageView) photoView.findViewById(R.id.imageviewphoto);
            final ProgressBar progressBar = (ProgressBar) photoView.findViewById(R.id.image_loading);

            viewList.put(position, imageView);
            imageView.setViewPager(mViewPager);
            ((ViewGroup) container).addView(photoView);
            imageView.setOnGestureListener(this);
            imageView.setOnDoubleTapListener(this);
            imageView.setIsLongpressEnabled(true);
            imageView.setTransformEnabled(true);

            ImageItem imageItem = getItem(position);
            if (imageItem != null) {
                mImageLoader.display(imageItem.getImagePath(), new GlideDrawableImageViewTarget(imageView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        progressBar.setVisibility(View.GONE);
                        super.onResourceReady(resource, animation);
                    }

                    @Override
                    public void onStop() {
                        super.onStop();
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        progressBar.setVisibility(View.VISIBLE);
                        super.onLoadStarted(placeholder);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                    }
                });
//                imageView.setImageBitmap(imageItem.getBitmap());
            } else {
                progressBar.setVisibility(View.GONE);
            }
            return photoView;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewGroup) container).removeView((View) object);
            mRycycleBin.addScrapView((View) object);
        }

        @Override
        public void finishUpdate(View container) {

        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public MultiTransformImageView getPosByPosition(int position) {
            return viewList.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {

        }

        public ImageItem getItem(int position) {
            if (mAllImages != null && mAllImages.size() > position) {
                return mAllImages.get(position);
            } else {
                return null;
            }
        }


        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onLongPress(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            onKeyDown();
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

    }
}
