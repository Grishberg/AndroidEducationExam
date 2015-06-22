package com.grishberg.android_test_exam.data.api.request;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.grishberg.android_test_exam.data.api.response.DataResponse;
import com.grishberg.android_test_exam.data.containers.Article;

import java.io.Serializable;

/**
 * Created by grigoriy on 16.06.15.
 */
public class DataRequest implements Parcelable {
	private Article	article;
	private String	uri;

	public DataRequest(){
		this(null, null);
	}

	public DataRequest(long id){
		this(null, null);
	}

	public DataRequest(Article article){
		this(article,null);
	}


	public DataRequest(Article article, String uri){
		this.article	= article;
		this.uri		= uri;
	}

	public Article	getArticle(){ return article;}
	public String 	getAdditionalData(){ return uri;}

	//--------------- implement Parcelable

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(article, flags);
		dest.writeString(uri);
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
		article	= (Article)in.readParcelable(Article.class.getClassLoader());
		uri		= in.readString();
	}
}
