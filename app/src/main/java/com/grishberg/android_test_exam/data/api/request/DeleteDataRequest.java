package com.grishberg.android_test_exam.data.api.request;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by grigoriy on 22.06.15.
 */
public class DeleteDataRequest implements Parcelable {
	private long id;

	public DeleteDataRequest(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}


	//---------------- Parcelable ------------------

	public DeleteDataRequest(Parcel in){
		id			= in.readLong();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
	}

	public static final Parcelable.Creator<DeleteDataRequest> CREATOR = new Parcelable.Creator<DeleteDataRequest>() {
		// распаковываем объект из Parcel
		public DeleteDataRequest createFromParcel(Parcel in) {
			return new DeleteDataRequest(in);
		}

		public DeleteDataRequest[] newArray(int size) {
			return new DeleteDataRequest[size];
		}
	};
}
