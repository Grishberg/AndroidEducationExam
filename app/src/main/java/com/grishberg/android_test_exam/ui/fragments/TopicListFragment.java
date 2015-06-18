package com.grishberg.android_test_exam.ui.fragments;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.app.LoaderManager;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.grishberg.android_test_exam.data.api.ApiService;
import com.grishberg.android_test_exam.data.api.ApiServiceHelper;
import com.grishberg.android_test_exam.data.api.request.DataRequest;
import com.grishberg.android_test_exam.data.api.response.DataResponse;
import com.grishberg.android_test_exam.data.model.AppContentProvider;
import com.grishberg.android_test_exam.data.model.DbHelper;
import com.grishberg.android_test_exam.ui.adapters.EpxListViewCursorAdapter;
import com.grishberg.android_test_exam.ui.adapters.ListViewCursorAdapter;
import com.grishberg.android_test_exam.ui.listeners.IActivityTopicListInteractionListener;
import com.grishberg.android_test_exam.ui.listeners.ITopicListFragmentInteraction;

public class TopicListFragment extends Fragment implements IActivityTopicListInteractionListener
, LoaderManager.LoaderCallbacks<Cursor>
, AdapterView.OnItemSelectedListener
, CompoundButton.OnCheckedChangeListener{

	public static final int LISTVIEW_MODE = 0;
	public static final int EXPLISTVIEW_MODE = 1;

	public static final int ARTICLES_LOADER = 0;
	private ListView mListView;
	private ExpandableListView mListViewEx;
	private ITopicListFragmentInteraction mListener;
	private SimpleCursorAdapter mListViewCursorAdapter;
	private boolean mFilterOnlyMy;
	private boolean mFilterUnpublished;
	String[] mProjection;

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
		mProjection = new String[] {DbHelper.COLUMN_ID, DbHelper.ARTICLES_TITLE};
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

		getLoaderManager().initLoader(ARTICLES_LOADER, null, this);
		mListViewCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.topiclist_listview_cell
				, null, from, to, 0);
		mListView.setAdapter(mListViewCursorAdapter);

		//	2) load articles from server
		ApiServiceHelper.getInstance().getArticles(new DataRequest(), new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (resultData.containsKey(ApiService.ERROR_KEY)) {

				} else {
					DataResponse response = (DataResponse) resultData
							.getSerializable(ApiService.RESPONSE_OBJECT_KEY);
					// categories received from server, get it from content provider
				}
			}
		});
	}

	/**
	 * refresh list
	 */
	private void onRefresh(){
		getActivity().getLoaderManager().initLoader(ARTICLES_LOADER, null, this).forceLoad();
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
		//TODO: init filter
	}

	private void filterTextChanged(String keyword){
		//TODO: init filter
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		switch (id) {
			case ARTICLES_LOADER:
				// Returns a new CursorLoader
				return new CursorLoader(
						getActivity(),   // Parent activity context
						AppContentProvider.CONTENT_URI_ARTICLES, // Table to query
						mProjection,     // Projection to return
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
		if(loader.getId() == ARTICLES_LOADER) {
			mListViewCursorAdapter.swapCursor(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if(loader.getId() == ARTICLES_LOADER) {
			mListViewCursorAdapter.changeCursor(null);
		}

	}

	/**
	 * new article was created, need to refresh
	 * @param id
	 */
	@Override
	public void onCreatedNewArticle(long id) {
		//TODO: refresh list
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
		//TODO: save
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
				Uri uri = AppContentProvider.getArticlesUri( info.id );
				getActivity().getContentResolver().delete(uri, null, null);
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
