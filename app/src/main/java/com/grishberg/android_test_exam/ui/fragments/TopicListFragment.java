package com.grishberg.android_test_exam.ui.fragments;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.LoaderManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import com.grishberg.android_test_exam.R;
import com.grishberg.android_test_exam.data.model.AppContentProvider;
import com.grishberg.android_test_exam.data.model.DbHelper;
import com.grishberg.android_test_exam.ui.adapters.EpxListViewCursorAdapter;
import com.grishberg.android_test_exam.ui.listeners.IActivityAdapterInteraction;
import com.grishberg.android_test_exam.ui.listeners.IActivityTopicListInteractionListener;
import com.grishberg.android_test_exam.ui.listeners.ITopicListFragmentInteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TopicListFragment extends BaseFragment
		implements IActivityTopicListInteractionListener
, LoaderManager.LoaderCallbacks<Cursor>
, AdapterView.OnItemSelectedListener
, CompoundButton.OnCheckedChangeListener
, IActivityAdapterInteraction {

	private static final String TAG = "TopicListFragment";
	public static final int LISTVIEW_MODE 			= 0;
	public static final int EXPLISTVIEW_MODE 		= 1;

	public static final int ARTICLES_LOADER			= 0;
	public static final int CATEGORIES_LOADER 		= 1;
	public static final int ARTICLES_CHILD_LOADER	= 2;

	private static final String ARGS_SELECTION 				= "argsSelection";
	private static final String ARGS_SELECTION_ARGUMENTS 	= "argsSelectionArguments";

	private static final String ARGS_ARTICLES_SELECTION 	= "argsArticlesSelection";
	private static final String ARGS_ARTICLES_SELECTION_ARGUMENTS 	= "argsArticlesSelectionArguments";

	private ListView 						mListView;
	private ExpandableListView 				mListViewEx;
	private ITopicListFragmentInteraction 	mListener;
	private SimpleCursorAdapter 			mListViewCursorAdapter;
	private EpxListViewCursorAdapter		mListViewExAdapter;

	private boolean 						mFilterOnlyMy;
	private boolean 						mFilterUnpublished;
	private String							mKeyword;

	// DB cursor settings
	String[] 								mProjection;
	String[]								mCategoryProjection;
	private String 							mArticlesSortOrder;
	private String 							mCategoriesSortOrder;
	private String							mChildArticlesSortOrder;
	private boolean							mFirstLaunch;

	public static TopicListFragment newInstance() {
		TopicListFragment fragment = new TopicListFragment();
		Bundle args = new Bundle();
		return fragment;
	}

	public TopicListFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view	= inflater.inflate(R.layout.fragment_topic_list, container, false);

		// init ListView
		mListView	= (ListView) view.findViewById(R.id.fragment_topiclist_listview);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onListViewItemClicked(id);
			}
		});
		registerForContextMenu(mListView);

		//init ExpandableListView
		mListViewEx	= (ExpandableListView) view.findViewById(R.id.fragment_topiclist_explistview);

		// refresh button
		view.findViewById(R.id.fragment_topiclist_button_refresh).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onRefresh();
			}
		});

		// switch filters
		( (Switch)view.findViewById(R.id.fragment_topiclist_switch_onlymy) ).setOnCheckedChangeListener(this);
		( (Switch)view.findViewById(R.id.fragment_topiclist_switch_unpublished) ).setOnCheckedChangeListener(this);

		// Spinner
		Spinner spinner	= (Spinner) view.findViewById(R.id.fragment_topiclist_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item
				, new String[] {getString(R.string.listview_caption), getString(R.string.listviewex_caption) });
		spinner.setAdapter(adapter);
		spinner.setSelection(0);
		spinner.setOnItemSelectedListener(this);

		// edittext filter
		EditText filterEdit	= (EditText)view.findViewById(R.id.fragment_topiclist_edittext_filter);
		filterEdit.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				filterTextChanged(s.toString());
			}
		});

		// button add
		view.findViewById(R.id.fragment_topiclist_add_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddNewArticle();
			}
		});
		// set projection
		mProjection				= new String[] {DbHelper.COLUMN_ID, DbHelper.ARTICLES_TITLE};
		mCategoryProjection		= new String[] {DbHelper.COLUMN_ID, DbHelper.CATEGORIES_TITLE};
		mArticlesSortOrder		= DbHelper.ARTICLES_UPDATED + " DESC ";
		mCategoriesSortOrder	= DbHelper.CATEGORIES_TITLE + " ASC ";
		mChildArticlesSortOrder	= DbHelper.ARTICLES_UPDATED	+ " DESC ";
		mFirstLaunch			= true;
		fillData();
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof ITopicListFragmentInteraction){

			mListener = (ITopicListFragmentInteraction) activity;
			mListener.onRegister(this);

		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	private void fillData() {
		//  1) load articles from db

		// Fields from the database (projection)
		// Must include the _id column for the adapter to work
		String[] from = new String[] {DbHelper.ARTICLES_TITLE};

		// Fields on the UI to which we map
		int[] to = new int[] { R.id.listview_cell_title };

		mListViewCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.topiclist_listview_cell
				, null, from, to, 0);
		mListView.setAdapter(mListViewCursorAdapter);

		//Expandable list adapter
		mListViewExAdapter	= new EpxListViewCursorAdapter(null, getActivity(), this);
		mListViewEx.setAdapter(mListViewExAdapter);
		getLoaderManager().initLoader(ARTICLES_LOADER, null, this);
		getLoaderManager().initLoader(CATEGORIES_LOADER, null, this);
		//	2) load articles from server
		getArticlesRequest(null, null);

	}

	/**
	 * refresh list
	 */
	private void onRefresh(){
		getArticlesRequest(null, null);
	}

	private void onAddNewArticle(){
		mListener.onCreateNewArticle();
	}

	/**
	 * event when user click on LV
	 * @param id
	 */
	private void onListViewItemClicked(long id){
		mListener.onItemClicked(id);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()){
			case R.id.fragment_topiclist_switch_onlymy:
				mFilterOnlyMy	= isChecked;
				break;

			case R.id.fragment_topiclist_switch_unpublished:
				mFilterUnpublished	= isChecked;
				break;
		}
		initFilter();
	}

	private void filterTextChanged(String keyword){
		mKeyword			= keyword;
		initFilter();
	}

	/**
	 * setup filter and init query from DB
	 */
	private void initFilter(){

		List<String> selectionArgs		= new ArrayList<>();
		StringBuilder filterSelection	= new StringBuilder();
		if(mFilterOnlyMy) {
			filterSelection.append(DbHelper.ARTICLES_OWN);
			filterSelection.append("= ? ");
			selectionArgs.add("1");
		}
		if(mFilterUnpublished) {
			if(filterSelection.length() > 0){
				filterSelection.append( " AND " );
			}
			filterSelection.append(DbHelper.ARTICLES_PUBLISHED);
			filterSelection.append("= ?");
			selectionArgs.add("1");
		}
		if(!TextUtils.isEmpty(mKeyword)) {
			if(filterSelection.length() > 0){
				filterSelection.append( " AND " );
			}
			filterSelection.append( DbHelper.ARTICLES_TITLE);
			filterSelection.append( " LIKE ?");
			selectionArgs.add( mKeyword + "%");

		}

		Bundle args = null;
		if(filterSelection.length() > 0) {
			args	= new Bundle();
			args.putString(ARGS_SELECTION, filterSelection.toString());
			args.putStringArray(ARGS_SELECTION_ARGUMENTS
					, selectionArgs.toArray(new String[selectionArgs.size()]));
		}

		getLoaderManager().restartLoader(ARTICLES_LOADER, args, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		if (id < ARTICLES_CHILD_LOADER) {
			switch (id) {
				case ARTICLES_LOADER:
					// filter items
					String selection = null;
					String[] selectionArgs = null;
					if (args != null) {
						selection = args.getString(ARGS_SELECTION);
						selectionArgs = args.getStringArray(ARGS_SELECTION_ARGUMENTS);
					}

					// Returns a new CursorLoader
					return new CursorLoader(
							getActivity(),   // Parent activity context
							AppContentProvider.CONTENT_URI_ARTICLES, // Table to query
							mProjection,     // Projection to return
							selection,            // No selection clause
							selectionArgs,            // No selection arguments
							mArticlesSortOrder             // Default sort order
					);
				case CATEGORIES_LOADER:
					// Returns a new CursorLoader
					return new CursorLoader(
							getActivity(),   // Parent activity context
							AppContentProvider.CONTENT_URI_CATEGORIES, // Table to query
							mCategoryProjection,     // Projection to return
							null,            // No selection clause
							null,            // No selection arguments
							mCategoriesSortOrder  // Default sort order
					);
				default:
					// An invalid id was passed in
					return null;
			}
		} else {
			// child loaders
			// filter items
			String selection = null;
			String[] selectionArgs = null;
			if (args != null) {
				selection = args.getString(ARGS_ARTICLES_SELECTION);
				selectionArgs = args.getStringArray(ARGS_ARTICLES_SELECTION_ARGUMENTS);
			}

			// Returns a new CursorLoader
			return new CursorLoader(
					getActivity(),   // Parent activity context
					AppContentProvider.CONTENT_URI_ARTICLES, // Table to query
					mProjection,     // Projection to return
					selection,            // No selection clause
					selectionArgs,            // No selection arguments
					mChildArticlesSortOrder             // Default sort order
			);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if(loader.getId() < ARTICLES_CHILD_LOADER) {
			switch (loader.getId()){
				case ARTICLES_LOADER:
					mListViewCursorAdapter.swapCursor(data);
					notifyArticlePanel(data);
					break;
				case CATEGORIES_LOADER:
					mListViewExAdapter.setGroupCursor(data);
					break;
			}
		} else {
			// child cursor
			Map<Long, Integer> groupMap	= mListViewExAdapter.getGroupMap();
			Integer groupIndex	= groupMap.get((long)(loader.getId() - ARTICLES_CHILD_LOADER));
			if(groupIndex != null) {
				mListViewExAdapter.setChildrenCursor(groupIndex, data);
			} else  {
				Log.d(TAG,"groupMap = null, id="+loader.getId());
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if(loader.getId() == ARTICLES_LOADER) {
			mListViewCursorAdapter.changeCursor(null);
		}

	}


	private void notifyArticlePanel(Cursor data){
		if(mFirstLaunch && mListener != null){
			if(data.moveToFirst()){
				mFirstLaunch = false;
				mListener.onItemClicked( data.getLong( data.getColumnIndex(DbHelper.COLUMN_ID )) );
			}
		}
	}

	// spinner listview type changed
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch (position){
			case LISTVIEW_MODE:
				mListView.setVisibility(View.VISIBLE);
				mListViewEx.setVisibility(View.GONE);
				break;

			case EXPLISTVIEW_MODE:
				mListView.setVisibility(View.GONE);
				mListViewEx.setVisibility(View.VISIBLE);
				break;
		}
		//TODO: save state of selected article(id)
	}

	//start or restart loader from adapter
	@Override
	public void getChildrenCursor(long categoryId) {
		Loader loader	= getLoaderManager().getLoader(ARTICLES_CHILD_LOADER+(int)categoryId);
		Bundle args		= new Bundle();
		args.putString(ARGS_ARTICLES_SELECTION , DbHelper.ARTICLES_CATEGORY_ID+" = ?");
		args.putStringArray(ARGS_ARTICLES_SELECTION_ARGUMENTS, new String[]{ String.format("%d",categoryId)})
		;
		if (loader != null && !loader.isReset()) {
			getLoaderManager().restartLoader(ARTICLES_CHILD_LOADER+(int)categoryId, args,
					this);
		} else {
			getLoaderManager().initLoader(ARTICLES_CHILD_LOADER+(int)categoryId, args,
					this);
		}
	}

	//------------------- context menu ----------------------
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.fragment_topiclist_listview) {
			MenuInflater inflater = getActivity().getMenuInflater();
			inflater.inflate(R.menu.menu_delete_listview, menu);

		} else if(v.getId() == R.id.fragment_topiclist_explistview) {

			MenuInflater inflater = getActivity().getMenuInflater();
			inflater.inflate(R.menu.menu_delete_listviewex, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_delete_listview:
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
						.getMenuInfo();
				deleteArticleRequest(info.id, new IResponseListener() {
					@Override
					public void onResponse(long id) {
						if (mListener != null && id > 0) {
							mListener.onDeleteArticle(id);
						}
					}
				}, null);
				return true;

			case R.id.action_delete_listviewex:
				// TODO: delete in expandable
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
	//--------------------------------------------------

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener.onUnregister(this);
		mListener = null;
	}

}
