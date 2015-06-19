package com.grishberg.android_test_exam.data.containers;

import com.google.gson.annotations.Expose;

/**
 * Created by G on 18.06.15.
 */
public class ArticleRequestContainer {
	@Expose
	private Article article;
	public ArticleRequestContainer(Article article){
		this.article	= article;
	}
}
