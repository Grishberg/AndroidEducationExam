package com.grishberg.android_test_exam.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grishberg.android_test_exam.AppController;
import com.grishberg.android_test_exam.R;
import com.grishberg.android_test_exam.data.api.ApiService;
import com.grishberg.android_test_exam.data.api.ApiServiceHelper;
import com.grishberg.android_test_exam.data.api.request.DataRequest;
import com.grishberg.android_test_exam.data.api.response.DataResponse;
import com.grishberg.android_test_exam.data.containers.Article;
import com.grishberg.android_test_exam.data.containers.ArticleRequestContainer;
import com.grishberg.android_test_exam.data.containers.Category;
import com.grishberg.android_test_exam.data.model.AppContentProvider;
import com.grishberg.android_test_exam.data.model.DbHelper;
import com.grishberg.android_test_exam.ui.listeners.IActivityArticleInteractionListener;
import com.grishberg.android_test_exam.ui.listeners.IArticleFragmentInteractionListener;

import java.util.ArrayList;
import java.util.Date;

public class ArticlesFragment extends Fragment implements IActivityArticleInteractionListener
		, LoaderManager.LoaderCallbacks<Cursor>{

	private static final int GET_CURRENT_ARTICLE_LOADER = 1;
	private static final int GET_CATEGORIES_LOADER 		= 2;

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
	String[] mArticlesProjection;
	String[] mCategoriesProjection;

	// controls
	private EditText 	mTitleEdit;
	private EditText 	mDescriptionEdit;
	private Button		mViewButton;
	private Button		mEditButton;
	private Button		mSaveButton;
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
		mCategories			= new ArrayList<>();
		mTitleEdit			= (EditText) view.findViewById(R.id.fragment_article_edit_title);
		mDescriptionEdit	= (EditText) view.findViewById(R.id.fragment_article_edit_description);

		mSpinner			= (Spinner) view.findViewById(R.id.fragment_article_category_spinner);
		mSpinner.setEnabled(false);

		mIsPublishedSwitch	= (Switch)	view.findViewById(R.id.fragment_article_publish_switch);

		// buttons
		mViewButton			= (Button)	view.findViewById(R.id.fragment_article_view_button);
		mViewButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onViewModeButtonClicked();
			}
		});

		mEditButton			= (Button)	view.findViewById(R.id.fragment_article_edit_button);
		mEditButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onEditModeButtonClicked();
			}
		});

		// restore fields if need
		if(savedInstanceState != null){
			mCategories = savedInstanceState.getParcelableArrayList(STATE_CATEGORIES);
			onCategoriesReceived(savedInstanceState.getInt(STATE_ARTICLE_CATEGORY));
			mArticleId	= savedInstanceState.getLong(STATE_ARTICLE_ID);
			mTitleEdit.setText(savedInstanceState.getString(STATE_ARTICLE_TITLE));
			mDescriptionEdit.setText(savedInstanceState.getString(STATE_ARTICLE_DESCRIPTION));

		} else {
			getCategories();
		}

		mSaveButton	= (Button) view.findViewById(R.id.fragment_article_save_button);
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onSavePressed();
			}
		});

		// set projection
		mArticlesProjection = new String[] {DbHelper.COLUMN_ID
				, DbHelper.ARTICLES_CATEGORY_ID
				, DbHelper.ARTICLES_TITLE
				, DbHelper.ARTICLES_DESCRIPTION
				, DbHelper.ARTICLES_PHOTO_URL
				, DbHelper.ARTICLES_PUBLISHED
				, DbHelper.ARTICLES_CREATED
				, DbHelper.ARTICLES_UPDATED
				, DbHelper.ARTICLES_OWN
		};

		mCategoriesProjection = new String[] {DbHelper.COLUMN_ID
				, DbHelper.CATEGORIES_TITLE
		};

		return view;
	}
	//TODO: get from server
	private void getCategories(){

		ApiServiceHelper.getInstance().getCategories(new DataRequest(), new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (resultData.containsKey(ApiService.ERROR_KEY)) {
					getCategoriesFromDb();
				} else {
					DataResponse response = (DataResponse) resultData
							.getSerializable(ApiService.RESPONSE_OBJECT_KEY);
					// categories received from server, get it from content provider
					getCategoriesFromDb();
				}
			}
		});
	}

	/**
	 * fill categories array from cursor
	 * @param cursor
	 */
	private void addCategoriesFromCursor(Cursor cursor){

		cursor.moveToFirst();
		while (cursor.moveToNext()) {
			Category category	= Category.fromCursor(cursor);
			mCategories.add(category);
		}
		cursor.close();
		onCategoriesReceived(0);
	}

	private void getCategoriesFromDb() {
		getLoaderManager().restartLoader(GET_CATEGORIES_LOADER, null, this);
	}

	private void onCategoriesReceived(int initCategoryIndex){
		// fill spinner with categories
		ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(getActivity()
				, android.R.layout.simple_spinner_item
				, mCategories.toArray(new Category[mCategories.size()]));
		mSpinner.setAdapter(adapter);
		mSpinner.setSelection(initCategoryIndex);
	}

	private void onViewModeButtonClicked(){
		mTitleEdit.setEnabled(false);
		mDescriptionEdit.setEnabled(false);
		mSaveButton.setVisibility(View.INVISIBLE);
		mIsPublishedSwitch.setEnabled(false);
		mViewButton.setEnabled(false);
		mSpinner.setEnabled(false);
	}

	private void onEditModeButtonClicked(){
		mTitleEdit.setEnabled(true);
		mDescriptionEdit.setEnabled(true
		);
		mSaveButton.setVisibility(View.VISIBLE);
		mIsPublishedSwitch.setEnabled(true);
		mEditButton.setEnabled(false);
		mSpinner.setEnabled(true);
	}

	/**
	 * save article
	 */
	private void onSavePressed(){
		// 1) save to server
		// 2) get result's idFromServer
		// 3) save to DB
		String title		=  mTitleEdit.getText().toString();
		String description	= mDescriptionEdit.getText().toString();
		long updated		= (new Date()).getTime();
		long categoryId		= mCategories.get(mSpinner.getSelectedItemPosition()).getId();
		if (mCreatedDate == 0) mCreatedDate = updated;

		if (description.length() == 0 || title.length() == 0) {
			return;
		}

		Article article 	= new Article(-1,title, description, "", true, categoryId
				, 0,0,true);

		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String jsonContent	= gson.toJson(new ArticleRequestContainer(article));
		if( mArticleUri == null) {
			// add new article
			ApiServiceHelper.getInstance().putArticle(new DataRequest(jsonContent)
					, new ResultReceiver(new Handler()) {
				@Override
				protected void onReceiveResult(int resultCode, Bundle resultData) {
					if (resultData.containsKey(ApiService.ERROR_KEY)) {
					} else {
						DataResponse response = (DataResponse) resultData
								.getSerializable(ApiService.RESPONSE_OBJECT_KEY);
					}
				}
			});
		} else {
			// edit my article
			ApiServiceHelper.getInstance().editArticle(new DataRequest(mArticleUri.getLastPathSegment()
					, jsonContent), new ResultReceiver(new Handler()) {
				@Override
				protected void onReceiveResult(int resultCode, Bundle resultData) {
					if (resultData.containsKey(ApiService.ERROR_KEY)) {
						getCategoriesFromDb();
					} else {
						DataResponse response = (DataResponse) resultData
								.getSerializable(ApiService.RESPONSE_OBJECT_KEY);
						// update in db
					}
				}
			});

		}
	}

	// user create new article
	@Override
	public void onCreateNewArticle() {
		// set defaults values
		mArticleUri	= null;
		mTitleEdit.setText("");
		mDescriptionEdit.setText("");
		mSpinner.setSelection(0);
		mIsPublishedSwitch.setChecked(false);
		onEditModeButtonClicked();

	}

	@Override
	public void onOpenArticle(long id) {
		// open article data from cursorLoader
		Bundle args	= new Bundle();
		args.putLong(PARAM_ARTICLE_ID,id);
		getLoaderManager().restartLoader(GET_CURRENT_ARTICLE_LOADER, args, this);
	}

	/**
	 * get data from cursor and put to UI
	 * @param cursor
	 */
	private void fillUiWithData(Cursor cursor){
		cursor.moveToFirst();
		Article currentArticle	= Article.fromCursor(cursor);

		mTitleEdit.setText(currentArticle.getTitle());
		mDescriptionEdit.setText(currentArticle.getDescription());
		mIsPublishedSwitch.setChecked(currentArticle.isPublished());

		int spinnerSelection = getCategoryIndexById(currentArticle.getCategoryId());
		if( spinnerSelection >= 0){
			mSpinner.setSelection(spinnerSelection);
		}

		// can we edit article?
		mEditButton.setEnabled(currentArticle.getIsMine());
		//mViewButton.setEnabled(currentArticle.getIsMine());
		// always close the cursor
		cursor.close();
	}

	private int getCategoryIndexById(long id){
		for(int i = 0; i < mCategories.size(); i++){
			if(mCategories.get(i).getId() == id){
				return i;
			}
		}
		return -1;
	}

	//------ Cursor Loader ------
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		switch (id) {
			case GET_CURRENT_ARTICLE_LOADER:
				// Returns a new CursorLoader
				long articleId			= args.getLong(PARAM_ARTICLE_ID);
				return new CursorLoader(
						getActivity(),   // Parent activity context
						AppContentProvider.getArticlesUri(articleId), // Table to query
						mArticlesProjection,     // Projection to return
						null,            // No selection clause
						null,            // No selection arguments
						null             // Default sort order
				);
			case GET_CATEGORIES_LOADER:
				// Returns a new CursorLoader
				return new CursorLoader(
						getActivity(),   // Parent activity context
						AppContentProvider.CONTENT_URI_CATEGORIES, // Table to query
						mCategoriesProjection,     // Projection to return
						null,            // No selection clause
						null,            // No selection arguments
						null             // Default sort order
				);
			default:
				// An invalid id was passed in
				return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		switch (loader.getId() ){

			case GET_CURRENT_ARTICLE_LOADER:
				if (data != null){
					fillUiWithData(data);
				}
				break;

			case GET_CATEGORIES_LOADER:
				if( data != null) {
					addCategoriesFromCursor(data);
				}
				break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

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
