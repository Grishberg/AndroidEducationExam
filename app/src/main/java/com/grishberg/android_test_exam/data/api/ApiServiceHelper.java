package com.grishberg.android_test_exam.data.api;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.os.ResultReceiver;

import com.grishberg.android_test_exam.AppController;
import com.grishberg.android_test_exam.data.api.request.DataRequest;
import com.grishberg.android_test_exam.data.api.request.DeleteDataRequest;

import java.io.Serializable;

/**
 * Created by grigoriy on 16.06.15.
 */
public class ApiServiceHelper {
	private static final String TAG = "ApiServiceHelper";


	private ApiServiceHelper() {
		super();
	}

	public static  void getCategories( ResultReceiver onServiceResult){
		startService(null, ApiService.ACTION_GET_CATEGORIES, onServiceResult);
	}

	public static void getArticles( ResultReceiver onServiceResult){
		startService(null, ApiService.ACTION_GET_ARTICLES, onServiceResult);
	}

	public static void addArticle(DataRequest data, ResultReceiver onServiceResult){
		startService(data, ApiService.ACTION_ADD_ARTICLE, onServiceResult);
	}

	public static void editArticle(DataRequest data, ResultReceiver onServiceResult){
		startService(data, ApiService.ACTION_EDIT_ARTICLE, onServiceResult);
	}

	public static void deleteArticle(DeleteDataRequest data, ResultReceiver onServiceResult){
		startService(data, ApiService.ACTION_DELETE_ARTICLE, onServiceResult);
	}

	 /*
    utils
     */

	private static void startService(Parcelable data, int action, ResultReceiver onServiceResult) {
		Intent intent = getIntent(action, onServiceResult);
		if(data != null){
			intent.putExtra(ApiService.REQUEST_OBJECT_KEY, data);
		}
		getContext().startService(intent);
	}

	private static Intent getIntent(int action, ResultReceiver onServiceResult) {
		final Intent i = new Intent(getContext(), ApiService.class);
		i.putExtra(ApiService.ACTION_KEY, action);
		i.putExtra(ApiService.CALLBACK_KEY, onServiceResult);
		return i;
	}

	private static Context getContext(){
		return AppController.getAppContext();
	}
}
