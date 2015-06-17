package com.grishberg.android_test_exam.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;

/**
 * Created by G on 16.06.15.
 */
public class EpxListViewCursorAdapter extends CursorTreeAdapter {

	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		return null;
	}

	@Override
	protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
		return null;
	}

	@Override
	protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {

	}

	@Override
	protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
		return null;
	}

	@Override
	protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {

	}
}
