package com.sharathp.blabber.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.IntDef;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sharathp.blabber.BlabberApplication;
import com.sharathp.blabber.events.StatusSubmittedEvent;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import cz.msebera.android.httpclient.Header;


public class UpdateTimelineService extends BaseService {
    private static final String TAG = UpdateTimelineService.class.getSimpleName();
    private static final String EXTRA_OPERATION_TYPE = UpdateTimelineService.class.getName() + ".OPERATION_TYPE";
    private static final String EXTRA_STATUS = UpdateTimelineService.class.getName() + ".EXTRA_STATUS";
    private static final String EXTRA_IN_REPLY_TO_STATUS_ID = UpdateTimelineService.class.getName() + ".IN_REPLY_TO_STATUS_ID";

    @IntDef({OP_REFRESH_LATEST, OP_REFRESH_PAST, OP_DELETE_EXISTING_AND_REFRESH,
            OP_TWEET, OP_TWEET_FAVORITE, OP_TWEET_UNFAVORITE})
    private @interface OperationType {}

    private static final int OP_REFRESH_LATEST = 1;
    private static final int OP_REFRESH_PAST = 2;
    private static final int OP_DELETE_EXISTING_AND_REFRESH = 3;
    private static final int OP_TWEET = 4;
    private static final int OP_TWEET_FAVORITE = 5;
    private static final int OP_TWEET_UNFAVORITE = 6;

    @Inject
    EventBus mEventBus;

    @Inject
    TwitterDAO mTwitterDAO;

    @Inject
    TwitterClient mTwitterClient;

    @Inject
    Gson mGson;

    public static Intent createIntentForLatestItems(final Context context) {
        return createIntent(context, OP_REFRESH_LATEST);
    }

    public static Intent createIntentForPastItems(final Context context) {
        return createIntent(context, OP_REFRESH_PAST);
    }

    public static Intent createIntentForDeleteExistingItemsAndRetrieveLasterItems(final Context context) {
        return createIntent(context, OP_DELETE_EXISTING_AND_REFRESH);
    }

    public static Intent createIntentForTweeting(final Context context, final String status, final Long inReplyToStatusId) {
        final Intent intent = createIntent(context, OP_TWEET);
        intent.putExtra(EXTRA_STATUS, status);
        if (inReplyToStatusId != null) {
            intent.putExtra(EXTRA_IN_REPLY_TO_STATUS_ID, inReplyToStatusId);
        }
        return intent;
    }

    // from and to are exclusive
    private static Intent createIntent(final Context context, @OperationType final int operationType) {
        final Intent intent = new Intent(context, UpdateTimelineService.class);
        intent.putExtra(EXTRA_OPERATION_TYPE, operationType);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BlabberApplication.from(this).getComponent().inject(this);
    }

    @Override
    protected void doHandleMessage(final Message message) {
        final Bundle intentData = message.getData();
        final int operationType = intentData.getInt(EXTRA_OPERATION_TYPE, -1);

        switch (operationType) {
            case OP_REFRESH_LATEST: {
                Log.i(TAG, "Retrieved Latest tweets");
                retrieveLatestTweets(null);
                break;
            }
            case OP_REFRESH_PAST: {
                Log.i(TAG, "Retrieved past tweets");
                retrievePastTweets();
                break;
            }
            case OP_DELETE_EXISTING_AND_REFRESH: {
                Log.i(TAG, "Deleting and retrieving latest tweets");
                deleteExistingData();
                retrieveLatestTweets(null);
                break;
            }
            case OP_TWEET: {
                Log.i(TAG, "Tweeting/Retweeting");
                final String status = intentData.getString(EXTRA_STATUS);
                Long inReplyToStatusId = intentData.getLong(EXTRA_IN_REPLY_TO_STATUS_ID, -1);
                if (inReplyToStatusId == -1) {
                    inReplyToStatusId = null;
                }
                tweet(status, inReplyToStatusId);
                break;
            }
            default: {
                Log.w(TAG, "Invalid value for: " + EXTRA_OPERATION_TYPE + ": " + operationType);
            }
        }
    }

