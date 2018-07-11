package com.imagepicker.xp.utils;

import android.net.Uri;

import com.imagepicker.xp.bean.ImageItem;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by dell on 2017/12/23.
 */

public class ImageNetUtil {

    public static ArrayList<ImageItem> netImageList ;
    private static String  path = "/storage/emulated/0/com.imagepicker.xp/imageCache/1514020928016.jpg";

    public static ArrayList<ImageItem> getNetImageList(){
        ImageItem item1 = new ImageItem();
        item1.setImgneturl("http://7xqiu3.com1.z0.glb.clouddn.com/_45105376_TP_2017-12-19_15:47:28_1587");
        item1.setImagePath("28_1587");
        item1.setUri(Uri.fromFile(new File(path)));
        item1.setType(1);//

        ImageItem item2 = new ImageItem();
        item2.setImgneturl("http://7xqiu3.com1.z0.glb.clouddn.com/_45105376_TP_2017-12-19_11:46:41_7656");
        item2.setImagePath("41_7656");
        item2.setUri(Uri.fromFile(new File(path)));
        item2.setType(1);//来自网络


        ImageItem item3 = new ImageItem();
        item3.setImgneturl("http://f10.baidu.com/it/u=3853349812,859130730&fm=72");
        item3.setImagePath("41_76533");
        item3.setUri(Uri.fromFile(new File(path)));
        item3.setType(1);//来自网络

        netImageList = new ArrayList<>();
        netImageList.add(item1);
        netImageList.add(item2);
        netImageList.add(item3);


        return netImageList;
    }
}
