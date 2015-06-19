package com.grishberg.android_test_exam.data.containers;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.grishberg.android_test_exam.data.model.DbHelper;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by grigoriy on 16.06.15.
 */
public class Article {
	private long			id;
	@Expose
	private String			title;
	@Expose
	private String			description;
	private PhotoContainer	photo;
	@Expose
	private boolean 		published;
	@Expose
	private long			category_id;
	private Date			created_at;
	private Date			updated_at;
	private boolean			own;

	public Article(long id
			, String title, String description, String photoUrl, boolean isPublished
			, long categoryId, long created, long updated, boolean own) {
		this.id				= id;
		this.title			= title;
		this.description	= description;
		this.photo			= new PhotoContainer(photoUrl);
		this.published		= isPublished;
		this.category_id	= categoryId;
		this.created_at		= new Date(created);
		this.updated_at		= new Date(updated);
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
		cv.put(DbHelper.ARTICLES_CATEGORY_ID, 	category_id);
		cv.put(DbHelper.ARTICLES_CREATED, 		created_at.getTime());
		cv.put(DbHelper.ARTICLES_UPDATED, 		updated_at.getTime());
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
		return category_id;
	}


	public Date getCreated() {
		return created_at;
	}

	public Date getUpdated() {
		return updated_at;
	}

	public boolean getIsMine() {
		return own;
	}

	public static class ArticlefSerializer implements JsonSerializer<Article>
	{
		@Override
		public JsonElement serialize(Article src, Type typeOfSrc, JsonSerializationContext context)
		{
			JsonObject result = new JsonObject();
			result.addProperty("title", src.getTitle());
			result.addProperty("description", src.getDescription());
			result.addProperty("published", src.isPublished());
			result.addProperty("category_id", src.getCategoryId());

			//result.add("Article", fields);
			return result;
		}
	}
}
