package com.grishberg.android_test_exam.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.TextView;

import com.grishberg.android_test_exam.R;
import com.grishberg.android_test_exam.data.model.DbHelper;
import com.grishberg.android_test_exam.ui.listeners.IActivityAdapterInteraction;

import java.util.HashMap;

/**
 * Created by G on 16.06.15.
 */
public class EpxListViewCursorAdapter extends CursorTreeAdapter {

	protected final HashMap<Integer, Integer> mGroupMap;
	private LayoutInflater mInflater;
	private IActivityAdapterInteraction mListener;

	public EpxListViewCursorAdapter(Cursor cursor,Context context, IActivityAdapterInteraction listener){
		super(cursor, context);
		mInflater	= LayoutInflater.from(context);
		mGroupMap	= new HashMap<Integer, Integer>();
		mListener	= listener;
	}
	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		// Given the group, we return a cursor for all the children within that
		// group
		int groupPos = groupCursor.getPosition();
		int groupId = groupCursor.getInt(groupCursor
				.getColumnIndex(DbHelper.COLUMN_ID));

		mGroupMap.put(groupId, groupPos);

		if(mListener != null ){
			mListener.getChildrenCursor(groupId);
		}

		return null;
	}

	@Override
	protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.topiclist_explistview_group, parent, false);
		return view;
	}

	@Override
	protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
		TextView titleTextView = (TextView) view
				.findViewById(R.id.explistview_cell_title);

		if (titleTextView != null) {
			titleTextView.setText(cursor.getString(cursor
					.getColumnIndex(DbHelper.CATEGORIES_TITLE)));
		}
	}

	@Override
	protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.topiclist_explistview_cell, parent, false);

		return view;
	}

	@Override
	protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {

	}
}
