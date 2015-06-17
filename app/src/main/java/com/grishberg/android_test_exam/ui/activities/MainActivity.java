package com.grishberg.android_test_exam.ui.activities;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.grishberg.android_test_exam.R;
import com.grishberg.android_test_exam.ui.listeners.IActivityArticleInteractionListener;
import com.grishberg.android_test_exam.ui.listeners.IActivityTopicListInteractionListener;
import com.grishberg.android_test_exam.ui.listeners.IArticleFragmentInteractionListener;
import com.grishberg.android_test_exam.ui.listeners.ITopicListFragmentInteraction;


public class MainActivity extends Activity implements
		IArticleFragmentInteractionListener
	, ITopicListFragmentInteraction
{
	private IActivityArticleInteractionListener 	mArticleFragment;
	private IActivityTopicListInteractionListener	mTopiclistFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}


	@Override
	public void onRegister(IActivityArticleInteractionListener fragment) {
		mArticleFragment	= fragment;
	}

	@Override
	public void onUnregister(IActivityArticleInteractionListener fragment) {
		mArticleFragment	= null;
	}

	@Override
	public void onRegister(IActivityTopicListInteractionListener fragment) {
		mTopiclistFragment	= fragment;
	}

	@Override
	public void onUnregister(IActivityTopicListInteractionListener fragment) {
		mTopiclistFragment	= null;
	}

}
