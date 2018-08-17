package com.xp.pro.imagepickerlib.utils;

import android.os.Environment;

public class PathConfig {

    static class Path {
        static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().getPath();

        static final String BASE_PATH = SD_CARD_PATH + "/imagePicker";

        static final String IMAGE_CATCH = "/imageCache";

    }

    public static final String getBasePath() {
        return Path.BASE_PATH;
    }

    public static final String getImagePath() {
        return Path.BASE_PATH + Path.IMAGE_CATCH;
    }

}