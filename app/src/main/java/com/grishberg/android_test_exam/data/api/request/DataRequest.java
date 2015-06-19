package com.grishberg.android_test_exam.data.api.request;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by grigoriy on 16.06.15.
 */
public class DataRequest implements Parcelable {
	private String id;
	private String data;

	public DataRequest(){
		this(null, null);
	}

	public DataRequest(String data){
		this(null, data);
	}

	public DataRequest(String id, String data){
		this.id		= id;
		this.data	= data;
	}

	public String getId(){return id;}
	public String getData(){ return data;}

	//--------------- implement Parcelable

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(data);
	}

	public static final Parcelable.Creator<DataRequest> CREATOR
			= new Parcelable.Creator<DataRequest>() {
		public DataRequest createFromParcel(Parcel in) {
			return new DataRequest(in);
		}

		public DataRequest[] newArray(int size) {
			return new DataRequest[size];
		}
	};

	public DataRequest( Parcel in){
		id		= in.readString();
		data	= in.readString();
	}
}
