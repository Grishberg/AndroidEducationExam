package com.grishberg.android_test_exam;

import android.app.Application;
import android.content.Context;

/**
 * Created by grigoriy on 16.06.15.
 */
public class AppController extends Application {
	private static AppController instance;
	private static Context appContext;

	@Override
	public void onCreate() {
		super.onCreate();
		appContext = getApplicationContext();
		instance = this;
	}

	public AppController getInstance() {
		return instance;
	}

	public static Context getAppContext(){
		return appContext;
	}
}
