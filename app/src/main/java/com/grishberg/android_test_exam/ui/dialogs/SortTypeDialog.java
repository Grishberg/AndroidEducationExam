package com.grishberg.android_test_exam.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.content.DialogInterface.OnClickListener;
import android.view.ViewGroup;

import com.grishberg.android_test_exam.R;
import com.grishberg.android_test_exam.data.model.DbHelper;

/**
 * Created by grigoriy on 24.06.15.
 */
public class SortTypeDialog extends DialogFragment implements OnClickListener {

	final String TAG = "SortTypeDialog";
	public static final String PARAM_SORT_TYPE = "paramSortType";
	public static final String SORT_TYPE_TITLE 		= " UPPER ("+DbHelper.ARTICLES_TITLE + ") ASC ";;
	public static final String SORT_TYPE_ID 		= DbHelper.COLUMN_ID + " ASC ";;
	public static final String SORT_TYPE_UPDATED 	= DbHelper.ARTICLES_UPDATED + " DESC ";;


	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
				.setTitle(getString(R.string.sort_dialog_title))
				.setPositiveButton(R.string.sort_dialog_title_button_text, this)
				.setNegativeButton(R.string.sort_dialog_id_button_text, this)
				.setNeutralButton(R.string.sort_dialog_updated_button_text, this)
				.setMessage(R.string.sort_dialog_message);
		return adb.create();
	}

	public void onClick(DialogInterface dialog, int which) {
		String sortType = SORT_TYPE_UPDATED;
		switch (which) {
			case Dialog.BUTTON_POSITIVE:
				sortType = SORT_TYPE_TITLE;
				break;
			case Dialog.BUTTON_NEGATIVE:
				sortType = SORT_TYPE_ID;
				break;
			case Dialog.BUTTON_NEUTRAL:
				sortType = SORT_TYPE_UPDATED;
				break;
		}
		//отправляем результат обратно
		Intent intent = new Intent();
		intent.putExtra(PARAM_SORT_TYPE, sortType);
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
	}

	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
	}

	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
	}
}
