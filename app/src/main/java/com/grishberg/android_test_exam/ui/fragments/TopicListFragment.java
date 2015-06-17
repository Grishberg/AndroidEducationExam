package com.grishberg.android_test_exam.ui.fragments;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.app.LoaderManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import com.grishberg.android_test_exam.R;
import com.grishberg.android_test_exam.ui.adapters.EpxListViewCursorAdapter;
import com.grishberg.android_test_exam.ui.adapters.ListViewCursorAdapter;
import com.grishberg.android_test_exam.ui.listeners.IActivityTopicListInteractionListener;
import com.grishberg.android_test_exam.ui.listeners.ITopicListFragmentInteraction;

public class TopicListFragment extends Fragment implements IActivityTopicListInteractionListener
, LoaderManager.LoaderCallbacks<Cursor>
, AdapterView.OnItemSelectedListener
, View.OnClickListener
, CompoundButton.OnCheckedChangeListener{

	public static final int CONTACTS_LOADER = 0;
	private ListView mListView;
	private ExpandableListView mListViewEx;
	private ITopicListFragmentInteraction mListener;
	private ListViewCursorAdapter mListViewCursorAdapter;
	private boolean mFilterOnlyMy;
	private boolean mFilterUnpublished;

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
		View view = inflater.inflate(R.layout.fragment_topic_list, container, false);

		mListView	= (ListView) view.findViewById(R.id.fragment_topiclist_listview);
		mListViewEx	= (ExpandableListView) view.findViewById(R.id.fragment_topiclist_explistview);

		// refresh button
		view.findViewById(R.id.fragment_topiclist_button_refresh).setOnClickListener( this );
		( (Switch)view.findViewById(R.id.fragment_topiclist_switch_onlymy) ).setOnCheckedChangeListener(this);
		// Spinner
		Spinner spinner	= (Spinner) view.findViewById(R.id.fragment_topiclist_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item
				, new String[] {getString(R.string.listview_caption), getString(R.string.listviewex_caption) });
		spinner.setAdapter(adapter);
		spinner.setSelection(0);
		spinner.setOnItemSelectedListener(this);

		// edittext
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
		mListViewCursorAdapter	= new ListViewCursorAdapter();
		getActivity().getLoaderManager().initLoader(DATA_LOADER, null, this);
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

	private void onRefresh(){
		getActivity().getLoaderManager().getLoader(DATA_LOADER).forceLoad();
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
			case URL_LOADER:
				// Returns a new CursorLoader
				return new CursorLoader(
						getActivity(),   // Parent activity context
						mDataUrl,        // Table to query
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
		mListViewCursorAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener.onUnregister();
		mListener = null;
	}

}
