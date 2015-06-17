package com.grishberg.android_test_exam.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by G on 16.06.15.
 */
public class ListViewCursorAdapter extends CursorAdapter {

	public ListViewCursorAdapter(Context context){
		super(context,null);
	}
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return null;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

	}
}
