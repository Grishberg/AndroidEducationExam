package com.grishberg.android_test_exam.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.grishberg.android_test_exam.data.api.ApiService;
import com.grishberg.android_test_exam.data.api.ApiServiceHelper;
import com.grishberg.android_test_exam.data.api.request.DataRequest;
import com.grishberg.android_test_exam.data.api.response.DataResponse;

/**
 * Created by grigoriy on 19.06.15.
 */
public abstract class BaseFragment extends Fragment {

	protected interface IErrorListener{
		void onError();
	}
	protected interface IResponseListener{
		void onResponse();
	}

	public void getArticlesRequest(final IResponseListener	responseListener
			, final IErrorListener		errorListener){
		ApiServiceHelper.getInstance().getArticles(new DataRequest(), new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (resultData.containsKey(ApiService.ERROR_KEY)) {
					if(errorListener != null){
						errorListener.onError();
					}
				} else {
					if(responseListener != null){
						responseListener.onResponse();
					}
				}
			}
		});
	}

	public void putArticleRequest(String body
			, final IResponseListener	responseListener
			, final IErrorListener		errorListener) {

		ApiServiceHelper.getInstance().putArticle(new DataRequest(body)
				, new ResultReceiver(new Handler()) {

			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (resultData.containsKey(ApiService.ERROR_KEY)) {
					if(errorListener != null){
						errorListener.onError();
					}
				} else {

					DataResponse response = (DataResponse) resultData
							.getParcelable(ApiService.RESPONSE_OBJECT_KEY);
					if(responseListener!= null){
						responseListener.onResponse();
					}
				}
			}
		});
	}

	public void editArticleRequest(long id, String body
			, final IResponseListener	responseListener
			, final IErrorListener		errorListener){
		ApiServiceHelper.getInstance().editArticle(new DataRequest(id
				, body), new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (resultData.containsKey(ApiService.ERROR_KEY)) {
					if(errorListener != null){
						errorListener.onError();
					}
				} else {
					if(responseListener != null){
						responseListener.onResponse();
					}
				}
			}
		});
	}

	public void deleteArticleRequest(long id
			, final IResponseListener	responseListener
			, final IErrorListener		errorListener){


	}

	public void getCategoriesRequest(final IResponseListener	responseListener
			, final IErrorListener		errorListener){

		ApiServiceHelper.getInstance().getCategories(new DataRequest(), new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (resultData.containsKey(ApiService.ERROR_KEY)) {
					if(errorListener != null){
						errorListener.onError();
					}
				} else {
					if(responseListener != null){
						responseListener.onResponse();
					}
				}
			}
		});
	}



}
