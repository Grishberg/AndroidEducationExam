package com.grishberg.android_test_exam.ui.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.grishberg.android_test_exam.AppController;
import com.grishberg.android_test_exam.R;
import com.grishberg.android_test_exam.data.containers.Article;
import com.grishberg.android_test_exam.data.containers.Category;
import com.grishberg.android_test_exam.data.model.AppContentProvider;
import com.grishberg.android_test_exam.data.model.DbHelper;
import com.grishberg.android_test_exam.ui.adapters.ListViewCursorAdapter;
import com.grishberg.android_test_exam.ui.listeners.IActivityArticleInteractionListener;
import com.grishberg.android_test_exam.ui.listeners.IArticleFragmentInteractionListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArticlesFragment extends Fragment implements IActivityArticleInteractionListener
		, AdapterView.OnItemClickListener{

	private static final String PARAM_ARTICLE_ID = "paramArticleId";

	private static final String STATE_ARTICLE_ID			= "stateArticleId";
	private static final String STATE_ARTICLE_TITLE			= "stateArticleTilte";
	private static final String STATE_ARTICLE_DESCRIPTION	= "stateArticleDescription";
	private static final String STATE_ARTICLE_CATEGORY		= "stateArticleCategory";
	private static final String STATE_ARTICLE_PUBLISHED		= "stateArticlePublished";
	private static final String STATE_CATEGORIES			= "stateCategories";


	private IArticleFragmentInteractionListener mListener;
	private boolean mIsEditMode;
	private long mArticleId;
	private long mCreatedDate;
	private ArrayList<Category> mCategories;
	private Uri mArticleUri;

	// controls
	private EditText 	mTitleEdit;
	private EditText 	mDescriptionEdit;
	private Spinner		mSpinner;
	private Switch		mIsPublishedSwitch;

	public static ArticlesFragment newInstance(long articleId) {
		ArticlesFragment fragment = new ArticlesFragment();
		Bundle args	= new Bundle();
		args.putLong(PARAM_ARTICLE_ID, articleId);
		fragment.setArguments(args);
		return fragment;
	}

	public ArticlesFragment() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if( activity instanceof  IArticleFragmentInteractionListener){
			mListener = (IArticleFragmentInteractionListener) activity;
			mListener.onRegister(this);
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null){
			mArticleId = getArguments().getLong(PARAM_ARTICLE_ID);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view			= inflater.inflate(R.layout.fragment_articles, container, false);

		mTitleEdit			= (EditText) view.findViewById(R.id.fragment_article_edit_title);
		mDescriptionEdit	= (EditText) view.findViewById(R.id.fragment_article_edit_description);

		mSpinner			= (Spinner) view.findViewById(R.id.fragment_article_category_spinner);
		mIsPublishedSwitch	= (Switch)	view.findViewById(R.id.fragment_article_publish_switch);

		// restore fields if need
		if(savedInstanceState != null){
			ArrayList<Category> categories = savedInstanceState.getParcelableArrayList(STATE_CATEGORIES);

			onCategoriesReceived(categories,
					savedInstanceState.getInt(STATE_ARTICLE_CATEGORY));
			mArticleId	= savedInstanceState.getLong(STATE_ARTICLE_ID);
			mTitleEdit.setText(savedInstanceState.getString(STATE_ARTICLE_TITLE));
			mDescriptionEdit.setText(savedInstanceState.getString(STATE_ARTICLE_DESCRIPTION));

		} else {
			getCategories();
		}

		view.findViewById(R.id.fragment_article_save_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onSavePressed();
			}
		});

		return view;
	}
	//TODO: get from server
	private void getCategories(){
		ArrayList<Category> categories = new ArrayList<>(3);
		categories.add(new Category(1, "Best"));
		categories.add(new Category(2, "Fast"));
		categories.add(new Category(3, "Cool"));
		onCategoriesReceived(categories,0);
	}

	private void onCategoriesReceived(ArrayList<Category> categories, int initCategoryIndex){
		mCategories	= categories;
		// fill spinner with categories
		ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(getActivity()
				, android.R.layout.simple_spinner_item
				, mCategories.toArray(new Category[mCategories.size()]));
		mSpinner.setAdapter(adapter);
		mSpinner.setSelection(initCategoryIndex);
	}

	/**
	 * save article
	 */
	private void onSavePressed(){
		// 1) save to DB
		String title		=  mTitleEdit.getText().toString();
		String description	= mDescriptionEdit.getText().toString();
		long updated		= (new Date()).getTime();
		long categoryId		= mCategories.get(mSpinner.getSelectedItemPosition()).getId();
		if (mCreatedDate == 0) mCreatedDate = updated;
		boolean isPublished	= mIsPublishedSwitch.isChecked();
		// only save if title or description
		// is available

		if (description.length() == 0 || title.length() == 0) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(DbHelper.ARTICLES_TITLE, 		title);
		values.put(DbHelper.ARTICLES_DESCRIPTION, 	description);
		values.put(DbHelper.ARTICLES_CATEGORY_ID, 	categoryId);
		values.put(DbHelper.ARTICLES_CREATED, 		mCreatedDate);
		values.put(DbHelper.ARTICLES_UPDATED, 		updated);
		values.put(DbHelper.ARTICLES_PUBLISHED, 	isPublished);


		if (mArticleUri == null) {
			// New todo
			mArticleUri = AppController.getAppContext().getContentResolver()
					.insert(AppContentProvider.CONTENT_URI_ARTICLES, values);
		} else {
			// Update todo
			AppController.getAppContext().getContentResolver()
					.update(mArticleUri, values, null, null);
		}

		// 2) send to server
	}

	@Override
	public void onOpenArticle(long id) {
		// open article data from cursorLoader
	}

	// spinner category changed
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(STATE_CATEGORIES, mCategories);
		outState.putString(STATE_ARTICLE_TITLE, mTitleEdit.getText().toString());
		outState.putString(STATE_ARTICLE_DESCRIPTION, mDescriptionEdit.getText().toString());
		outState.putInt(STATE_ARTICLE_CATEGORY
				, mSpinner.getSelectedItemPosition());
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener.onUnregister(this);
		mListener = null;
	}

}
