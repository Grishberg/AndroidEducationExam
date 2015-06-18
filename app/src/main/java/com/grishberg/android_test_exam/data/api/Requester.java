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
		ArticleContainer articleContainer = null;
		//TODO:start volley
		RestClient restClient = new RestClient();
		String url = putArticleUrl();
		ArrayList<NameValuePair> params = new ArrayList<>();
		ApiResponse response = restClient.doPost(url,new ArrayList<NameValuePair>());

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
			Log.d(TAG, "article succesful sended");
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

	private String editArticleUrl(long id){
		return SERVER + String.format("articles/%d.json",id);
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

