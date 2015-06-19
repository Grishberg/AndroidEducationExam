package com.grishberg.android_test_exam.data.api.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by grigoriy on 16.06.15.
 */
public class DataResponse implements Parcelable {
	private long id;
	public DataResponse(){
		id = 0;
	}

	public DataResponse(long id){
		this.id	= id;
	}

	public long getId(){
		return id;
	}

	//--------------- implement Parcelable

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
	}

	public static final Parcelable.Creator<DataResponse> CREATOR
			= new Parcelable.Creator<DataResponse>() {
		public DataResponse createFromParcel(Parcel in) {
			return new DataResponse(in);
		}

		public DataResponse[] newArray(int size) {
			return new DataResponse[size];
		}
	};

	public DataResponse( Parcel in){
		id	= in.readLong();
	}
}
