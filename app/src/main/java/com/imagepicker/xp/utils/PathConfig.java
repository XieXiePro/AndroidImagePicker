package com.imagepicker.xp.utils;

import android.os.Environment;

import com.imagepicker.xp.app.PickerApplication;

public class PathConfig {

    static class Path {
        static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().getPath();

        static final String BASE_PATH = SD_CARD_PATH + "/" + PickerApplication.getAppContext().getPackageName();

        static final String IMAGE_CATCH = "/imageCache";

        static final String DOWNLOAD_CATCH = "/downloadCache";
    }

    public static final String getBasePath() {
        return Path.BASE_PATH;
    }

    public static final String getImagePath() {
        return Path.BASE_PATH + Path.IMAGE_CATCH;
    }

    public static final String getDownloadPath() {
        return Path.BASE_PATH + Path.DOWNLOAD_CATCH;
    }
}
