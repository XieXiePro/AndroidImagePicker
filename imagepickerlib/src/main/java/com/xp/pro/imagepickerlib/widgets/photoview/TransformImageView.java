package com.xp.pro.imagepickerlib.widgets.photoview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

public class TransformImageView extends ImageView {

    // 初始图片显示策略
    public interface InitialImageShowStrategy {
        Matrix getInitialMatrix(TransformImageView view, int drawableWidth,
                                int drawableHeight, int viewWidth, int viewHeight, Matrix matrix);
    }

    /**
     * if (图高*2.0<屏幕高 && 图宽*2.0 <屏幕宽) {
     * <p>
     * 放大1.5，居中显示
     * <p>
     * } else {
     * <p>
     * if (高宽比>3.5) {//超长图
     * <p>
     * if(图宽>屏宽)
     * <p>
     * 缩小至与屏幕等宽显示，可上下滚动
     * <p>
     * else //宽度比屏幕小
     * <p>
     * 保持原来宽度，可上下滚动
     * <p>
     * } else if (宽长比>3.5) {//超宽图
     * <p>
     * if(图高>屏幕高)
     * <p>
     * 缩小至与屏幕等高显示，可左右滚动
     * <p>
     * else //高度比屏幕小
     * <p>
     * 保持原来高度，可左右滚动
     * <p>
     * } else { //正常图，大于屏幕
     * <p>
     * 保持比例自适应到屏幕大小显示，上下或左右有相等的黑边
     * <p>
     * }
     * <p>
     * }
     */

    // 图片浮层初始显示策略
    public static class PictureViewerShowStrategy implements InitialImageShowStrategy {

        static final float SCALE_RATIO = 1.5f;
        static final float MAX_SCALE_RATIO = 2.0f;

        @Override
        public Matrix getInitialMatrix(TransformImageView view, int drawableWidth, int drawableHeight,
                                       int viewWidth, int viewHeight, Matrix matrix) {

            float w = drawableWidth;
            float h = drawableHeight;
            float scale = SCALE_RATIO;

            float scaleW = w * MAX_SCALE_RATIO;
            float scaleH = h * MAX_SCALE_RATIO;

            if (viewWidth < viewHeight) {
//                if (scaleW <= viewWidth && scaleH <= viewHeight) { // 原图居中显示
//                    scale = SCALE_RATIO;
//                } else {
//                    if (h / w > 3.5f) { // 超长图
//                        if (scaleW > viewWidth) {
//                            scale = viewWidth / w;
//                        } 
//                    } else if (w / h > 3.5f) { // 超宽图
//                        if (scaleH > viewHeight) {
//                            scale = viewHeight / h;
//                        }
//                    } else {
//                        scale = viewWidth / w;
//                        scale = Math.min(scale, viewHeight / h);
//                    }
//                }    
                scale = viewWidth / w;
            } else {
                if (scaleW <= viewWidth && scaleH <= viewHeight) { // 原图居中显示
                    scale = SCALE_RATIO;
                } else {
                    scale = viewWidth / w;
                    scale = Math.min(scale, viewHeight / h);
                }
            }

            matrix.reset();
            matrix.postScale(scale, scale);

            // 高度大于view高度,初始位置为顶部
            if (h * scale > viewHeight) {
                matrix.postTranslate((viewWidth - w * scale) / 2, 0);
            } else {
                matrix.postTranslate((viewWidth - w * scale) / 2, (viewHeight - h * scale) / 2);
            }
            return matrix;
        }

    }


    public static int LEFT_EDGE_REACHED = 0x1;

    public static int TOP_EDGE_REACHED = 0x2;

    public static int RIGHT_EDGE_REACHED = 0x4;

    public static int BOTTOM_EDGE_REACHED = 0x8;


    public static final float DEFAULT_ZOOM_STEP = 0.2f;

    public static final float DEFAULT_ZOOM_MAX = 4.0f;

    public static final float DEFAULT_ZOOM_MIN = 1 / 4.0f;

    // This is the base transformation which is used to show the image
    // initially.  The current computation for this shows the image in
    // it's entirety, letter-boxing as needed.  One could choose to
    // show the image as cropped instead.
    //
    // This matrix is recomputed when we go from the thumbnail image to
    // the full size image.
    protected Matrix mBaseMatrix = new Matrix();

