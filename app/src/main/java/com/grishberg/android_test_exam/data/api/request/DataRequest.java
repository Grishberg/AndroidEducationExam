package com.grishberg.android_test_exam.data.api.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.grishberg.android_test_exam.data.api.response.DataResponse;

import java.io.Serializable;

/**
 * Created by grigoriy on 16.06.15.
 */
public class DataRequest implements Parcelable {
	private long id;
	private String data;

	public DataRequest(){
		this(0, null);
	}

	public DataRequest(long id){
		this(id, null);
	}

	public DataRequest(String data){
		this(0, data);
	}

	public DataRequest(long id, String data){
		this.id		= id;
		this.data	= data;
	}

//	public DataRequest(String id, String data){
//		this(0, id, data);
//	}


	public long		getId(){ return id;}
	public String 	getData(){ return data;}

	//--------------- implement Parcelable

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
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
		id		= in.readLong();
		data	= in.readString();
	}
}
