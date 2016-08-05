package com.sharathp.blabber;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.facebook.stetho.Stetho;
import com.sharathp.blabber.di.ApplicationComponent;
import com.sharathp.blabber.di.DaggerApplicationComponent;
import com.sharathp.blabber.di.modules.ApplicationModule;

public class BlabberApplication extends Application {
	private ApplicationComponent mComponent;

	@Override
	public void onCreate() {
		super.onCreate();

		initDependencyInjection();
		initStetho();
	}

	private void initDependencyInjection() {
		mComponent = DaggerApplicationComponent.builder()
				.applicationModule(new ApplicationModule(this))
				.build();
	}

	private void initStetho() {
		Stetho.initializeWithDefaults(this);
	}

	public ApplicationComponent getComponent() {
		return mComponent;
	}

	public static BlabberApplication from(@NonNull final Context context) {
		return (BlabberApplication) context.getApplicationContext();
	}
}