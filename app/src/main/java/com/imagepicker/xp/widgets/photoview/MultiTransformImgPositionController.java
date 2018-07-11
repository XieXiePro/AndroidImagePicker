package com.imagepicker.xp.widgets.photoview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.imagepicker.xp.base.BaseHandler;


/**
 * @author littletli 2013/03/05
 *         配合MultiTransformImageView使用，对其touch事件进行处理
 */
public class MultiTransformImgPositionController {

    public static final String TAG = "PositionController";
    private static final boolean DEBUG = false;

    private Scroller mScroller;

    private int mTouchSlop;

    private int mTouchSlopSquare;
    private int mDoubleTapSlopSquare;

    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private VelocityTracker mVelocityTracker;

    // Touch Mode
    private static final int MODE_NONE = 0;
    private static final int MODE_DRAG = 1;
    private static final int MODE_ZOOM = 2;
    private static final int MODE_FAKEDRAG = 3;

    int mTouchMode = MODE_NONE;

    //
    private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
    private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
    private static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();


    // constants for Message.what used by GestureHandler below
    private static final int SHOW_PRESS = 1;
    private static final int LONG_PRESS = 2;
    private static final int TAP = 3;


    private PointF mPivot = new PointF();
    private float mBaseDist;
    private float mBaseScale;

    Runnable mFlingRunnable;


    private float mLastMotionX;
    private float mLastMotionY;

    private boolean mStillDown;
    private boolean mInLongPress;
    private boolean mAlwaysInTapRegion;
    private boolean mAlwaysInBiggerTapRegion;

    private boolean mIsDoubleTapping;

    private int mBiggerTouchSlopSquare = 20 * 20;

    private MotionEvent mCurrentDownEvent;
    private MotionEvent mPreviousUpEvent;

    //
    boolean mIsDoubleTapZoomEnabled = true;
    //
    boolean mIsLongpressEnabled;

    private OnGestureListener mGestureListener;
    private OnDoubleTapListener mDoubleTapListener;

    private OnImageFlingListener mImageFlingListener;

    GestureHandler mHandler;

    public interface OnGestureListener {
        boolean onDown(MotionEvent e);

        boolean onUp(MotionEvent e);

        boolean onSingleTapUp(MotionEvent e);

        boolean onLongPress(MotionEvent e);
    }


    // 双击监听，默认为缩放处理
    public interface OnDoubleTapListener {
        boolean onSingleTapConfirmed(MotionEvent e);

        boolean onDoubleTap(MotionEvent e);

        boolean onDoubleTapEvent(MotionEvent e);
    }


    // 长图滑动
    public interface OnImageFlingListener {
        void onSingleImageFlingBegin();

        void onSingleImageFlingEnd();
    }

    private class GestureHandler extends BaseHandler {
        GestureHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LONG_PRESS:
                    dispatchLongPress();
                    break;

                case TAP:
                    // If the user's finger is still down, do not count it as a tap
                    if (mDoubleTapListener != null && !mStillDown) {
                        mDoubleTapListener.onSingleTapConfirmed(mCurrentDownEvent);
                    }
                    break;

