package com.grishberg.android_test_exam.data.containers;

import android.content.ContentValues;
import android.database.Cursor;

import com.grishberg.android_test_exam.data.model.DbHelper;

import java.util.Date;

/**
 * Created by grigoriy on 16.06.15.
 */
public class Article {

	private long	id;
	private long	serverId;
	private String	title;
	private String	description;
	private String	photoUrl;
	private boolean isPublished;
	private long	categoryId;
	private Category	category;
	private Date		created;
	private Date		updated;
	private int 		idAuthor;

	private Article(long id,long serverId
			, String title, String description, String photoUrl, boolean isPublished
			, long categoryId, long created, long updated) {
		this.id	= id;
		this.title	= title;
		this.description	= description;
		this.photoUrl		= photoUrl;
		this.isPublished	= isPublished;
		this.categoryId		= categoryId;
		this.created		= new Date(created);
		this.updated		= new Date(updated);

	}

	public Article(
			long serverId
			, String title
			, String description
			, String photoUrl
			, boolean isPublished
			, long categoryId
			, long created
			, long published) {
		this(-1,serverId, title, description, photoUrl, isPublished, categoryId
				, created, published);
	}

	public ContentValues buildContentValues() {
		ContentValues cv = new ContentValues();
		if (id >= 0) {
			cv.put(DbHelper.COLUMN_ID, id);
		}
		cv.put(DbHelper.ARTICLES_SERVER_ID,		serverId);
		cv.put(DbHelper.ARTICLES_TITLE, 		title);
		cv.put(DbHelper.ARTICLES_DESCRIPTION, 	description);
		cv.put(DbHelper.ARTICLES_PHOTO_URL, 	photoUrl);
		cv.put(DbHelper.ARTICLES_PUBLISHED,		isPublished);
		cv.put(DbHelper.ARTICLES_CATEGORY_ID, 	categoryId);
		cv.put(DbHelper.ARTICLES_CREATED, 		created.getTime());
		cv.put(DbHelper.ARTICLES_UPDATED, 		updated.getTime());

		return cv;
	}

	public static Article fromCursor(Cursor c){
		int idColId				= c.getColumnIndex(DbHelper.COLUMN_ID);
		int serverIdColId		= c.getColumnIndex(DbHelper.ARTICLES_SERVER_ID);
		int titleColId			= c.getColumnIndex(DbHelper.ARTICLES_TITLE);
		int descriptionColId	= c.getColumnIndex(DbHelper.ARTICLES_DESCRIPTION);
		int photoUrlColId		= c.getColumnIndex(DbHelper.ARTICLES_PHOTO_URL);
		int isPublishColId		= c.getColumnIndex(DbHelper.ARTICLES_PUBLISHED);
		int categoryColId		= c.getColumnIndex(DbHelper.ARTICLES_CATEGORY_ID);
		int createdColId		= c.getColumnIndex(DbHelper.ARTICLES_CREATED);
		int updatedColId		= c.getColumnIndex(DbHelper.ARTICLES_PUBLISHED);

		return new Article(
				c.getLong(idColId),
				c.getLong(serverIdColId),
				c.getString(titleColId),
				c.getString(descriptionColId),
				c.getString(photoUrlColId),
				c.getInt(isPublishColId) == 1,
				c.getLong(categoryColId),
				c.getLong(createdColId),
				c.getLong(updatedColId)
		);
	}
}