    // This is the supplementary transformation which reflects what
    // the user has done in terms of zooming and panning.
    //
    // This matrix remains the same when we go from the thumbnail image
    // to the full size image.
    protected Matrix mTransformMatrix = new Matrix();

    // This is the final matrix which is computed as the concatentation
    // of the base matrix and the supplementary matrix.
    protected final Matrix mDisplayMatrix = new Matrix();

    // Temporary buffer used for getting the values out of a matrix.
    private final float[] mMatrixValues = new float[9];

    private ScaleType mPrevScaleType;

    private Interpolator mInterpolator;

    private boolean mTransformEnabled = false;
    private float mZoomMax = DEFAULT_ZOOM_MAX;
    private float mZoomMin = DEFAULT_ZOOM_MIN;
    private float mZoomStep = DEFAULT_ZOOM_STEP;


    private Runnable mOnLayoutRunnable = null;


    private InitialImageShowStrategy mInitialImageShowStratege = new PictureViewerShowStrategy();

    public TransformImageView(Context context) {
        this(context, null);
    }

    public TransformImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransformImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setTransformEnabled(boolean enabled) {
        if (mTransformEnabled != enabled) {
            mTransformEnabled = enabled;
            if (enabled) {
                setScaleTypeInternal(ScaleType.MATRIX);
            } else {
                ScaleType prevScaleType = mPrevScaleType;
                if (prevScaleType != null) setScaleTypeInternal(prevScaleType);
            }
            updateTransformBaseIfNeed();
        }
    }

    public boolean isTransformEnabled() {
        return mTransformEnabled && getScaleType() == ScaleType.MATRIX;
    }


