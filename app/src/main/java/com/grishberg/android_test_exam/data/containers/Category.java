package com.grishberg.android_test_exam.data.containers;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.grishberg.android_test_exam.data.model.DbHelper;

/**
 * Created by grigoriy on 16.06.15.
 */
public class Category implements Parcelable{
	private long id;
	private String title;

	public  Category(long id, String title){
		this.id		= id;
		this.title	= title;
	}

	public Category(String title){
		this(-1, title);
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return title;
	}

	public static Category fromCursor(Cursor c){
		int idColId = c.getColumnIndex(DbHelper.COLUMN_ID);
		int titleColId = c.getColumnIndex(DbHelper.CATEGORIES_TITLE);

		return new Category(
				c.getLong(idColId),
				c.getString(titleColId));

	}

	public ContentValues buildContentValues() {
		ContentValues cv = new ContentValues();
		if (id >= 0) {
			cv.put(DbHelper.COLUMN_ID, id);
		}
		cv.put(DbHelper.CATEGORIES_TITLE, title);
		return cv;
	}

	public Category(Parcel in){
		id		= in.readLong();
		title	= in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(title);
	}

	public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
		// распаковываем объект из Parcel
		public Category createFromParcel(Parcel in) {
			return new Category(in);
		}

		public Category[] newArray(int size) {
			return new Category[size];
		}
	};
}
