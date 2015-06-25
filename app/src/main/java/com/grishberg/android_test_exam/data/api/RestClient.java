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

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by grigoriy on 16.06.15.
 */
public class RestClient {

	private static final String TAG 	= "RestClient";
	public static final String 	API_KEY = "157922e9c8c383f89a3a66d6735b02af";
	private static final String CHARSET = "UTF-8";

	public RestClient() {
	}

	public ApiResponse doGet(String url) {
		return doGet(url, null);
	}

	public ApiResponse doGet(String url, Map<String, String> headers) {
		ApiResponse apiResponse = new ApiResponse();
		try {
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoInput(true);

			setDefaultHeaders(connection);
			if (headers != null) {
				for (Map.Entry<String, String> currentHeader : headers.entrySet()) {
					connection.setRequestProperty(currentHeader.getKey(), currentHeader.getValue());
				}
			}
			int responseCode = connection.getResponseCode();
			apiResponse = new ApiResponse(responseCode,connection.getInputStream());
			Log.d(TAG, "doGet: " + url);
		} catch (Exception e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}

		return apiResponse;
	}

	public ApiResponse doPost(String url,  Map<String,Object> postParams, String string) {
		ApiResponse apiResponse = new ApiResponse();

		try {
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
			//TODO: set timeout
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			setDefaultHeaders(connection);

			byte[] postDataBytes = null;
			StringBuilder postData = new StringBuilder();
			if(postParams != null) {
				for (Map.Entry<String, Object> param : postParams.entrySet()) {
					if (postData.length() != 0) postData.append('&');
					postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
					postData.append('=');
					postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
				}
				postDataBytes = postData.toString().getBytes("UTF-8");
				connection.getOutputStream().write(postDataBytes);
			}

			postDataBytes	= string.getBytes("UTF-8");
			connection.getOutputStream().write(postDataBytes);

			int responseCode = connection.getResponseCode();
			apiResponse = new ApiResponse(responseCode,connection.getInputStream());

			Log.d(TAG, "doPost: " + url);
		} catch (final IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}

		return apiResponse;
	}

	public ApiResponse doPut(String url, String string) {
		ApiResponse apiResponse = new ApiResponse();

		try {
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
			//TODO: set timeout
			connection.setDoOutput(true);
			connection.setRequestMethod("PUT");
			setDefaultHeaders(connection);

			byte[] postDataBytes = string.getBytes("UTF-8");
			connection.getOutputStream().write(postDataBytes);

			int responseCode = connection.getResponseCode();
			apiResponse = new ApiResponse(responseCode,connection.getInputStream());

			Log.d(TAG, "doPut: " + url);
		} catch (final IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}

		return apiResponse;
	}

	public ApiResponse doDelete(String url) {
		ApiResponse apiResponse = new ApiResponse();

		try {
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
			//TODO: set timeout
			connection.setDoOutput(true);
			connection.setRequestMethod("DELETE");
			setDefaultHeaders(connection);

			int responseCode = connection.getResponseCode();
			apiResponse = new ApiResponse(responseCode,connection.getInputStream());

			Log.d(TAG, "doDelete: " + url);
		} catch (final IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}

		return apiResponse;
	}


	public String doUploadFile(String requestURL, File uploadFile1, String fileParameterName) {
		String result = null;
		try {

			Map<String,String> headers = new HashMap<>();
			headers.put("Authorization", "Token token=" + API_KEY);
			headers.put("Content-Type", "application/json");
			MultipartUtility multipart = new MultipartUtility(requestURL, CHARSET, headers);


			multipart.addFilePart(fileParameterName, uploadFile1);
			List<String> response = multipart.finish();

			StringBuilder responseBuffer = new StringBuilder();

			for (String line : response) {
				responseBuffer.append(line);
			}
			result = responseBuffer.toString();
		} catch (IOException ex) {
			System.err.println(ex);
		}
		return result;
	}

	private void setDefaultHeaders(HttpURLConnection httpRequest) {
        httpRequest.setRequestProperty("Authorization", "Token token=" + API_KEY);
		httpRequest.setRequestProperty("Content-Type", "application/json");
        httpRequest.setRequestProperty("Accept-Encoding", "utf-8");
	}


}
