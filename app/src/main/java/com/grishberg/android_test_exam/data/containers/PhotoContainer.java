package com.grishberg.android_test_exam.data.containers;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by grigoriy on 18.06.15.
 */
public class PhotoContainer implements Parcelable {

	private String url;

	public PhotoContainer(String url){
		this.url	= url;
	}
	public String getUrl() {
		return url;
	}


	//-------------- Parcelable -----------------

	public PhotoContainer(Parcel in){
		url	= in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(url);
	}

	public static final Parcelable.Creator<PhotoContainer> CREATOR = new Parcelable.Creator<PhotoContainer>() {
		// распаковываем объект из Parcel
		public PhotoContainer createFromParcel(Parcel in) {
			return new PhotoContainer(in);
		}

		public PhotoContainer[] newArray(int size) {
			return new PhotoContainer[size];
		}
	};

}
