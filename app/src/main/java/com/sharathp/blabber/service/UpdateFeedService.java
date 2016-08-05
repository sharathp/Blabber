package com.sharathp.blabber.service;

import android.app.IntentService;
import android.content.Intent;


public class UpdateFeedService extends IntentService {


    public UpdateFeedService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        ShopTalkApplication.from(this).getComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

    }

}