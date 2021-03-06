package com.xp.pro.imagepickerlib.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ImageItem implements Serializable, Parcelable {
    public String imageId;
    public String thumbnailPath;
    public String imagePath;
    public String imgneturl;
    /**
     * 是否已添加水印：false：未添加，true：已添加
     */
    public boolean isMark;
    /**
     * 0:从相册选择获取图片；1:从网络获取图片；2:从拍照获取图片
     */
    public int type;
    public Uri uri;
    public boolean isSelected = false;
    /**
     * 是否可修改模式：true:无法单张修改或删除 可单张修改，
     */
    private boolean singleModify;

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getImgneturl() {
        return imgneturl;
    }

    public int getType() {
        return type;
    }

    public void setImgneturl(String imgneturl) {
        this.imgneturl = imgneturl;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isMark() {
        return isMark;
    }

    public void setMark(boolean mark) {
        isMark = mark;
    }

    public boolean isSingleModify() {
        return singleModify;
    }

    public void setSingleModify(boolean singleModify) {
        this.singleModify = singleModify;
    }

    public ImageItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageId);
        dest.writeString(this.thumbnailPath);
        dest.writeString(this.imagePath);
        dest.writeString(this.imgneturl);
        dest.writeInt(this.type);
        dest.writeParcelable(this.uri, flags);
        dest.writeByte(this.isMark ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.singleModify ? (byte) 1 : (byte) 0);
    }

    protected ImageItem(Parcel in) {
        this.imageId = in.readString();
        this.thumbnailPath = in.readString();
        this.imagePath = in.readString();
        this.imgneturl = in.readString();
        this.type = in.readInt();
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.isMark = in.readByte() != 0;
        this.isSelected = in.readByte() != 0;
        this.singleModify = in.readByte() != 0;
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };

    @Override
    public String toString() {
        return "ImageItem{" +
                "imageId='" + imageId + '\'' +
                ", thumbnailPath='" + thumbnailPath + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", imgneturl='" + imgneturl + '\'' +
                ", isMark=" + isMark +
                ", type=" + type +
                ", uri=" + uri +
                ", isSelected=" + isSelected +
                ", singleModify=" + singleModify +
                '}';
    }
}
