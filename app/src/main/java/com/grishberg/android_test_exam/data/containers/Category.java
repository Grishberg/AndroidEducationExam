package com.grishberg.android_test_exam.data.containers;

import android.content.ContentValues;
import android.database.Cursor;

import com.grishberg.android_test_exam.data.model.DbHelper;

/**
 * Created by grigoriy on 16.06.15.
 */
public class Category {
	private long id;
	private String title;

	private Category(long id, String title){
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

	public static Category fromCursor(Cursor c){
		int idColId = c.getColumnIndex(DbHelper.COLUMN_ID);
		int titleColId = c.getColumnIndex(DbHelper.CATEGORY_TITLE);

		return new Category(
				c.getLong(idColId),
				c.getString(titleColId));

	}

	public ContentValues buildContentValues() {
		ContentValues cv = new ContentValues();
		if (id >= 0) {
			cv.put(DbHelper.COLUMN_ID, id);
		}
		cv.put(DbHelper.CATEGORY_TITLE, title);
		return cv;
	}

}
