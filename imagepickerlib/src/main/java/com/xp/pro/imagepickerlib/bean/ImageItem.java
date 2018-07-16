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
    public int type;
    public Uri uri;
    public boolean isSelected = false;


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
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected ImageItem(Parcel in) {
        this.imageId = in.readString();
        this.thumbnailPath = in.readString();
        this.imagePath = in.readString();
        this.imgneturl = in.readString();
        this.type = in.readInt();
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.isSelected = in.readByte() != 0;
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
}