                default:
                    throw new RuntimeException("Unknown message " + msg); //never
            }
        }
    }

    public MultiTransformImgPositionController(Context context) {
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mScroller = new Scroller(context);
        mHandler = new GestureHandler();

        int doubleTapSlop = configuration.getScaledDoubleTapSlop();

        mTouchSlopSquare = mTouchSlop * mTouchSlop;
        mDoubleTapSlopSquare = doubleTapSlop * doubleTapSlop;
    }


    /**
     *
     */
    public void setOnGestureListener(OnGestureListener onGestureListener) {
        mGestureListener = onGestureListener;

    }


    public void setOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener) {
        mDoubleTapListener = onDoubleTapListener;
    }


    public void setOnImageFlingListener(OnImageFlingListener onImageFlingListener) {
        mImageFlingListener = onImageFlingListener;
    }

    public void setIsLongpressEnabled(boolean isLongpressEnabled) {
        mIsLongpressEnabled = isLongpressEnabled;
    }

    /**
     * @return true if longpress is enabled, else false.
     */
    public boolean isLongpressEnabled() {
        return mIsLongpressEnabled;
    }

    public void setOnDoubleTapZoomEnabled(boolean enabled) {
        mIsDoubleTapZoomEnabled = enabled;
    }

    /**
     * @param event
     * @return
     */
    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }


    private void midPoint(PointF p, MotionEvent event) {
        if (event.getPointerCount() == 1) {
            p.set(event.getX(), event.getY());
        } else {
            p.set((event.getX(1) + event.getX(0)) / 2, (event.getY(1) + event
                    .getY(0)) / 2);
        }
    }


    public void fling(MultiTransformImageView view, int velocityX, int velocityY) {
        if ((Math.abs(velocityY) < mMinimumVelocity)
                && (Math.abs(velocityX) > mMinimumVelocity)) {
            return;
        }
        RectF rect = view.getTransformRect();
        if (rect == null) {
            return;
        }
        int minX = 0;
        int minY = 0;
        int maxX = (int) Math.max(0.0f, rect.width() - view.getWidth());
        int maxY = (int) Math.max(0.0f, rect.height() - view.getHeight());

        if (velocityX <= 0) { //
            minX = -maxX;
            maxX = 0;
        }
        if (velocityY <= 0) { //
            minY = -maxY;
            maxY = 0;
        }
        if (mFlingRunnable == null) {
            mFlingRunnable = new FlingRunnable(view);
        }
        mLastMotionX = 0.0f;
        mLastMotionY = 0.0f;
        mScroller.fling(0, 0, velocityX, velocityY, minX, maxX, minY, maxY);
        view.post(mFlingRunnable);
        if (mImageFlingListener != null) {
            mImageFlingListener.onSingleImageFlingBegin();
        }
    }

    public void endFling(MultiTransformImageView view) {
        mScroller.forceFinished(true);
        if (mFlingRunnable != null) {
            view.removeCallbacks(mFlingRunnable);
        }
    }


    private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
    }


    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }


    @SuppressLint("InlinedApi")
    public boolean onTouchEvent(MultiTransformImageView view, MotionEvent event) {
        acquireVelocityTrackerAndAddMovement(event);
        int action = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR
                ? event.getAction() & MotionEvent.ACTION_MASK : event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (view.getViewPager().isFakeDragging()) {
                    return false;
                }
                // if in scroll state, finish first (for continue scroll).
                view.getViewPager().finishScroll();
                if (mDoubleTapListener != null) {
                    boolean hadTapMessage = mHandler.hasMessages(TAP);
                    if (hadTapMessage)
                        mHandler.removeMessages(TAP);
                    if ((mCurrentDownEvent != null)
                            && (mPreviousUpEvent != null)
                            && hadTapMessage
                            && isConsideredDoubleTap(mCurrentDownEvent,
                            mPreviousUpEvent, event)) {
                        // This is a second tap
                        mIsDoubleTapping = true;
//					mDoubleTapListener.onDoubleTapEvent(event);
                    } else {
                        // This is a first tap
                        mHandler.sendEmptyMessageDelayed(TAP, DOUBLE_TAP_TIMEOUT);
                    }
                }

                if (mCurrentDownEvent != null) {
                    mCurrentDownEvent.recycle();
                }
                mCurrentDownEvent = MotionEvent.obtain(event);
                mAlwaysInTapRegion = true;
                mAlwaysInBiggerTapRegion = true;
                mStillDown = true;
                mInLongPress = false;

                if (mIsLongpressEnabled) {
                    mHandler.removeMessages(LONG_PRESS);

                    // Send Long Press Message
                    mHandler.sendEmptyMessageAtTime(LONG_PRESS,
                            mCurrentDownEvent.getDownTime() + TAP_TIMEOUT
                                    + LONGPRESS_TIMEOUT);
                }

                if (mGestureListener != null) {
                    mGestureListener.onDown(event);
                }
                mLastMotionX = event.getX();
                mLastMotionY = event.getY();
                endFling(view);
                return true;

            case MotionEvent.ACTION_MOVE:
                if (mInLongPress) {
                    break;
                }
                float x = event.getX();
                float y = event.getY();
                float dx = x - mLastMotionX;
                float dy = y - mLastMotionY;
                final float xDiff = Math.abs(dx);
                final float yDiff = Math.abs(dy);

                int distance = (int) ((xDiff * xDiff) + (yDiff * yDiff));
                if (mAlwaysInTapRegion) {
                    if (distance > mTouchSlopSquare) {
                        mLastMotionX = x;
                        mLastMotionY = y;
                        mAlwaysInTapRegion = false;
                        mHandler.removeMessages(TAP);
                        mHandler.removeMessages(LONG_PRESS);
                    }

                    if (distance > mBiggerTouchSlopSquare) {
                        mAlwaysInBiggerTapRegion = false;
                    }
                }
                if (mTouchMode == MODE_NONE) {
                    if (distance > mTouchSlopSquare) {
                        if ((view.getTransformRect() == null) || (view.getTransformRect() != null
                                && view.getTransformRect().width() <= view.getWidth()
                                && xDiff > yDiff)) {
                            mTouchMode = MODE_FAKEDRAG;
                            mLastMotionX = x;
                            view.getViewPager().beginFakeDrag();
                        } else if (view.getDrawable() != null) {
                            mTouchMode = MODE_DRAG;
                        }
                    }
                }
                if (mTouchMode == MODE_DRAG) { // image internal move
                    mLastMotionX = x;
                    mLastMotionY = y;
                    int edgeReachedFlags = view.postTranslateCenter(dx, dy);

                    if (xDiff > mTouchSlop && xDiff > yDiff) {
                        if ((dx > 0 && ((edgeReachedFlags & MultiTransformImageView.LEFT_EDGE_REACHED) != 0))
                                || (dx < 0 && ((edgeReachedFlags & MultiTransformImageView.RIGHT_EDGE_REACHED) != 0))) {
                            if (DEBUG) {
                                Log.v(TAG, "MODE_DRAG change to MODE_FAKEDRAG!");
                            }
                            mTouchMode = MODE_FAKEDRAG;
                            mLastMotionX = event.getRawX();
                            view.getViewPager().beginFakeDrag();
                        }
                    }

                } else if (mTouchMode == MODE_FAKEDRAG) { // viewpager move
                    float rawX = event.getRawX();
                    dx = rawX - mLastMotionX;
                    mLastMotionX = event.getRawX();
                    if (!view.getViewPager().isFakeDragging()) {
                        view.getViewPager().beginFakeDrag(); //
                    }
                    view.getViewPager().fakeDragBy(dx);
                } else if (mTouchMode == MODE_ZOOM) {
                    if (event.getPointerCount() == 2) {
                        float scale = distance(event) / mBaseDist;
                        view.zoomToSpecial(scale * mBaseScale, mPivot.x, mPivot.y);
                    }
                }
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (view.getViewPager().isFakeDragging()) {
                    return false;
                }
                if (event.getPointerCount() == 2) {
                    if (view.getDrawable() != null) {
                        if (DEBUG) {
                            Log.v(TAG, "down MODE_ZOOM");
                        }
                        mTouchMode = MODE_ZOOM;
                        mBaseDist = distance(event);
                        mBaseScale = view.getZoomScale();
                        midPoint(mPivot, event);
                        return true;
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (view.getViewPager().isFakeDragging()) {
                    float rawX = event.getRawX();
                    dx = rawX - mLastMotionX;
                    view.getViewPager().endFakeDrag(dx);
                }

                if (mGestureListener != null) {
                    mGestureListener.onUp(event);
                }

                mStillDown = false;
                MotionEvent currentUpEvent = MotionEvent.obtain(event);
                if (mIsDoubleTapping) {
                    if (mIsDoubleTapZoomEnabled) {
                        if (view.getDrawable() != null) {
                            if (view.getZoomScale() > 1.0f || view.getZoomScale() < 1.0f) {
                                view.animationZoomTo(1.0f, view.getWidth() / 2,
                                        view.getHeight() / 2, 200.f);
                            } else {
                                view.animationZoomTo(2.0f, view.getWidth() / 2, view.getHeight() / 2,
                                        200.f);
                            }
                        }
                    }
                    mDoubleTapListener.onDoubleTapEvent(event);
                } else if (mInLongPress) {
                    mHandler.removeMessages(TAP);
                    mInLongPress = false;
                } else if (mAlwaysInTapRegion) {
                    if (mGestureListener != null) {
                        mGestureListener.onSingleTapUp(event);
                    }
                } else {
                    if (mTouchMode == MODE_DRAG) {
                        mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                        int xVelocity = (int) mVelocityTracker.getXVelocity();
                        int yVelocity = (int) mVelocityTracker.getYVelocity();
                        fling(view, xVelocity, yVelocity);
                    }
                }
                if (mPreviousUpEvent != null) {
                    mPreviousUpEvent.recycle();
                }
                // Hold the event we obtained above - listeners may have changed the
                // original.
                mPreviousUpEvent = currentUpEvent;
                mIsDoubleTapping = false;
                mHandler.removeMessages(SHOW_PRESS);
                mHandler.removeMessages(LONG_PRESS);
                mTouchMode = MODE_NONE;
                releaseVelocityTracker();
                return true;

            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2) {
                    if (mTouchMode == MODE_ZOOM) {
                        if (view.getZoomScale() < view.minZoomScale()) {
                            view.animationZoomTo(view.minZoomScale(), view.getWidth() / 2,
                                    view.getHeight() / 2, 200.f);
                        } else if (view.getZoomScale() > view.maxZoomScale()) {
                            view.animationZoomTo(view.maxZoomScale(),
                                    view.getWidth() / 2, view.getHeight() / 2,
                                    200.f);
                        }
                    }
                }
                return true;

            case MotionEvent.ACTION_CANCEL:
                if (view.getViewPager().isFakeDragging()) {
                    float rawX = event.getRawX();
                    dx = rawX - mLastMotionX;
                    view.getViewPager().endFakeDrag(dx);
                }
                cancel();
                break;

        }
        return false;
    }

    private void cancel() {
        mHandler.removeMessages(SHOW_PRESS);
        mHandler.removeMessages(LONG_PRESS);
        mHandler.removeMessages(TAP);
        mIsDoubleTapping = false;
        mStillDown = false;
        mAlwaysInTapRegion = false;
        mAlwaysInBiggerTapRegion = false;
        if (mInLongPress) {
            mInLongPress = false;
        }

        releaseVelocityTracker();
    }

    /**
     * @param firstDown
     * @param firstUp
     * @param secondDown
     * @return
     */
    private boolean isConsideredDoubleTap(MotionEvent firstDown, MotionEvent firstUp,
                                          MotionEvent secondDown) {
        if (!mAlwaysInBiggerTapRegion) {
            return false;
        }

        if (secondDown.getEventTime() - firstUp.getEventTime() > DOUBLE_TAP_TIMEOUT) {
            return false;
        }

        int deltaX = (int) firstDown.getX() - (int) secondDown.getX();
        int deltaY = (int) firstDown.getY() - (int) secondDown.getY();
        return (deltaX * deltaX + deltaY * deltaY < mDoubleTapSlopSquare);
    }


    /**
     * 长按回调
     */
    private void dispatchLongPress() {
        mHandler.removeMessages(TAP);
        mInLongPress = true;
        if (mGestureListener != null) {
            mGestureListener.onLongPress(mCurrentDownEvent);
        }
    }

    /**
     *
     *
     */
    class FlingRunnable implements Runnable {

        MultiTransformImageView imageView;

        public FlingRunnable(MultiTransformImageView view) {
            imageView = view;
        }

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {
                int curX = mScroller.getCurrX();
                int curY = mScroller.getCurrY();
                float transX = curX - mLastMotionX;
                float transY = curY - mLastMotionY;
                mLastMotionX = curX;
                mLastMotionY = curY;
                imageView.postTranslateCenter(transX, transY);
                imageView.post(this);
            } else {
                if (mImageFlingListener != null) {
                    mImageFlingListener.onSingleImageFlingEnd();
                }
            }
        }

    }
}
