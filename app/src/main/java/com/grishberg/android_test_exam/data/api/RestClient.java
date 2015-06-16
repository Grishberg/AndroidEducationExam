package com.grishberg.android_test_exam.data.api;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by grigoriy on 16.06.15.
 */
public class RestClient {
	private static final String TAG = "RestClient";

	public RestClient() {
	}

	public ApiResponse doGet(String url) {
		return doGet(url, null);
	}

	public ApiResponse doGet(String url, ArrayList<Header> headers) {
		ApiResponse apiResponse = new ApiResponse();

		HttpClient httpClient = getHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(url);

		httpGet.setParams(getTimeOutParams());

		if (headers != null) {
			for (Header h : headers) {
				httpGet.addHeader(h);
			}
		}

		setDefaultHeaders(httpGet);

		HttpResponse response;
		try {
			response = httpClient.execute(httpGet, localContext);

			apiResponse.status = response.getStatusLine().getStatusCode();
			apiResponse.body = response.getEntity();
			Log.d(TAG, "doGet: " + url);
		} catch (Exception e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}

		return apiResponse;
	}

	public ApiResponse doPost(String url, List<NameValuePair> postParams, StringEntity stringEntity) {
		ApiResponse apiResponse = new ApiResponse();

		HttpClient httpClient = getHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);

		httpPost.setParams(getTimeOutParams());

		setDefaultHeaders(httpPost);

		try {
			if (postParams != null) {
				httpPost.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
			}
			if (stringEntity != null) {
				httpPost.setEntity(stringEntity);
			}

			HttpResponse response = httpClient.execute(httpPost, localContext);
			apiResponse.status = response.getStatusLine().getStatusCode();
			apiResponse.body = response.getEntity();
			Log.d(TAG, "doPost: " + url);
		} catch (final IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}

		return apiResponse;
	}

	public ApiResponse doPut(String url, StringEntity stringEntity) {
		ApiResponse apiResponse = new ApiResponse();

		HttpClient httpClient = getHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPut httpPut = new HttpPut(url);

		httpPut.setParams(getTimeOutParams());

		setDefaultHeaders(httpPut);

		try {
			if (stringEntity != null) {
				httpPut.setEntity(stringEntity);
			}

			HttpResponse response = httpClient.execute(httpPut, localContext);
			apiResponse.status = response.getStatusLine().getStatusCode();
			apiResponse.body = response.getEntity();
			Log.d(TAG, "doPut: " + url);
		} catch (final IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}

		return apiResponse;
	}

	public ApiResponse doDelete(String url) {
		ApiResponse apiResponse = new ApiResponse();
		HttpClient httpClient = getHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpDelete httpDelete = new HttpDelete(url);

		httpDelete.setParams(getTimeOutParams());

		setDefaultHeaders(httpDelete);

		HttpResponse response;
		try {
			response = httpClient.execute(httpDelete, localContext);

			apiResponse.status = response.getStatusLine().getStatusCode();
			apiResponse.body = response.getEntity();
			Log.d(TAG, "doDelete: " + url);
		} catch (Exception e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}

		return apiResponse;
	}

	private HttpClient getHttpClient() {
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Android");
		return httpClient;
	}

	private HttpParams getTimeOutParams() {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
		HttpConnectionParams.setSoTimeout(httpParams, 50000);
		return httpParams;
	}

	private void setDefaultHeaders(HttpRequestBase httpRequest) {
//        httpRequest.setHeader("Accept", "application/json");
//        httpRequest.setHeader("Accept-Encoding", "gzip");
//        httpRequest.setHeader("Accept-Language", language);
	}

}
