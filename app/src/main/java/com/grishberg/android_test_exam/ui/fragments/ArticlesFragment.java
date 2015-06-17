package com.grishberg.android_test_exam.ui.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grishberg.android_test_exam.R;
import com.grishberg.android_test_exam.ui.listeners.IActivityArticleInteractionListener;
import com.grishberg.android_test_exam.ui.listeners.IArticleFragmentInteractionListener;

public class ArticlesFragment extends Fragment implements IActivityArticleInteractionListener{

	private IArticleFragmentInteractionListener mListener;

	public static ArticlesFragment newInstance(String param1, String param2) {
		ArticlesFragment fragment = new ArticlesFragment();
		return fragment;
	}

	public ArticlesFragment() {
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
		return inflater.inflate(R.layout.fragment_articles, container, false);
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
	public void onOpenArticle(long id) {
		// open article data from cursorLoader
	}

	@Override
	public void onCreateArticle() {

	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener.onUnregister(this);
		mListener = null;
	}

}
