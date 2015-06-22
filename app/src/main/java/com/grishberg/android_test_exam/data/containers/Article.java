package com.grishberg.android_test_exam.data.containers;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.grishberg.android_test_exam.data.model.DbHelper;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by grigoriy on 16.06.15.
 */
public class Article implements Parcelable{
	private long			id;
	@Expose
	private String			title;
	@Expose
	private String			description;
	private PhotoContainer	photo;
	@Expose
	private boolean 		published;
	@Expose
	@SerializedName("category_id")
	private long			categoryId;
	@SerializedName("created_at")
	private Date 			createdAt;
	@SerializedName("updated_at")
	private Date 			updatedAt;
	private boolean			own;

	public Article(long id) {

		this.id		= id;
		title		= null;
		description	= null;
		photo		= new PhotoContainer("");
		createdAt	= null;
		updatedAt	= null;
		own			= false;
		published	= false;
	}

	public Article(long id
			, String title, String description, String photoUrl, boolean isPublished
			, long categoryId, long created, long updated, boolean own) {
		this.id				= id;
		this.title			= title;
		this.description	= description;
		this.photo			= new PhotoContainer(photoUrl);
		this.published		= isPublished;
		this.categoryId		= categoryId;
		this.createdAt 		= new Date(created);
		this.updatedAt 		= new Date(updated);
		this.own			= own;

	}

	public ContentValues buildContentValues() {
		ContentValues cv = new ContentValues();
		if (id >= 0) {
			cv.put(DbHelper.COLUMN_ID, id);
		}
		cv.put(DbHelper.ARTICLES_TITLE, 		title);
		cv.put(DbHelper.ARTICLES_DESCRIPTION, 	description);
		if(photo != null) {
			cv.put(DbHelper.ARTICLES_PHOTO_URL, photo.getUrl());
		}
		cv.put(DbHelper.ARTICLES_PUBLISHED,		published);
		cv.put(DbHelper.ARTICLES_CATEGORY_ID, 	categoryId);
		cv.put(DbHelper.ARTICLES_CREATED, 		createdAt.getTime());
		cv.put(DbHelper.ARTICLES_UPDATED, 		updatedAt.getTime());
		cv.put(DbHelper.ARTICLES_OWN,			own);

		return cv;
	}

	public static Article fromCursor(Cursor c){
		int idColId				= c.getColumnIndex(DbHelper.COLUMN_ID);
		int titleColId			= c.getColumnIndex(DbHelper.ARTICLES_TITLE);
		int descriptionColId	= c.getColumnIndex(DbHelper.ARTICLES_DESCRIPTION);
		int photoUrlColId		= c.getColumnIndex(DbHelper.ARTICLES_PHOTO_URL);
		int isPublishColId		= c.getColumnIndex(DbHelper.ARTICLES_PUBLISHED);
		int categoryColId		= c.getColumnIndex(DbHelper.ARTICLES_CATEGORY_ID);
		int createdColId		= c.getColumnIndex(DbHelper.ARTICLES_CREATED);
		int updatedColId		= c.getColumnIndex(DbHelper.ARTICLES_PUBLISHED);
		int isMineColId			= c.getColumnIndex(DbHelper.ARTICLES_OWN);

		return new Article(
				c.getLong(idColId),
				c.getString(titleColId),
				c.getString(descriptionColId),
				c.getString(photoUrlColId),
				c.getInt(isPublishColId) == 1,
				c.getLong(categoryColId),
				c.getLong(createdColId),
				c.getLong(updatedColId),
				c.getInt(isMineColId) == 1
		);
	}

	// getters

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getPhotoUrl() {
		return photo.getUrl();
	}

	public boolean isPublished() {
		return published;
	}

	public long getCategoryId() {
		return categoryId;
	}


	public Date getCreated() {
		return createdAt;
	}

	public Date getUpdated() {
		return updatedAt;
	}

	public boolean getIsMine() {
		return own;
	}

	//---------------- Parcelable ------------------

	public Article(Parcel in){

		id			= in.readLong();
		title		= in.readString();
		description	= in.readString();
		photo		= (PhotoContainer)in.readParcelable(PhotoContainer.class.getClassLoader());
		published	= in.readInt() == 1;
		categoryId	= in.readLong();
		createdAt	= new Date( in.readLong() );
		updatedAt	= new Date( in.readLong() );
		own			= in.readInt() == 1;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString( title );
		dest.writeString( description );
		dest.writeParcelable( photo, flags);
		dest.writeInt(published ? 1 : 0);
		dest.writeLong(categoryId);
		dest.writeLong( createdAt.getTime());
		dest.writeLong(updatedAt.getTime());
		dest.writeInt(own ? 1 : 0);
	}

	public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
		// распаковываем объект из Parcel
		public Article createFromParcel(Parcel in) {
			return new Article(in);
		}

		public Article[] newArray(int size) {
			return new Article[size];
		}
	};

}
