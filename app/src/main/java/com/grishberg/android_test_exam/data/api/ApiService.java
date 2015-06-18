package com.grishberg.android_test_exam.data.api;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

import com.grishberg.android_test_exam.data.api.request.DataRequest;
import com.grishberg.android_test_exam.data.api.response.DataResponse;

/**
 * Created by grigoriy on 16.06.15.
 */
public class ApiService extends IntentService {

	private static final String TAG = "ApiService";

	public static final String CALLBACK_KEY			= "CALLBACK_KEY";
	public static final String ACTION_KEY			= "ACTION_KEY";
	public static final String ERROR_KEY			= "ERROR_KEY";
	public static final String REQUEST_OBJECT_KEY	= "REQUEST_OBJECT_KEY";
	public static final String RESPONSE_OBJECT_KEY	= "RESPONSE_OBJECT_KEY";

	public static final int ACTION_GET_CATEGORIES	= 1;
	public static final int ACTION_GET_ARTICLES		= 2;
	public static final int ACTION_PUT_ARTICLE		= 3;



	private boolean 		destroyed;
	private ResultReceiver	receiver;

	public ApiService() {
		super(TAG);
	}

	/**
	 * execute queue task
	 * @param intent
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		receiver	= intent.getParcelableExtra(CALLBACK_KEY);
		int action	= intent.getIntExtra(ACTION_KEY, -1);
		// do process request with server
		Bundle data	= processIntent(intent, action);
		// return result
		sentMessage(action, data);
	}

	private Bundle processIntent(Intent intent, int  action){
		switch (action){
			case ACTION_GET_CATEGORIES:
				return getCategories((DataRequest) getRequestObject(intent));
			case ACTION_GET_ARTICLES:
				return getArticles((DataRequest) getRequestObject(intent));
			case ACTION_PUT_ARTICLE:
				return put((DataRequest) getRequestObject(intent));


		}
		return null;
	}

	private Bundle getCategories(DataRequest request){
		Requester requester = new Requester();
		Bundle bundle = new Bundle();
		DataResponse response = requester.getCategories(request);
		if(response == null){
			bundle.putBoolean(ERROR_KEY, true);
		} else {
			bundle.putSerializable(RESPONSE_OBJECT_KEY, response);
		}
		return bundle;
	}

	private Bundle getArticles(DataRequest request){
		Requester requester		= new Requester();
		Bundle bundle			= new Bundle();
		DataResponse response	= requester.getArticles(request);
		if(response == null){
			bundle.putBoolean(ERROR_KEY, true);
		} else {
			bundle.putSerializable(RESPONSE_OBJECT_KEY, response);
		}
		return bundle;
	}

	private Bundle putArticle(DataRequest request){
		Requester requester		= new Requester();
		Bundle bundle			= new Bundle();
		DataResponse response	= requester.putArticle(request);
		if(response == null){
			bundle.putBoolean(ERROR_KEY, true);
		} else {
			bundle.putSerializable(RESPONSE_OBJECT_KEY, response);
		}
		return bundle;
	}


	private Bundle getData(DataRequest request){
		Requester requester = new Requester();
		Bundle bundle = new Bundle();
		DataResponse response = requester.getArticles(request);
		if(response == null){
			bundle.putBoolean(ERROR_KEY, true);
		} else {
			bundle.putSerializable(RESPONSE_OBJECT_KEY, response);
		}
		return bundle;
	}



	private Parcelable getRequestObject(Intent intent){
		return intent.getParcelableExtra(REQUEST_OBJECT_KEY);
	}

	private void sentMessage(int code, Bundle data){
		if(!destroyed && receiver != null){
			receiver.send(code, data);
		}
	}

	@Override
	public void onCreate() {
		destroyed = false;
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		destroyed = true;
		receiver = null;
		Log.d(TAG, "ResourceLoadService: onDestroy");
		super.onDestroy();
	}
}
