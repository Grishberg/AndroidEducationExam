package com.grishberg.android_test_exam.data.api;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * Created by grigoriy on 16.06.15.
 */
public class ApiResponse {
	private static final String CHARSET 	= "UTF-8";
	private static final int BUFFER_SIZE	= 1024;
	private InputStream mInputSream;
	private int status;

	public ApiResponse() {
		this(0,null);
	}

	public ApiResponse(int status, InputStream inputStream) {
		this.status			= status;
		this.mInputSream	= inputStream;
	}

	public String getAsText(){
		final char[] buffer = new char[BUFFER_SIZE];
		final StringBuilder out = new StringBuilder();
		try (Reader in = new InputStreamReader(mInputSream, CHARSET)) {
			for (;;) {
				int rsz = in.read(buffer, 0, buffer.length);
				if (rsz < 0)
					break;
				out.append(buffer, 0, rsz);
			}
		}
		catch (UnsupportedEncodingException ex) {
		}
		catch (IOException ex) {
		}
		return out.toString();

	}

	public InputStreamReader getInputStreamReader() {
		if(mInputSream == null){
			return null;
		}
		return  new InputStreamReader(mInputSream);
	}

	public int getStatus() {
		return status;
	}
}

