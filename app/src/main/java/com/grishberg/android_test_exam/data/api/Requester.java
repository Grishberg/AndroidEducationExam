package com.grishberg.android_test_exam.data.api;

import android.content.ContentValues;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grishberg.android_test_exam.AppController;
import com.grishberg.android_test_exam.data.api.request.DataRequest;
import com.grishberg.android_test_exam.data.api.response.DataResponse;
import com.grishberg.android_test_exam.data.containers.Article;
import com.grishberg.android_test_exam.data.containers.Category;
import com.grishberg.android_test_exam.data.model.AppContentProvider;

import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * Created by grigoriy on 16.06.15.
 */
public class Requester {
	private static final String TAG = "Requester";

	private static final String SERVER = "http://editors.yozhik.sibext.ru/";

	public Requester() {
	}

	public DataResponse getCategories(DataRequest request) {
		CategoriesContainer categoriesResponse = null;
		//TODO:start volley
		RestClient restClient = new RestClient();
		String url = getCategoriesUrl();
		ApiResponse response = restClient.doGet(url);
		Gson gson = new Gson();

		if(response != null){
			try {
				categoriesResponse = gson.fromJson(response.getInputStreamReader(), CategoriesContainer.class);
			} catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}

		if(categoriesResponse != null){
			for (Category category: categoriesResponse.categories){
				AppController.getAppContext().getContentResolver()
						.insert(AppContentProvider.CONTENT_URI_CATEGORIES, category.buildContentValues());
			}
		}
		return new DataResponse();
	}


	public DataResponse getArticles(DataRequest request) {
		ArticlesContainer articlesContainer = null;
		//TODO:start volley
		RestClient restClient = new RestClient();
		String url = getArticlesUrl();
		ApiResponse response = restClient.doGet(url);
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();

		if(response != null){
			try {
				articlesContainer = gson.fromJson(response.getInputStreamReader()
						, ArticlesContainer.class);
			} catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}

		if(articlesContainer != null){
			for (Article category: articlesContainer.articles){
				AppController.getAppContext().getContentResolver()
						.insert(AppContentProvider.CONTENT_URI_ARTICLES, category.buildContentValues());
			}
		}
		return new DataResponse();
	}

	public DataResponse putArticle(DataRequest request) {
		long id	= -1;
		ArticleContainer articleContainer = null;
		//TODO:start volley
		RestClient restClient = new RestClient();
		String url = putArticleUrl();
		//ArrayList<NameValuePair> params = new ArrayList<>();
		//params.add(new BasicNameValuePair("body", request));
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();
		StringEntity stringEntity	= null;
		try
		{
			stringEntity = new StringEntity(request.getData());
		} catch (Exception e) {

		}
		ApiResponse response = restClient.doPost(url, null, stringEntity);

		if(response != null){
			try {
				String res = response.getAsText();

//				articleContainer = gson.fromJson(response.getInputStreamReader()
//						, ArticleContainer.class);
			} catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}

		if(articleContainer != null){
			//TODO: insert into db
			id	= articleContainer.article.getId();
			Log.d(TAG, "article succesful sended");
		}

		return new DataResponse(id);
	}


	public DataResponse editArticle(DataRequest request) {
		ArticleContainer articleContainer = null;
		//TODO:start volley
		RestClient restClient = new RestClient();
		String url = editArticleUrl(request.getId());
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();
		StringEntity stringEntity	= null;
		try {
			stringEntity = new StringEntity(request.getData());
		} catch (Exception e) { }
		ApiResponse response = restClient.doPut(url, stringEntity);

		if(response != null){
			try {
				articleContainer = gson.fromJson(response.getInputStreamReader()
						, ArticleContainer.class);
			} catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}

		if(articleContainer != null){
			//TODO update in db
			Log.d(TAG, "article succesful edited");
		}

		return new DataResponse();
	}
	private String getCategoriesUrl(){
		return SERVER + "categories.json";
	}

	private String getArticlesUrl(){
		return SERVER + "articles.json";
	}

	private String putArticleUrl(){
		return SERVER + "articles.json";
	}

	private String editArticleUrl(String id){
		return SERVER + String.format("articles/%s.json",id);
	}


	private static  class CategoriesContainer{
		public Category[] categories;
	}

	private static  class ArticlesContainer{
		public Article[] articles;
	}
	private static  class ArticleContainer{
		public Article article;
	}
}

