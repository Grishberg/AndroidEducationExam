package com.grishberg.android_test_exam.data.api;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grishberg.android_test_exam.AppController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by G on 19.06.15.
 */
public class VolleyRestClient<T> {
	private static final int VOLLEY_SYNC_TIMEOUT	= 10;
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

	public T request(String requestUrl, final String requestBody, Class<T> clazz){
		T result = null;
		RequestQueue queue			= Volley.newRequestQueue(AppController.getAppContext());
		RequestFuture<String> futureRequest = RequestFuture.newFuture();
		StringRequest getRequest	= new StringRequest(Request.Method.GET
				, requestUrl
				, futureRequest, futureRequest){
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				return super.getHeaders();
			}
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {

				Map<String, String> map = new HashMap<String, String>();
				map.put("body", requestBody);
				return map;
			}

		};
		queue.add(getRequest);

		try
		{
			// синхронное извлечение тела страницы
			String response = futureRequest.get(VOLLEY_SYNC_TIMEOUT, TimeUnit.SECONDS);
			// парсинг полученной строки
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();
			result = gson.fromJson(response, clazz);

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
