package com.grishberg.android_test_exam.data.api;

import android.app.DownloadManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grishberg.android_test_exam.AppController;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by G on 19.06.15.
 */
public class VolleyRestClient<T> {
	private static final int VOLLEY_SYNC_TIMEOUT	= 60;

	public enum RequestAction {ACTION_GET
		, ACTION_POST
		, ACTION_PUT
		, ACTION_DELETE
		};
	private static final String TAG = "VolleyRestClient";
	// user id = 3
	public static final String API_KEY = "157922e9c8c383f89a3a66d6735b02af";

	public VolleyRestClient(){

	}

	public T request(RequestAction action, String requestUrl, final String requestBody, Class<T> clazz){
		T result = null;

		int requestMethod	= Request.Method.GET;
		switch (action){
			case ACTION_POST:
				requestMethod	= Request.Method.POST;
				break;

			case ACTION_PUT:
				requestMethod	= Request.Method.PUT;
				break;

			case ACTION_DELETE:
				requestMethod	= Request.Method.DELETE;
				break;
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("Content-Type", "application/json");
		params.put("Authorization", "Token token=" + API_KEY);

		RequestFuture<T> future = RequestFuture.newFuture();
		RequestQueue queue = Volley.newRequestQueue(AppController.getAppContext());
		GsonRequest<T> request = new GsonRequest<T>(requestMethod, requestUrl, requestBody,clazz
				, params, future, future);
		queue.add(request);
		try
		{
			// синхронное извлечение тела страницы
			//String response = futureRequest.get(VOLLEY_SYNC_TIMEOUT, TimeUnit.SECONDS);
			result = future.get(VOLLEY_SYNC_TIMEOUT, TimeUnit.SECONDS);
			// парсинг полученной строки
			Log.d(TAG,"received");
			//Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();
			//result = gson.fromJson(response.toString(), clazz);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return result;
	}
}