    private void tweet(final String status, final Long inReplyToStatusId) {
        mTwitterClient.submitTweet(status, inReplyToStatusId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(final int statusCode, final Header[] headers, final String responseString, final Throwable throwable) {
                mEventBus.post(new StatusSubmittedEvent(status, false));
            }

            @Override
            public void onSuccess(final int statusCode, final Header[] headers, final String responseString) {
                retrieveLatestTweets(new StatusSubmittedEvent(status, true));
            }
        });
    }

    private void retrievePastTweets() {
        final Tweet tweet = mTwitterDAO.getEarliestTweet();
        Long maxId = null;
        if (tweet != null) {
            maxId = tweet.getId();
        }
        mTwitterClient.getPastTweets(maxId, getPastTweetsResponseHandler());
    }

    private void retrieveLatestTweets(final Object additionalEventToPostOnceComplete) {
        final Tweet tweet = mTwitterDAO.getLatestTweet();
        Long sinceId = null;
        if (tweet != null) {
            sinceId = tweet.getId();
        }
        mTwitterClient.getLatestTweets(sinceId, getRefreshTweetsResponseHandler(additionalEventToPostOnceComplete));
    }

    private boolean saveTweets(final List<TweetResource> tweetResources) {
        final List<Tweet> tweets = new ArrayList<>();
        for (final TweetResource tweetResource : tweetResources) {
            tweets.add(tweetResource.convertToTweet());
        }
        return mTwitterDAO.checkAndInsertTweets(tweets);
    }

    private boolean saveUsers(final List<TweetResource> tweetResources) {
        // this will filter the unique users
        final Map<Long, User> userMap = new HashMap<>();
        for (final TweetResource tweetResource : tweetResources) {
            userMap.put(tweetResource.getUser().getId(), tweetResource.getUser().convertToUser());
        }
        return mTwitterDAO.checkAndInsertUsers(userMap.values());
    }

    private void deleteExistingData() {
        mTwitterDAO.deleteExistingTweets();
    }

    private boolean saveTweetsAndUsers(final List<TweetResource> tweetResources) {
        boolean success = true;

        if (!tweetResources.isEmpty()) {
            success = saveUsers(tweetResources);
            if (success) {
                success = saveTweets(tweetResources);
            }

            if (!success) {
                Log.i(TAG, "Unable to save tweets");
            }
        }

        return success;
    }

    private AsyncHttpResponseHandler getRefreshTweetsResponseHandler(final Object additionalEventToPostOnceComplete) {
        return new TextHttpResponseHandler() {
            @Override
            public void onFailure(final int statusCode, final Header[] headers,
                                  final String responseString, final Throwable throwable) {
                Log.e(TAG, "Error retrieving tweets: " + responseString, throwable);
                final TweetsRefreshedEvent tweetsRefreshedEvent = new TweetsRefreshedEvent(0, false);
                mEventBus.post(tweetsRefreshedEvent);
                if (additionalEventToPostOnceComplete != null) {
                    mEventBus.post(additionalEventToPostOnceComplete);
                }
            }

            @Override
            public void onSuccess(final int statusCode, final Header[] headers,
                                  final String responseString) {
                final Type responseType = new TypeToken<List<TweetResource>>() {
                }.getType();
                final List<TweetResource> tweetResources = mGson.fromJson(responseString, responseType);
                Log.i(TAG, "Retrieved tweets: " + tweetResources.size());

                boolean success = saveTweetsAndUsers(tweetResources);

                final TweetsRefreshedEvent tweetsRefreshedEvent = new TweetsRefreshedEvent(tweetResources.size(), success);
                mEventBus.post(tweetsRefreshedEvent);
                if (additionalEventToPostOnceComplete != null) {
                    mEventBus.post(additionalEventToPostOnceComplete);
                }
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
                final Type responseType = new TypeToken<List<TweetResource>>() {
                }.getType();
                final List<TweetResource> tweetResources = mGson.fromJson(responseString, responseType);
                Log.i(TAG, "Retrieved tweets: " + tweetResources.size());

                boolean success = saveTweetsAndUsers(tweetResources);

                final TweetsPastRetrievedEvent tweetsPastRetrievedEvent = new TweetsPastRetrievedEvent(tweetResources.size(), success);
                mEventBus.post(tweetsPastRetrievedEvent);
            }
        };
    }
}