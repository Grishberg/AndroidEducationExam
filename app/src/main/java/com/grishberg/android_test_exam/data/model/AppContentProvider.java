package com.grishberg.android_test_exam.data.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by grigoriy on 16.06.15.
 */
public class AppContentProvider extends ContentProvider {

	private static final String AUTHORITY = "com.grishberg.android_test_exam.content_provider";

	private static final String PATH_CATEGORIES		= DbHelper.TABLE_CATEGORIES;
	private static final String PATH_ARTICLES		= DbHelper.TABLE_ARTICLES;
	private static final String PATH_CATEGORIES_NOT_EMPTY	= "notEmptyCategories";

	public static final Uri CONTENT_URI_CATEGORIES	= Uri.parse("content://" + AUTHORITY + "/" + PATH_CATEGORIES);
	public static final Uri CONTENT_URI_ARTICLES	= Uri.parse("content://" + AUTHORITY + "/" + PATH_ARTICLES);
	public static final Uri CONTENT_URI_CATEGORIES_NOT_EMPTY	= Uri.parse("content://" + AUTHORITY + "/" + PATH_CATEGORIES_NOT_EMPTY);

	private static final int CODE_CATEGORIES		= 0;
	private static final int CODE_ARTICLES			= 1;
	private static final int CODE_ARTICLES_ID		= 2;
	private static final int CODE_CATEGORIES_NOT_EMPTY	= 3;

	private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		URI_MATCHER.addURI(AUTHORITY, PATH_CATEGORIES, 		CODE_CATEGORIES);
		URI_MATCHER.addURI(AUTHORITY, PATH_ARTICLES, 		CODE_ARTICLES);
		URI_MATCHER.addURI(AUTHORITY, PATH_ARTICLES+ "/#", 	CODE_ARTICLES_ID);
		URI_MATCHER.addURI(AUTHORITY, PATH_CATEGORIES_NOT_EMPTY,	CODE_CATEGORIES_NOT_EMPTY);
	}

	private static DbHelper dbHelper;


	public static Uri getArticlesUri(){
		return CONTENT_URI_ARTICLES;
	}

	public static Uri getArticlesUri(Long id){
		return Uri.withAppendedPath(AppContentProvider.CONTENT_URI_ARTICLES, id.toString());
	}

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
		int uriId = URI_MATCHER.match(uri);
		Cursor cursor = null;
		switch (uriId) {
			case CODE_CATEGORIES:
				cursor = dbHelper.getReadableDatabase()
						.query(DbHelper.TABLE_CATEGORIES, projection, selection, selectionArgs, null, null, sortOrder);
				break;
			case CODE_ARTICLES:
				cursor = dbHelper.getReadableDatabase()
						.query(DbHelper.TABLE_ARTICLES, projection, selection, selectionArgs, null, null, sortOrder);
				break;
			case CODE_ARTICLES_ID:
				cursor = dbHelper.getReadableDatabase()
						.query(DbHelper.TABLE_ARTICLES, projection
								, DbHelper.COLUMN_ID + " = ?"
								, new String[] { uri.getLastPathSegment() }, null, null, sortOrder);
				break;

			case CODE_CATEGORIES_NOT_EMPTY:
				cursor	= getCategories();
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}

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
		long id = insertOrUpdateById(db,uri,table,values,DbHelper.COLUMN_ID);
//		long id = db.insert(table, null, values);
		//need if autoincrement
		//if (-1 == id) {
		//	throw new RuntimeException("Record wasn't saved.");
		//}
		Uri resultUri = ContentUris.withAppendedId(uri, id);
		getContext().getContentResolver().notifyChange(resultUri, null);

		return resultUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int result = 0;
		int uriId = URI_MATCHER.match(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriId){
			case CODE_ARTICLES:
				// delete all articles
				result	= db.delete(DbHelper.TABLE_ARTICLES, selection, selectionArgs);
				break;

			case CODE_ARTICLES_ID:
				// delete by ID
				String id	= uri.getLastPathSegment();
				result = db.delete(DbHelper.TABLE_ARTICLES
						, DbHelper.COLUMN_ID + " = ?", new String[] {id});
				break;
			case CODE_CATEGORIES:
				result	= db.delete(DbHelper.TABLE_CATEGORIES,selection,selectionArgs);
				break;
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int result = 0;
		int uriId = URI_MATCHER.match(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriId){

			case CODE_ARTICLES:
				// update all articles
				result	= db.update(DbHelper.TABLE_ARTICLES, values, selection, selectionArgs);
				break;

			case CODE_ARTICLES_ID:
				// update by ID
				String id	= uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					result = db.update(DbHelper.TABLE_ARTICLES
							, values, DbHelper.COLUMN_ID + " = ?", new String[]{id});
				} else {
					result = db.update(DbHelper.TABLE_ARTICLES
							, values, DbHelper.COLUMN_ID + " = " + id + " AND "
							+ selection, selectionArgs);
				}
				break;

			case CODE_CATEGORIES:
				result	= db.update(DbHelper.TABLE_CATEGORIES, values, selection,selectionArgs);
				break;
		}
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
			case CODE_ARTICLES_ID:
				table = dbHelper.TABLE_ARTICLES;
				break;
			default:
				throw new IllegalArgumentException("Invalid DB code: " + match);
		}
		return table;
	}

	private long insertOrUpdateById(SQLiteDatabase db, Uri uri, String table,
									ContentValues values, String column) throws SQLiteConstraintException{
		long result	= -1;

		try {
			result	= db.insertOrThrow(table, null, values);
		} catch (SQLiteConstraintException e) {
			int nrRows = update(uri, values, column + "=?",
					new String[]{values.getAsString(column)});
			if (nrRows == 0) {
				throw e;
			}
		}
		return result;
	}

	private Cursor getCategories(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		StringBuilder sqlBuilder = new StringBuilder("SELECT art.");
		sqlBuilder.append(DbHelper.ARTICLES_CATEGORY_ID);
		sqlBuilder.append(" AS ");
		sqlBuilder.append(DbHelper.COLUMN_ID);
		sqlBuilder.append(", cat.");
		sqlBuilder.append(DbHelper.CATEGORIES_TITLE);
		sqlBuilder.append(" FROM ");
		sqlBuilder.append(DbHelper.TABLE_ARTICLES);
		sqlBuilder.append(" AS art INNER JOIN ");
		sqlBuilder.append(DbHelper.TABLE_CATEGORIES);
		sqlBuilder.append(" AS cat ON art.");
		sqlBuilder.append(DbHelper.ARTICLES_CATEGORY_ID);
		sqlBuilder.append(" = cat.");
		sqlBuilder.append(DbHelper.COLUMN_ID);
		sqlBuilder.append(" GROUP BY art.");
		sqlBuilder.append(DbHelper.ARTICLES_CATEGORY_ID);

		String sql = sqlBuilder.toString();
		return db.rawQuery(sql, null);
	}
}
