package com.grishberg.android_test_exam.ui.framework;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by G on 21.06.15.
 */
public class Utils {
	public static String getFileNameByUri(Context context, Uri uri) {
		String filePathUri = null;
		if (uri.getScheme().toString().compareTo("content") == 0) {
			Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
			if (cursor.moveToFirst()) {
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"
				filePathUri = cursor.getString(column_index);
			}
		}
		return filePathUri;
	}
}
