package com.grishberg.android_test_exam.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grishberg.android_test_exam.data.api.ApiService;
import com.grishberg.android_test_exam.data.api.ApiServiceHelper;
import com.grishberg.android_test_exam.data.api.request.DataRequest;
import com.grishberg.android_test_exam.data.api.request.DeleteDataRequest;
import com.grishberg.android_test_exam.data.api.response.DataResponse;
import com.grishberg.android_test_exam.data.containers.Article;

/**
 * Created by grigoriy on 19.06.15.
 */
public abstract class BaseFragment extends Fragment {

	protected interface IErrorListener{
		void onError();
	}

	protected interface IResponseListener{
		void onResponse(long id);
	}

	public void getArticlesRequest(final IResponseListener	responseListener
			, final IErrorListener		errorListener){
		ApiServiceHelper.getInstance().getArticles(new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (resultData.containsKey(ApiService.ERROR_KEY)) {
					if (errorListener != null) {
						errorListener.onError();
					}
				} else {
					if (responseListener != null) {
						responseListener.onResponse(0L);
					}
				}
			}
		});
	}

	public void addArticleRequest(Article article, String imagePath
			, final IResponseListener	responseListener
			, final IErrorListener		errorListener) {

		ApiServiceHelper.getInstance().addArticle(new DataRequest(article, imagePath)
				, new ResultReceiver(new Handler()) {

			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (resultData.containsKey(ApiService.ERROR_KEY)) {
					if (errorListener != null) {
						errorListener.onError();
					}
				} else {

					DataResponse response = (DataResponse) resultData
							.getParcelable(ApiService.RESPONSE_OBJECT_KEY);
					if (responseListener != null) {
						responseListener.onResponse(response.getId());
					}
				}
			}
		});
	}

	public void editArticleRequest(Article article, String imagePath
			, final IResponseListener	responseListener
			, final IErrorListener		errorListener){

		ApiServiceHelper.getInstance().editArticle(new DataRequest(article, imagePath), new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (resultData.containsKey(ApiService.ERROR_KEY)) {
					if (errorListener != null) {
						errorListener.onError();
					}
				} else {
					DataResponse response = (DataResponse) resultData
							.getParcelable(ApiService.RESPONSE_OBJECT_KEY);
					if (responseListener != null) {
						responseListener.onResponse(response.getId());
					}
				}
			}
		});
	}

	public void deleteArticleRequest(long id
			, final IResponseListener	responseListener
			, final IErrorListener		errorListener){

		ApiServiceHelper.getInstance().deleteArticle(new DeleteDataRequest(id)
				, new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (resultData.containsKey(ApiService.ERROR_KEY)) {
					if (errorListener != null) {
						errorListener.onError();
					}
				} else {
					if (responseListener != null) {
						responseListener.onResponse(0L);
					}
				}
			}
		});
	}

	public void getCategoriesRequest(final IResponseListener	responseListener
			, final IErrorListener		errorListener){

		ApiServiceHelper.getInstance().getCategories(new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (resultData.containsKey(ApiService.ERROR_KEY)) {
					if (errorListener != null) {
						errorListener.onError();
					}
				} else {
					if (responseListener != null) {
						responseListener.onResponse(0L);
					}
				}
			}
		});
	}

}
