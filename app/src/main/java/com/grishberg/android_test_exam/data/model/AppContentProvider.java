package com.grishberg.android_test_exam.data.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by grigoriy on 16.06.15.
 */
public class AppContentProvider extends ContentProvider {

	private static final String AUTHORITY = "com.grishberg.android_test_exam.content_provider";

	private static final String PATH_CATEGORIES		= "categories";
	private static final String PATH_ARTICLES		= "articles";

	public static final Uri CONTENT_URI_CATEGORIES	= Uri.parse("content://" + AUTHORITY + "/" + PATH_CATEGORIES);
	public static final Uri CONTENT_URI_ARTICLES	= Uri.parse("content://" + AUTHORITY + "/" + PATH_ARTICLES);

	private static final int CODE_CATEGORIES		= 0;
	private static final int CODE_ARTICLES			= 1;

	private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		URI_MATCHER.addURI(AUTHORITY, PATH_CATEGORIES, CODE_CATEGORIES);
		URI_MATCHER.addURI(AUTHORITY, PATH_ARTICLES, CODE_ARTICLES);
	}

	private static DbHelper dbHelper;

	public synchronized static DbHelper getDbHelper(Context context) {
		if (null == dbHelper) {
			dbHelper = new DbHelper(context);
		}
		return dbHelper;
	}


	@Override
	public boolean onCreate() {
		getDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		String table = parseUri(uri);
		Cursor cursor = dbHelper.getReadableDatabase()
				.query(table, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String table = parseUri(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long id = db.insert(table, null, values);
		if (-1 == id) {
			throw new RuntimeException("Record wasn't saved.");
		}
		Uri resultUri = ContentUris.withAppendedId(uri, id);
		getContext().getContentResolver().notifyChange(resultUri, null);

		return resultUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		String table = parseUri(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int result = db.delete(table, selection, selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		String table = parseUri(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int result = db.update(table, values, selection, selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

	private String parseUri(Uri uri) {
		return parseUri(URI_MATCHER.match(uri));
	}

	private String parseUri(int match) {
		String table = null;
		switch (match) {
			case CODE_CATEGORIES:
				table = dbHelper.TABLE_CATEGORIES;
				break;
			case CODE_ARTICLES:
				table = dbHelper.TABLE_ARTICLES;
				break;
			default:
				throw new IllegalArgumentException("Invalid DB code: " + match);
		}
		return table;
	}

	private Cursor getCategories(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select ...";
		return db.rawQuery(sql, null);
	}
}
