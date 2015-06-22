package com.grishberg.android_test_exam.data.api;

import android.util.Log;
import android.util.Xml;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.CharsetUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by grigoriy on 16.06.15.
 */
public class RestClient {

	private static final String TAG = "RestClient";
	public static final String API_KEY = "157922e9c8c383f89a3a66d6735b02af";

	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary = "nnnVwRxmJMKcpkWwwwE-DWVRe4CQ0d";

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

	public ApiResponse doPost(String url, List<NameValuePair> postParams, String string) {
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

			StringEntity stringEntity	= null;
			stringEntity = new StringEntity(string, "UTF-8");
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

	public ApiResponse doPut(String url, String string) {
		ApiResponse apiResponse = new ApiResponse();

		HttpClient httpClient = getHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPut httpPut = new HttpPut(url);

		httpPut.setParams(getTimeOutParams());

		setDefaultHeaders(httpPut);

		try {
			StringEntity stringEntity	= null;
			stringEntity = new StringEntity(string, "UTF-8");
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



	public String doUploadFile(String urlString, File file, String fileParameterName) {
		HttpURLConnection conn				= null;
		DataOutputStream dos				= null;
		DataInputStream dis					= null;
		FileInputStream fileInputStream		= null;

		MultipartEntityBuilder entity 		= MultipartEntityBuilder.create();
		HttpEntity httpentity;
		String result						= null;

		try {
			entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				entity.setCharset(CharsetUtils.get("UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			entity.addPart(fileParameterName, new FileBody(file, ContentType.create("image/jpeg"), file.getName()));
			httpentity = entity.build();

			// ------------------ CLIENT REQUEST
			fileInputStream = new FileInputStream(file);

			// open a URL connection to the Servlet
			// Open a HTTP connection to the URL
			URL url	= new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			// Allow Inputs
			conn.setDoInput(true);
			// Allow Outputs
			conn.setDoOutput(true);
			// Don't use a cached copy.
			conn.setUseCaches(false);
			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Token token=" + API_KEY);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.addRequestProperty(httpentity.getContentType().getName(), httpentity.getContentType().getValue());
			dos = new DataOutputStream(conn.getOutputStream());
			httpentity.writeTo(dos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				if (fileInputStream != null){
					fileInputStream.close();
				}
				if (dos != null)
					dos.close();
			} catch (Exception e) {}
		}

		// ------------------ read the SERVER RESPONSE
		try {
			int responseCode		= conn.getResponseCode();
			StringBuilder response 	= new StringBuilder();
			dis = new DataInputStream(conn.getInputStream());

			String line;
			while ((line = dis.readLine()) != null) {
				response.append(line).append('\n');
			}

			System.out.println("Upload file responce:"
					+ response.toString());
			result					= response.toString();
		} catch (IOException e){
			e.printStackTrace();
		}
		finally {
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {}
			}
		}
		return result;
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
        httpRequest.setHeader("Authorization", "Token token="+API_KEY);
		httpRequest.setHeader("Content-Type", "application/json");
        httpRequest.setHeader("Accept-Encoding", "utf-8");
	}

	//TODO: make without MultipartBuilder
	public String doUploadFile0(String urlString, File file, String fileParameterName) {
		HttpURLConnection conn				= null;
		DataOutputStream dos				= null;
		DataInputStream dis					= null;
		FileInputStream fileInputStream		= null;
		HashMap<String, String> parameters	= new HashMap<>();
		StringBuilder  stringBuilder		= new StringBuilder();
		String result						= null;
		byte[] buffer;
		int maxBufferSize = 20 * 1024;
		try {
			// ------------------ CLIENT REQUEST
			fileInputStream = new FileInputStream(file);

			// open a URL connection to the Servlet
			// Open a HTTP connection to the URL
			URL url	= new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			// Allow Inputs
			conn.setDoInput(true);
			// Allow Outputs
			conn.setDoOutput(true);
			// Don't use a cached copy.
			conn.setUseCaches(false);
			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Token token=" + API_KEY);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary + "; charset=UTF-8");

			dos = new DataOutputStream(conn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\""
					+ fileParameterName + "\"; filename=\"" + file.getName()
					+ "\"" + lineEnd);
			dos.writeBytes("Content-Type:image/jpg" + lineEnd);
			dos.writeBytes(lineEnd);

			// create a buffer of maximum size
			buffer = new byte[Math.min((int) file.length(), maxBufferSize)];
			int length;
			// read file and write it into form...
			while ((length = fileInputStream.read(buffer)) != -1) {
				dos.write(buffer, 0, length);
			}

			for (String name : parameters.keySet()) {
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\""
						+ name + "\"" + lineEnd);
				dos.writeBytes(lineEnd);
				dos.writeBytes(parameters.get(name));
			}

			// send multipart form data necessary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				if (fileInputStream != null){
					fileInputStream.close();
				}
				if (dos != null)
					dos.close();
			} catch (Exception e) {}
		}

		// ------------------ read the SERVER RESPONSE
		try {
			int responseCode		= conn.getResponseCode();
			String responseMessage	= conn.getResponseMessage();
			conn.connect();
			dis = new DataInputStream(conn.getInputStream());
			StringBuilder response = new StringBuilder();

			String line;
			while ((line = dis.readLine()) != null) {
				response.append(line).append('\n');
			}

			System.out.println("Upload file responce:"
					+ response.toString());
			result	= response.toString();
		} catch (IOException e){
			e.printStackTrace();
		}
		finally {
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {}
			}
		}
		return result;
	}
}
