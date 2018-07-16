package com.imagepicker.xp;

import android.net.Uri;

import com.xp.pro.imagepickerlib.bean.ImageItem;

import java.io.File;
import java.util.ArrayList;

public class ImageNetData {

    public static ArrayList<ImageItem> netImageList ;
    private static String  path = "http://7xqiu3.com1.z0.glb.clouddn.com/_45105376_TP_2017-12-19_15:47:28_1587";

    public static ArrayList<ImageItem> getNetImageList(){
        ImageItem item1 = new ImageItem();
        item1.setImgneturl("http://7xqiu3.com1.z0.glb.clouddn.com/_45105376_TP_2017-12-19_15:47:28_1587");
        item1.setImagePath("http://7xqiu3.com1.z0.glb.clouddn.com/_45105376_TP_2017-12-19_15:47:28_1587");
        item1.setUri(Uri.fromFile(new File(path)));
        item1.setType(1);

        netImageList = new ArrayList<>();
        netImageList.add(item1);

        return netImageList;
    }
}
