package com.grishberg.android_test_exam.data.api;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by grigoriy on 16.06.15.
 */
public class ApiResponse {
	public HttpEntity body;
	public int status;

	public ApiResponse() {}

	public String getAsText(){
		String text = "";
		if (body != null) {
			try {
				text = EntityUtils.toString(body, HTTP.UTF_8);
				body.consumeContent();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return text;
	}

	public InputStreamReader getInputStreamReader() {
		if(body == null){
			return null;
		}
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(body.getContent());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputStreamReader;
	}
}

