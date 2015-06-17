package com.grishberg.android_test_exam.ui.listeners;

/**
 * Created by G on 16.06.15.
 */
public interface ITopicListFragmentInteraction {
	void onRegister(IActivityTopicListInteractionListener fragment);
	void onUnregister(IActivityTopicListInteractionListener fragment);
	void onItemClicked(long id);
	void onCreateNewArticle();
}
