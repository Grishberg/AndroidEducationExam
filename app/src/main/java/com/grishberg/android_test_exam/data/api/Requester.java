package com.grishberg.android_test_exam.data.api;

import com.google.gson.Gson;
import com.grishberg.android_test_exam.data.api.request.DataRequest;
import com.grishberg.android_test_exam.data.api.response.DataResponse;

/**
 * Created by grigoriy on 16.06.15.
 */
public class Requester {
	private static final String TAG = "Requester";

	private static final String SERVER = "http://editors.yozhik.sibext.ru/";

	public Requester() {
	}

	public DataResponse getData(DataRequest request){
		RestClient restClient = new RestClient();
		String url = getDataUrl();
		ApiResponse response = restClient.doGet(url);

		Gson gson = new Gson();

//        Response loginResponse = null;
//        try {
//            loginResponse = gson.fromJson(response.getInputStreamReader(), Response.class);
//        } catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//
//        if(loginResponse == null){
//            return false;
//        }
//
//        TestApplication.getAppContext().getContentResolver().insert(...);

		return new DataResponse();
	}

	private String getCategoriesUrl(){
		return SERVER + "categories.json";
	}

	private String putArticleUrl(){
		return SERVER + "articles.json";
	}

	private String editArticleUrl(long id){
		return SERVER + String.format("articles/%d.json",id);
	}

	private String getDataUrl(){
		return SERVER + "data.json";
	}
}

