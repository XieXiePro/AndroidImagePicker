package com.imagepicker.xp;

import android.net.Uri;

import com.xp.pro.imagepickerlib.bean.ImageItem;

import java.io.File;
import java.util.ArrayList;

public class ImageNetData {

    private static final String path = "http://7xqiu3.com1.z0.glb.clouddn.com/_45105376_TP_2017-12-19_15:47:28_1587";

    public static ArrayList<ImageItem> getNetImageList() {
        ImageItem item1 = new ImageItem();
        item1.setImgneturl(path);
        item1.setImagePath(path);
        item1.setUri(Uri.fromFile(new File(path)));
        item1.setType(1);
        ImageItem item2 = new ImageItem();
        item2.setImgneturl(path);
        item2.setImagePath(path);
        item2.setUri(Uri.fromFile(new File(path)));
        item1.setType(1);
        ImageItem item3 = new ImageItem();
        item3.setImgneturl(path);
        item3.setImagePath(path);
        item3.setUri(Uri.fromFile(new File(path)));
        item3.setType(1);
        ArrayList<ImageItem> netImageList = new ArrayList<>();
        netImageList.add(item1);
        netImageList.add(item2);
        netImageList.add(item3);
        return netImageList;
    }
}
