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
import com.sharathp.blabber.events.MentionsPastRetrievedEvent;
import com.sharathp.blabber.events.MentionsLatestEvent;
import com.sharathp.blabber.events.StatusSubmittedEvent;
import com.sharathp.blabber.events.TweetsPastRetrievedEvent;
import com.sharathp.blabber.events.TweetsRefreshedEvent;
import com.sharathp.blabber.events.UserTimelineLatestEvent;
import com.sharathp.blabber.events.UserTimelinePastRetrievedEvent;
import com.sharathp.blabber.models.Mentions;
import com.sharathp.blabber.models.MentionsWithUser;
import com.sharathp.blabber.models.Tweet;
import com.sharathp.blabber.models.User;
import com.sharathp.blabber.models.UserTimeLineTweetWithUser;
import com.sharathp.blabber.models.UserTimeline;
import com.sharathp.blabber.repositories.LocalPreferencesDAO;
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
    private static final String EXTRA_USER_ID = UpdateTimelineService.class.getName() + ".USER_ID";

    private static final String EXTRA_STATUS = UpdateTimelineService.class.getName() + ".EXTRA_STATUS";
    private static final String EXTRA_IN_REPLY_TO_STATUS_ID = UpdateTimelineService.class.getName() + ".IN_REPLY_TO_STATUS_ID";


    @IntDef({OP_REFRESH_LATEST, OP_REFRESH_PAST, OP_DELETE_EXISTING_AND_REFRESH,
            OP_TWEET, OP_TWEET_FAVORITE, OP_TWEET_UNFAVORITE,OP_MENTION_LATEST,
            OP_MENTION_PAST, OP_USER_TIMELINE_LATEST, OP_USER_TIMELINE_PAST})
    private @interface OperationType {}

    private static final int OP_REFRESH_LATEST = 1;
    private static final int OP_REFRESH_PAST = 2;
    private static final int OP_DELETE_EXISTING_AND_REFRESH = 3;
    private static final int OP_TWEET = 4;
    private static final int OP_TWEET_FAVORITE = 5;
    private static final int OP_TWEET_UNFAVORITE = 6;
    private static final int OP_MENTION_LATEST= 7;
    private static final int OP_MENTION_PAST = 8;
    private static final int OP_USER_TIMELINE_LATEST= 9;
    private static final int OP_USER_TIMELINE_PAST = 10;

    @Inject
    EventBus mEventBus;

    @Inject
    TwitterDAO mTwitterDAO;

    @Inject
    TwitterClient mTwitterClient;

    @Inject
    Gson mGson;

    @Inject
    LocalPreferencesDAO mLocalPreferencesDAO;

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

    public static Intent createIntentForLatestMentions(final Context context) {
        return createIntent(context, OP_MENTION_LATEST);
    }

    public static Intent createIntentForPastMentions(final Context context) {
        return createIntent(context, OP_MENTION_PAST);
    }

    public static Intent createIntentForLatestUserTimeline(final Context context, final long userId) {
        final Intent intent = createIntent(context, OP_USER_TIMELINE_LATEST);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    public static Intent createIntentForPastUserTimeline(final Context context, final long userId) {
        final Intent intent =  createIntent(context, OP_USER_TIMELINE_PAST);
        intent.putExtra(EXTRA_USER_ID, userId);
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
            case OP_MENTION_LATEST: {
                Log.i(TAG, "Retrieved Latest mentions");
                retrieveLatestMentions();
                break;
            }
            case OP_MENTION_PAST: {
                Log.i(TAG, "Retrieved Past mentions");
                retrievePastMentions();
                break;
            }
            case OP_USER_TIMELINE_LATEST: {
                Log.i(TAG, "Retrieved Latest user tweets");
                retrieveLatestUserTimeline(intentData.getLong(EXTRA_USER_ID, -1));
                break;
            }
            case OP_USER_TIMELINE_PAST: {
                Log.i(TAG, "Retrieved Past user tweets");
                retrievePastUserTimeline(intentData.getLong(EXTRA_USER_ID, -1));
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

    private void retrieveLatestUserTimeline(final long userId) {
        final UserTimeLineTweetWithUser latestUserTimeline = mTwitterDAO.getLatestUserTimeline(userId);
        Long sinceId = null;
        if (latestUserTimeline != null) {
            sinceId = latestUserTimeline.getId();
        }
        mTwitterClient.getLatestUserTimeLineTweets(sinceId, userId, getLatestUserTimelineResponseHandler(userId));
    }

    private void retrievePastUserTimeline(final long userId) {
        final UserTimeLineTweetWithUser pastUserTimeline = mTwitterDAO.getEarliestUserTimeline(userId);
        Long maxId = null;
        if (pastUserTimeline != null) {
            maxId = pastUserTimeline.getId();
        }
        mTwitterClient.getPastUserTimeLineTweets(maxId - 1, userId, getPastUserTimelineResponseHandler(userId));
    }

    private void retrievePastMentions() {
        final MentionsWithUser pastMention = mTwitterDAO.getEarliestMention();
        Long maxId = null;
        if (pastMention != null) {
            maxId = pastMention.getId();
        }
        mTwitterClient.getPastMentionTweets(maxId - 1, getMentionsPastTweetsResponseHandler());
    }

    private void retrieveLatestMentions() {
        final MentionsWithUser latestMention = mTwitterDAO.getLatestMention();
        Long sinceId = null;
        if (latestMention != null) {
            sinceId = latestMention.getId();
        }
        mTwitterClient.getLatestMentionTweets(sinceId, getMentionsRefreshResponseHandler());
    }

    private void retrievePastTweets() {
        final Tweet tweet = mTwitterDAO.getEarliestTweet();
        Long maxId = null;
        if (tweet != null) {
            maxId = tweet.getId();
        }
        mTwitterClient.getPastTweets(maxId - 1, getPastTweetsResponseHandler());
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

    private boolean saveMentions(final List<TweetResource> tweetResources) {
        boolean success = saveTweetsAndUsers(tweetResources);

        if (! success) {
            Log.i(TAG, "Unable to save tweets, not saving mentions");
            return success;
        }

        final long userId = mLocalPreferencesDAO.getUserId();
        final List<Mentions> mentions = new ArrayList<>();
        for (final TweetResource tweetResource : tweetResources) {
            mentions.add(new Mentions()
                    .setTweetId(tweetResource.getId())
                    .setUserMentionId(userId));
        }
        success = mTwitterDAO.checkAndInsertMentions(mentions);
        if (! success) {
            Log.i(TAG, "Unable to save mentions");
        }
        return success;
    }

    private boolean saveUserTimelines(final Long userId, final List<TweetResource> tweetResources) {
        boolean success = saveTweetsAndUsers(tweetResources);

        if (! success) {
            Log.i(TAG, "Unable to save tweets, not saving timelines");
            return success;
        }

        final List<UserTimeline> userTimelines = new ArrayList<>();
        for (final TweetResource tweetResource : tweetResources) {
            userTimelines.add(new UserTimeline()
                    .setTweetId(tweetResource.getId())
                    .setUserTimeLineId(userId));
        }
        success = mTwitterDAO.checkAndInsertUserTimelines(userTimelines);
        if (! success) {
            Log.i(TAG, "Unable to save user timelines");
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
                Log.e(TAG, "Error retrieving past tweets: " + responseString, throwable);
                final TweetsPastRetrievedEvent tweetsPastRetrievedEvent = new TweetsPastRetrievedEvent(0, false);
                mEventBus.post(tweetsPastRetrievedEvent);
            }

            @Override
            public void onSuccess(final int statusCode, final Header[] headers,
                                  final String responseString) {
                final Type responseType = new TypeToken<List<TweetResource>>() {
                }.getType();
                final List<TweetResource> tweetResources = mGson.fromJson(responseString, responseType);
                Log.i(TAG, "Retrieved past tweets: " + tweetResources.size());

                boolean success = saveTweetsAndUsers(tweetResources);

                final TweetsPastRetrievedEvent tweetsPastRetrievedEvent = new TweetsPastRetrievedEvent(tweetResources.size(), success);
                mEventBus.post(tweetsPastRetrievedEvent);
            }
        };
    }

    private AsyncHttpResponseHandler getMentionsRefreshResponseHandler() {
        return new TextHttpResponseHandler() {
            @Override
            public void onFailure(final int statusCode, final Header[] headers,
                                  final String responseString, final Throwable throwable) {
                Log.e(TAG, "Error retrieving mentions: " + responseString, throwable);
                final MentionsLatestEvent mentionsLatestEvent = new MentionsLatestEvent(0, false);
                mEventBus.post(mentionsLatestEvent);
            }

            @Override
            public void onSuccess(final int statusCode, final Header[] headers,
                                  final String responseString) {
                final Type responseType = new TypeToken<List<TweetResource>>() {
                }.getType();
                final List<TweetResource> tweetResources = mGson.fromJson(responseString, responseType);
                Log.i(TAG, "Retrieved mentions: " + tweetResources.size());

                boolean success = saveMentions(tweetResources);

                final MentionsLatestEvent mentionsLatestEvent = new MentionsLatestEvent(tweetResources.size(), success);
                mEventBus.post(mentionsLatestEvent);
            }
        };
    }

    private AsyncHttpResponseHandler getMentionsPastTweetsResponseHandler() {
        return new TextHttpResponseHandler() {
            @Override
            public void onFailure(final int statusCode, final Header[] headers,
                                  final String responseString, final Throwable throwable) {
                Log.e(TAG, "Error retrieving past mentions: " + responseString, throwable);
                final MentionsPastRetrievedEvent mentionsPastRetrievedEvent = new MentionsPastRetrievedEvent(0, false);
                mEventBus.post(mentionsPastRetrievedEvent);
            }

            @Override
            public void onSuccess(final int statusCode, final Header[] headers,
                                  final String responseString) {
                final Type responseType = new TypeToken<List<TweetResource>>() {
                }.getType();
                final List<TweetResource> tweetResources = mGson.fromJson(responseString, responseType);
                Log.i(TAG, "Retrieved past mentions: " + tweetResources.size());

                boolean success = saveMentions(tweetResources);

                final MentionsPastRetrievedEvent mentionsPastRetrievedEvent = new MentionsPastRetrievedEvent(tweetResources.size(), success);
                mEventBus.post(mentionsPastRetrievedEvent);
            }
        };
    }

    private AsyncHttpResponseHandler getLatestUserTimelineResponseHandler(final Long userId) {
        return new TextHttpResponseHandler() {
            @Override
            public void onFailure(final int statusCode, final Header[] headers,
                                  final String responseString, final Throwable throwable) {
                Log.e(TAG, "Error retrieving user timeline: " + responseString, throwable);

                final UserTimelineLatestEvent userTimelineLatestEvent = new UserTimelineLatestEvent(userId, 0, false);
                mEventBus.post(userTimelineLatestEvent);
            }

            @Override
            public void onSuccess(final int statusCode, final Header[] headers,
                                  final String responseString) {
                final Type responseType = new TypeToken<List<TweetResource>>() {
                }.getType();
                final List<TweetResource> tweetResources = mGson.fromJson(responseString, responseType);
                Log.i(TAG, "Retrieved latest user timeline: " + tweetResources.size());

                final boolean success = saveUserTimelines(userId, tweetResources);

                final UserTimelineLatestEvent userTimelineLatestEvent = new UserTimelineLatestEvent(userId, tweetResources.size(), success);
                mEventBus.post(userTimelineLatestEvent);
            }
        };
    }

    private AsyncHttpResponseHandler getPastUserTimelineResponseHandler(final Long userId) {
        return new TextHttpResponseHandler() {
            @Override
            public void onFailure(final int statusCode, final Header[] headers,
                                  final String responseString, final Throwable throwable) {
                Log.e(TAG, "Error retrieving past timeline: " + responseString, throwable);

                final UserTimelinePastRetrievedEvent userTimelinePastRetrievedEvent = new UserTimelinePastRetrievedEvent(userId, 0, false);
                mEventBus.post(userTimelinePastRetrievedEvent);
            }

            @Override
            public void onSuccess(final int statusCode, final Header[] headers,
                                  final String responseString) {
                final Type responseType = new TypeToken<List<TweetResource>>() {
                }.getType();
                final List<TweetResource> tweetResources = mGson.fromJson(responseString, responseType);
                Log.i(TAG, "Retrieved past user timeline: " + tweetResources.size());

                final boolean success = saveUserTimelines(userId, tweetResources);

                final UserTimelinePastRetrievedEvent userTimelinePastRetrievedEvent = new UserTimelinePastRetrievedEvent(userId, tweetResources.size(), success);
                mEventBus.post(userTimelinePastRetrievedEvent);
            }
        };
    }
}