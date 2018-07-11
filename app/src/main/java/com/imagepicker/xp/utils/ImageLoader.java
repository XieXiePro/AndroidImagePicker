package com.imagepicker.xp.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.File;

public class ImageLoader {

    private static Singleton<ImageLoader, Void> sImageLoader = new Singleton<ImageLoader, Void>() {
        @Override
        protected ImageLoader create(Void var1) {
            return new ImageLoader();
        }
    };

    public static ImageLoader getInstance() {
        return sImageLoader.get(null);
    }

    private static final String SP_VALUE_SAVE_FLOW = "save_flow";

    private boolean isSaveFlow = false;

    private ImageLoader() {
        init();
    }

    private void init() {
//        isSaveFlow = SharedPreferencUtil.getBoolean(SP_VALUE_SAVE_FLOW, false);
    }

    public void setSaveFlow(boolean isSave) {
        isSaveFlow = isSave;
//        SharedPreferencUtil.setBoolean(SP_VALUE_SAVE_FLOW, isSaveFlow);
    }

    public boolean getSaveFlow() {
        return isSaveFlow;
    }

    private Context getContext(View container) {
        Context c = null;
        if (null != container) {
            c = container.getContext();
        }
        return c;
    }

    /**
     * 显示网络图片
     *
     * @param container
     * @param url       图片网络地址
     * @param <T>
     */
    public <T extends ImageView> void display(T container, String url) {
        init_config(container, url).into(container);
    }

    public <T extends ImageView> void display(String url, GlideDrawableImageViewTarget target) {
        Context context = getContext(target.getView());
        if (null != context) {
            url = checkSaveFlow(target.getView(), url);
            Glide.with(context).load(url).crossFade().into(target);
        }
    }


    /**
     * 显示uri图片
     *
     * @param container
     * @param uri       图片网络地址
     * @param <T>
     */
    public <T extends ImageView> void display(T container, Uri uri) {
        Context context = getContext(container);
        if (null != context) {
            Glide.with(context).load(uri).crossFade().into(container);
        }
    }

    /**
     * 加载资源图片，当然也可以加在gif图
     *
     * @param container
     * @param resId     资源id
     * @param <T>
     */
    public <T extends ImageView> void display(T container, int resId) {
        Context context = getContext(container);
        if (null != context) {
            Glide.with(context).load(resId).crossFade().into(container);
        }
    }

    /**
     * 加载文件图片
     *
     * @param container
     * @param file      图片文件
     * @param <T>
     */
    public <T extends ImageView> void display(T container, File file) {
        Context context = getContext(container);
        if (null != context) {
            Glide.with(context).load(file).crossFade().into(container);
        }
    }

    /**
     * 加载字节文件图片
     *
     * @param container
     * @param bytes     图片字节
     * @param <T>
     */
    public <T extends ImageView> void display(T container, Byte[] bytes) {
        Context context = getContext(container);
        if (null != context) {
            Glide.with(context).load(bytes).crossFade().into(container);
        }
    }

    /**
     * 显示缩略图
     *
     * @param container
     * @param uri
     * @param thumbnail 比例
     * @param <T>
     */
    public <T extends ImageView> void display(T container, Uri uri, float thumbnail) {
        Context context = getContext(container);
        if (null != context) {
            Glide.with(context).load(uri).thumbnail(thumbnail).crossFade().into(container);
        }
    }


    /**
     * 显示缩略图
     *
     * @param container
     * @param url
     * @param thumbnail 比例
     * @param <T>
     */
    public <T extends ImageView> void display(T container, String url, float thumbnail) {
        Context context = getContext(container);
        if (null != context) {
            Glide.with(context).load(url).thumbnail(thumbnail).crossFade().into(container);
        }
    }

    /**
     * 显示placeHolder图片和error图片
     *
     * @param container
     * @param url
     * @param config
     * @param <T>
     */
    public <T extends ImageView> void display(T container, String url, DisplayConfig config) {
        init_config(container, url)
                .placeholder(config.getId_holder_image())
                .error(config.getId_err_image())
                .into(container);
    }

    private <T extends ImageView> DrawableRequestBuilder init_config(T container, String url) {
        Context context = getContext(container);
        url = checkSaveFlow(container, url);
//        if (context == null) {
//            context = GroupContext.getApplication();
//        }
        return Glide.with(context)
                .load(url)
                .crossFade(); //淡入淡出效果
    }

    private <T extends ImageView> String checkSaveFlow(T container, String url) {
        if (isSaveFlow && !TextUtils.isEmpty(url) && StringUtil.isUrl(url)) {
            return url + "?w=" + container.getMeasuredWidth() + "&h=" + +container.getMeasuredHeight();
        }
        return url;
    }

    public void downloadOnly(final Context c, final String url, final int width, final int height, final DownloadListener listener) {
        ThreadUtils.runOnNonUIthread(
                new Runnable() {
                    @Override
                    public void run() {
                        Throwable throwable = new Resources.NotFoundException("downloadOnly something is null?");
                        try {
                            if (null != listener) {
                                final Bitmap bitmap = Glide.with(c).load(url).asBitmap().into(width, height).get();
                                if (null != bitmap) {
                                    ThreadUtils.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listener.onLoadSuccess(bitmap);
                                        }
                                    });
                                }
                            }
                        } catch (Throwable e) {
                            throwable = e;
                        }
                        if (null != listener) {
                            listener.onLoadFailed(throwable);
                        }
                    }
                }
        );
    }

    public interface DownloadListener {
        void onLoadSuccess(Bitmap bitmap);

        void onLoadFailed(Throwable e);
    }


}
