package com.grishberg.android_test_exam.data.api;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.os.ResultReceiver;

import com.grishberg.android_test_exam.AppController;
import com.grishberg.android_test_exam.data.api.request.DataRequest;

/**
 * Created by grigoriy on 16.06.15.
 */
public class ApiServiceHelper {
	private static final String TAG = "ApiServiceHelper";
	private static ApiServiceHelper instance;

	public static ApiServiceHelper getInstance() {
		if (instance == null) {
			instance = new ApiServiceHelper();
		}
		return instance;
	}

	private ApiServiceHelper() {
		super();
	}

	public void getData(DataRequest data, ResultReceiver onServiceResult){
		startService(data, ApiService.ACTION_GET_DATA, onServiceResult);
	}

	 /*
    utils
     */

	private void startService(Parcelable data, int action, ResultReceiver onServiceResult) {
		Intent intent = getIntent(action, onServiceResult);
		if(data != null){
			intent.putExtra(ApiService.REQUEST_OBJECT_KEY, data);
		}
		getContext().startService(intent);
	}

	private Intent getIntent(int action, ResultReceiver onServiceResult) {
		final Intent i = new Intent(getContext(), ApiService.class);
		i.putExtra(ApiService.ACTION_KEY, action);
		i.putExtra(ApiService.CALLBACK_KEY, onServiceResult);
		return i;
	}

	private Context getContext(){
		return AppController.getAppContext();
	}
}