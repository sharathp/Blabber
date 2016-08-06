package com.sharathp.blabber.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sharathp.blabber.BlabberApplication;
import com.sharathp.blabber.events.TweetsPastRetrievedEvent;
import com.sharathp.blabber.events.TweetsRefreshedEvent;
import com.sharathp.blabber.models.Tweet;
import com.sharathp.blabber.models.User;
import com.sharathp.blabber.repositories.TwitterDAO;
import com.sharathp.blabber.repositories.rest.TwitterClient;
import com.sharathp.blabber.repositories.rest.resources.TweetResource;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import cz.msebera.android.httpclient.Header;


public class UpdateTimelineService extends Service {
    private static final String TAG = UpdateTimelineService.class.getSimpleName();
    private static final String EXTRA_UPDATE_TYPE = UpdateTimelineService.class.getName() + ".UPDATE_TYPE";

    @IntDef({UPDATE_TYPE_LATEST, UPDATE_TYPE_PAST})
    private @interface UpdateType {}

    private static final int UPDATE_TYPE_LATEST = 1;
    private static final int UPDATE_TYPE_PAST = 2;

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;

    @Inject
    EventBus mEventBus;

    @Inject
    TwitterDAO mTwitterDAO;

    @Inject
    TwitterClient mTwitterClient;

    @Inject
    Gson mGson;

    public static Intent createIntentForLatestItems(final Context context) {
        return createIntent(context, UPDATE_TYPE_LATEST);
    }

    public static Intent createIntentForPastItems(final Context context) {
        return createIntent(context, UPDATE_TYPE_PAST);
    }

    // from and to are exclusive
    private static Intent createIntent(final Context context, @UpdateType final int updateType) {
        final Intent intent = new Intent(context, UpdateTimelineService.class);
        intent.putExtra(EXTRA_UPDATE_TYPE, updateType);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BlabberApplication.from(this).getComponent().inject(this);

        // An Android handler thread internally operates on a looper.
        mHandlerThread = new HandlerThread("UpdateTimelineService.HandlerThread");
        mHandlerThread.start();
        // An Android service handler is a handler running on a specific background thread.
        mServiceHandler = new ServiceHandler(mHandlerThread.getLooper());
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        final int updateType = intent.getIntExtra(EXTRA_UPDATE_TYPE, -1);

        final Message message = Message.obtain();
        message.what = updateType;
        mServiceHandler.sendMessage(message);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cleanup service before destruction
        mHandlerThread.quit();
    }

    final class ServiceHandler extends Handler {
        ServiceHandler(final Looper looper) {
            super(looper);
        }

        // Define how to handle any incoming messages here
        @Override
        public void handleMessage(final Message message) {
            final int updateType = message.what;

            switch (updateType) {
                case UPDATE_TYPE_LATEST: {
                    retrieveLatestTweets();
                    break;
                }
                case UPDATE_TYPE_PAST: {
                    retrievePastTweets();
                    break;
                }
                default: {
                    Log.w(TAG, "Invalid value for: " + EXTRA_UPDATE_TYPE + ": " + updateType);
                }
            }
        }

        private void retrievePastTweets() {
            final Tweet tweet = mTwitterDAO.getEarliestTweet();
            Long maxId = null;
            if (tweet != null) {
                maxId = tweet.getId();
            }
            mTwitterClient.getPastTweets(maxId, getPastTweetsResponseHandler());
        }

        private void retrieveLatestTweets() {
            final Tweet tweet = mTwitterDAO.getLatestTweet();
            Long sinceId = null;
            if (tweet != null) {
                sinceId = tweet.getId();
            }
            mTwitterClient.getLatestTweets(sinceId, getRefreshTweetsResponseHandler());
        }

        private boolean saveTweets(final List<TweetResource> tweetResources) {
            final List<Tweet> tweets = new ArrayList<>();
            for (final TweetResource tweetResource: tweetResources) {
                tweets.add(tweetResource.convertToTweet());
            }
            return mTwitterDAO.checkAndInsertTweets(tweets);
        }

        private boolean saveUsers(final Collection<TweetResource> tweetResources) {
            // this will filter the unique users
            final Map<Long, User> userMap = new HashMap<>();
            for (final TweetResource tweetResource: tweetResources) {
                userMap.put(tweetResource.getUser().getId(), tweetResource.getUser().convertToUser());
            }
            return mTwitterDAO.checkAndInsertUsers(userMap.values());
        }

        private AsyncHttpResponseHandler getRefreshTweetsResponseHandler() {
            return new TextHttpResponseHandler() {
                @Override
                public void onFailure(final int statusCode, final Header[] headers,
                                      final String responseString, final Throwable throwable) {
                    Log.e(TAG, "Error retrieving tweets: " + responseString, throwable);
                    final TweetsRefreshedEvent tweetsRefreshedEvent = new TweetsRefreshedEvent(0, false);
                    mEventBus.post(tweetsRefreshedEvent);
                }

                @Override
                public void onSuccess(final int statusCode, final Header[] headers,
                                      final String responseString) {
                    final Type responseType = new TypeToken<List<TweetResource>>(){}.getType();
                    final List<TweetResource> tweetResources = mGson.fromJson(responseString, responseType);
                    Log.i(TAG, "Retrieved tweets: " + tweetResources.size());

                    boolean success = saveUsers(tweetResources);
                    if (success) {
                        success = saveTweets(tweetResources);
                    }

                    if (! success) {
                        Log.i(TAG, "Unable to save tweets");
                    }

                    final TweetsRefreshedEvent tweetsRefreshedEvent = new TweetsRefreshedEvent(tweetResources.size(), success);
                    mEventBus.post(tweetsRefreshedEvent);
                }
            };
        }

        private AsyncHttpResponseHandler getPastTweetsResponseHandler() {
            return new TextHttpResponseHandler() {
                @Override
                public void onFailure(final int statusCode, final Header[] headers,
                                      final String responseString, final Throwable throwable) {
                    Log.e(TAG, "Error retrieving tweets: " + responseString, throwable);
                    final TweetsPastRetrievedEvent tweetsPastRetrievedEvent = new TweetsPastRetrievedEvent(0, false);
                    mEventBus.post(tweetsPastRetrievedEvent);
                }

                @Override
                public void onSuccess(final int statusCode, final Header[] headers,
                                      final String responseString) {
                    final Type responseType = new TypeToken<List<TweetResource>>(){}.getType();
                    final List<TweetResource> tweetResources = mGson.fromJson(responseString, responseType);
                    Log.i(TAG, "Retrieved tweets: " + tweetResources.size());

                    boolean success = saveUsers(tweetResources);
                    if (success) {
                        success = saveTweets(tweetResources);
                    }

                    if (! success) {
                        Log.i(TAG, "Unable to save tweets");
                    }

                    final TweetsPastRetrievedEvent tweetsPastRetrievedEvent = new TweetsPastRetrievedEvent(tweetResources.size(), success);
                    mEventBus.post(tweetsPastRetrievedEvent);
                }
            };
        }
    }
}