package com.imagepicker.xp.bean;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ImageBean implements Serializable, Parcelable {

	public String parentName;
	public long size;
	public String displayName;
	public String path;
	public boolean isChecked;

	public ImageBean() {
		super();
	}

	public ImageBean(String path) {
		super();
		this.path = path;
	}

	public ImageBean(String parentName, long size, String displayName,
					 String path, boolean isChecked) {
		super();
		this.parentName = parentName;
		this.size = size;
		this.displayName = displayName;
		this.path = path;
		this.isChecked = isChecked;
	}

	@Override
	public String toString() {
		return "ImageBean [parentName=" + parentName + ", size=" + size
				+ ", displayName=" + displayName + ", path=" + path
				+ ", isChecked=" + isChecked + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.parentName);
		dest.writeLong(this.size);
		dest.writeString(this.displayName);
		dest.writeString(this.path);
		dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
	}

	protected ImageBean(Parcel in) {
		this.parentName = in.readString();
		this.size = in.readLong();
		this.displayName = in.readString();
		this.path = in.readString();
		this.isChecked = in.readByte() != 0;
	}

	public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {
		@Override
		public ImageBean createFromParcel(Parcel source) {
			return new ImageBean(source);
		}

		@Override
		public ImageBean[] newArray(int size) {
			return new ImageBean[size];
		}
	};
}
