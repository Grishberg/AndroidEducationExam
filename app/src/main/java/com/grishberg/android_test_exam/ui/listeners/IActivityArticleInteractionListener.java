package com.grishberg.android_test_exam.ui.listeners;

/**
 * Created by G on 16.06.15.
 */
public interface IActivityArticleInteractionListener {
	void onOpenArticle(long id);
	void onCreateNewArticle();
	void onDeleteArticle(long id);
}