    @Override
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(scaleType);
        mPrevScaleType = scaleType;
    }

    private void setScaleTypeInternal(ScaleType scaleType) {
        super.setScaleType(scaleType);
    }


    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        updateTransformBaseIfNeed();
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        if (getWidth() <= 0) {
            mOnLayoutRunnable = new Runnable() {
                @Override
                public void run() {
                    setImageDrawable(getDrawable());
                }
            };
            return;
        }
        updateTransformBaseIfNeed();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (drawable == null) {
            mBaseMatrix.reset();
            mTransformMatrix.reset();
            applyTransform();
            return;
        }
        if (getWidth() <= 0) {
            mOnLayoutRunnable = new Runnable() {
                @Override
                public void run() {
                    setImageDrawable(getDrawable());
                }
            };
            return;
        }
        updateTransformBaseIfNeed();
    }


    public void setInitialImageShowStratege(InitialImageShowStrategy initialImageShowStratege) {
        mInitialImageShowStratege = initialImageShowStratege;
    }

    /**
     * 返回图像大小到显示大小（fitscale，即至少一边撑满屏幕）的变化
     */
    public Matrix getBaseTransformMatrix() {
        return new Matrix(mBaseMatrix);
    }

    /**
     * @return
     */
    public Matrix getTransformMatrix() {
        return new Matrix(mTransformMatrix);
    }


    /**
     * @param matrix
     */
    public void setTransformMatrix(Matrix matrix) {
        mTransformMatrix.set(matrix);
    }

    /**
     * @param matrix
     */
    public void setTransformMatrix(Matrix matrix, boolean applyTransform) {
        mTransformMatrix.set(matrix);
        if (applyTransform) {
            applyTransform();
        }
    }

    public void resetTransformMatrix() {
        if (!mTransformMatrix.isIdentity()) {
            mTransformMatrix.reset();
            applyTransform();
        }
        if (null != mRotateRunnable) {
            mRotateRunnable.reset();
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        updateTransformBaseIfNeed();
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        updateTransformBaseIfNeed();
        return changed;
    }


    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final Runnable r = mOnLayoutRunnable;
        if (r != null) {
            mOnLayoutRunnable = null;
            r.run();
        } else {
            if (getDrawable() != null) {
                updateTransformBaseIfNeed();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getZoomScale() > 1.0f) {
                // If we're zoomed in, pressing Back jumps out to show the
                // entire image, otherwise Back returns the user to the gallery.
                zoomTo(1.0f);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private int getAvailableWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getAvailableHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    // ------------------ base api for transformation -------------------
    public RectF getTransformRect() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return null;
        }

        RectF rect = new RectF(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Matrix mx = getTransform();
        mx.mapRect(rect);
        return rect;
    }

    protected Matrix getTransform() {
        Matrix matrix = mDisplayMatrix;
        matrix.set(mBaseMatrix);
        matrix.postConcat(mTransformMatrix);
        return matrix;
    }

    protected void applyTransform() {
        if (!isTransformEnabled()) {
            return;
        }
        setImageMatrix(getTransform());
    }

    protected boolean updateTransformBaseIfNeed() {
        if (!isTransformEnabled()) {
            return false;
        }
        int viewWidth = getAvailableWidth();
        int viewHeight = getAvailableHeight();
        if (viewWidth <= 0 || viewHeight <= 0) {
            return false;
        }

        final Drawable drawable = getDrawable();
        Matrix matrix = mBaseMatrix;
        if (drawable == null) {
            matrix.reset();
        } else {
            if (mInitialImageShowStratege != null) {
                matrix = mInitialImageShowStratege.getInitialMatrix(this, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), viewWidth, viewHeight, matrix);
            }
        }
        applyTransform();
        return true;
    }


    private void postTranslate(float dx, float dy) {
        mTransformMatrix.postTranslate(dx, dy);
    }

    private void postScale(float sx, float sy, float px, float py) {
        mTransformMatrix.postScale(sx, sy, px, py);
    }

    private void postRotate(float degrees, float px, float py) {
        mTransformMatrix.postRotate(degrees, px, py);
    }

    private void setRotate(float degrees, float px, float py) {
        mTransformMatrix.setRotate(degrees, px, py);
    }

    // Get the scale factor out of the matrix.
    protected float getScale(Matrix matrix) {
        float ret = Math.abs(getValue(matrix, Matrix.MSCALE_X));
        if (ret - 0.0f < 0.000001) {
            ret = Math.abs(getValue(matrix, Matrix.MSKEW_X));
        }
        return ret;
    }

    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    // ----------------------- transformation ---------------------------
    public void setZoomStep(float step) {
        if (step <= 0 || step >= 1) {
            return;
        }
        mZoomStep = step;
    }

    public void setZoomLimit(float minScale, float maxScale) {
        mZoomMax = maxScale > minScale ? maxScale : minScale;
        mZoomMin = maxScale > minScale ? minScale : maxScale;
    }

    public void zoomIn() {
        zoomBy(1 + mZoomStep);
    }

    public void zoomOut() {
        zoomBy(1 - mZoomStep);
    }

    public void zoomBy(float scale) {
        float cx = (float) getAvailableWidth() / 2;
        float cy = (float) getAvailableHeight() / 2;
        if (cx <= 0 || cy <= 0) {
            return;
        }

        zoomBy(scale, cx, cy);
    }

    public void zoomBy(float scale, float centerX, float centerY) {
        zoomTo(scale * getZoomScale(), centerX, centerY);
    }

    public void zoomTo(float scale) {
        float cx = (float) getAvailableWidth() / 2;
        float cy = (float) getAvailableHeight() / 2;
        if (cx <= 0 || cy <= 0) {
            return;
        }

        zoomTo(scale, cx, cy);
    }

    public void zoomTo(float scale, float centerX, float centerY) {
        float minScale = minZoomScale();
        float maxScale = maxZoomScale();
        if (scale < minScale) {
            scale = minScale;
        } else if (scale > maxScale) {
            scale = maxScale;
        }

        float currScale = getZoomScale();
        float deltaScale = scale / currScale;

        postScale(deltaScale, deltaScale, centerX, centerY);
        center(true, true);
    }


    public void zoomToSpecial(float scale, float centerX, float centerY) {

        float currScale = getZoomScale();
        float deltaScale = scale / currScale;

        postScale(deltaScale, deltaScale, centerX, centerY);
        center(true, true);
    }


    public void rotateBy(float degree) {
        float cx = (float) getAvailableWidth() / 2;
        float cy = (float) getAvailableHeight() / 2;
        if (cx <= 0 || cy <= 0) {
            return;
        }

        rotateBy(degree, cx, cy);
    }

    public void rotateBy(float degree, float centerX, float centerY) {
        postRotate(degree, centerX, centerY);
        applyTransform();
    }

    public void rotateTo(float degree) {
        float cx = (float) getAvailableWidth() / 2;
        float cy = (float) getAvailableHeight() / 2;
        if (cx <= 0 || cy <= 0) {
            return;
        }
        rotateTo(degree, cx, cy);
    }

    public void rotateTo(float degree, float centerX, float centerY) {
        setRotate(degree, centerX, centerY);
        applyTransform();
    }

    // Center as much as possible in one or both axis.  Centering is
    // defined as follows:  if the image is scaled down below the
    // view's dimensions then center it (literally).  If the image
    // is scaled larger than the view and is translated out of view
    // then translate it back into view (i.e. eliminate black bars)
    // 返回达到边界状态
    protected int center(boolean horizontal, boolean vertical) {
        int result = 0;
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return result;
        }

        RectF rect = new RectF(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Matrix matrix = getTransform();
        matrix.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            int viewHeight = getHeight();
            if (height < viewHeight) {
                deltaY = (viewHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                result |= TOP_EDGE_REACHED;
                deltaY = -rect.top;
            } else if (rect.bottom < viewHeight) {
                result |= BOTTOM_EDGE_REACHED;
                deltaY = getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            int viewWidth = getWidth();
            if (width < viewWidth) {
                deltaX = (viewWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                result |= LEFT_EDGE_REACHED;
                deltaX = -rect.left;
            } else if (rect.right < viewWidth) {
                result |= RIGHT_EDGE_REACHED;
                deltaX = viewWidth - rect.right;
            }
        }

        postTranslate(deltaX, deltaY);
        applyTransform();
        return result;
    }

    public float getZoomScale() {
        return getScale(mTransformMatrix);
    }

    public float maxZoomScale() {
        return mZoomMax;
    }

    public float minZoomScale() {
        return mZoomMin;
    }

    // post translate & center, then  return the edge status
    public int postTranslateCenter(float dx, float dy) {
        postTranslate(dx, dy);
        return center(true, true);
    }


    public void panBy(float dx, float dy) {
        postTranslate(dx, dy);
        applyTransform();
    }


    protected void ensureInterpolator() {
        if (mInterpolator == null) {
            mInterpolator = new AccelerateDecelerateInterpolator();
        }
    }


    /**
     * @param scale
     * @param centerX
     * @param centerY
     * @param durationMs
     */
    public void animationZoomTo(final float scale, final float centerX,
                                final float centerY, final float durationMs) {
        final long startTime = System.currentTimeMillis();
        final float fromScale = getZoomScale();
        ensureInterpolator();
        post(new Runnable() {
            public void run() {
                long currentTime = System.currentTimeMillis();
                float normalizedTime = (float) (currentTime - startTime)
                        / durationMs;
                boolean bMore = normalizedTime < 1.0f;
                normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);
                float interpolatedTime = mInterpolator
                        .getInterpolation(normalizedTime);
                float targetScale = fromScale + (scale - fromScale)
                        * interpolatedTime;
                zoomToSpecial(targetScale, centerX, centerY);
                if (bMore) {
                    post(this);
                }
            }
        });
    }

    private RotateRunnable mRotateRunnable;

    /**
     * 动画旋转
     *
     * @param degree
     */
    public void startRotateAnim(int degree) {
        if (null == mRotateRunnable) {
            mRotateRunnable = new RotateRunnable();
        }
        mRotateRunnable.startRun(degree);
    }

    class RotateRunnable implements Runnable {

        private int degree = 0;

        private int curDegree = 0;

        /**
         * 顺时针
         */
        private boolean clockwise = true;

        /**
         * 旋转的速度
         */
        private int speed = 6;

        private final int rotateDegree = 9;

        public void reset() {
            removeCallbacks(this);
            degree = 0;
            curDegree = 0;
        }

        private void setClockwise(boolean wise) {
            clockwise = wise;
        }

        public void startRun(int d) {
            if (degree == 0) {
                curDegree += d;
                if (curDegree > 360) {
                    curDegree -= 360;
                } else if (curDegree < -360) {
                    curDegree += 360;
                }
                setClockwise(d > 0);
                // 只有旋转完成了才能进行下一次旋转
                degree = d;
                post(this);
            }
        }

        @Override
        public void run() {
            if (degree != 0) {
                boolean needRotate = true;
                if (clockwise) {
                    degree -= rotateDegree;
                    rotateBy(rotateDegree);
                    if (degree <= 0) {
                        needRotate = false;
                        degree = 0;
                    }
                } else {
                    degree += rotateDegree;
                    rotateBy(-rotateDegree);
                    if (degree >= 0) {
                        needRotate = false;
                        degree = 0;
                    }
                }
                if (needRotate) {
                    postDelayed(this, speed);
                } else {
                    rotateTo(curDegree);
                }
            }
        }
    }


}



