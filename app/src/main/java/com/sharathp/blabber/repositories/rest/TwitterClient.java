package com.sharathp.blabber.repositories.rest;

import android.content.Context;
import android.text.TextUtils;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sharathp.blabber.BuildConfig;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;

import java.util.List;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    private static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;

    public static final String REST_URL = "https://api.twitter.com/1.1";
    private static final String REST_CONSUMER_KEY = BuildConfig.TWITTER_API_KEY;
    private static final String REST_CONSUMER_SECRET = BuildConfig.TWITTER_API_SECRET;
    private static final String REST_CALLBACK_URL = "oauth://myblabber";

    private static final String RELATIVE_URL_HOME_TIMELINE = "statuses/home_timeline.json";
    private static final String RELATIVE_URL_CREDENTIALS = "account/verify_credentials.json";
    private static final String RELATIVE_URL_STATUS_UPDATE = "statuses/update.json";
    private static final String RELATIVE_URL_FAVORITE = "favorites/create.json?id=%d";
    private static final String RELATIVE_URL_UNFAVORITE = "favorites/destroy.json?id=%d";
    private static final String RELATIVE_URL_RETWEET = "statuses/retweet/%d.json";
    private static final String RELATIVE_URL_UNRETWEET = "statuses/unretweet/%d.json";
    private static final String RELATIVE_URL_STATUS = "statuses/show/%d.json";

    private static final String RELATIVE_URL_MENTION = "statuses/mentions_timeline.json";
    private static final String RELATIVE_URL_USER_TIMELINE = "statuses/user_timeline.json";
    private static final String RELATIVE_URL_USER_LIKE = "favorites/list.json";
    private static final String RELATIVE_URL_USER = "users/show.json";

    private static final String RELATIVE_URL_FOLLOWING = "friends/ids.json";
    private static final String RELATIVE_URL_FOLLOWERS = "followers/ids.json";
    private static final String RELATIVE_URL_USERS_LOOKUP = "users/lookup.json";
    private static final String RELATIVE_URL_SEARCH = "search/tweets.json";
    private static final String RELATIVE_URL_DIRECT_MESSAGES = "direct_messages.json";

    private static final String REQ_STATUS_ID = "id";
    private static final String REQ_PARAM_MAX_ID = "max_id";
    private static final String REQ_PARAM_SINCE_ID = "since_id";
    private static final String REQ_PARAM_STATUS = "status";
    private static final String REQ_PARAM_IN_REPLY_TO_STATUS_ID = "in_reply_to_status_id";
    private static final String REQ_PARAM_USER_ID = "user_id";
    private static final String REQ_PARAM_USER_SCREEN_NAME = "screen_name";
    private static final String REQ_PARAM_COUNT = "count";
    private static final String REQ_PARAM_CURSOR = "cursor";
    private static final String REQ_PARAM_INCLUDE_ENTITIES = "include_entities";
    private static final String REQ_PARAM_QUERY = "q";


    public TwitterClient(final Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    public void getLoggedInUserDetails(final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_CREDENTIALS);
        client.get(apiUrl, handler);
    }

    public void submitTweet(final String status, final Long inReplyToStatusId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_STATUS_UPDATE);
        final RequestParams requestParams = new RequestParams();
        requestParams.put(REQ_PARAM_STATUS, status);
        if (inReplyToStatusId != null) {
            requestParams.put(REQ_PARAM_IN_REPLY_TO_STATUS_ID, Long.toString(inReplyToStatusId));
        }
        client.post(apiUrl, requestParams, handler);
    }

    public void getPastHomeTimeline(final Long maxId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_HOME_TIMELINE);
        final RequestParams requestParams = getTweetsRequestParams(maxId, null);
        client.get(apiUrl, requestParams, handler);
    }

    public void getLatestHomeTimeline(final Long sinceId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_HOME_TIMELINE);
        final RequestParams requestParams = getTweetsRequestParams(null, sinceId);
        client.get(apiUrl, requestParams, handler);
    }

    public void getPastMentionTweets(final Long maxId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_MENTION);
        final RequestParams requestParams = getTweetsRequestParams(maxId, null);
        client.get(apiUrl, requestParams, handler);
    }

    public void getLatestMentionTweets(final Long sinceId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_MENTION);
        final RequestParams requestParams = getTweetsRequestParams(null, sinceId);
        client.get(apiUrl, requestParams, handler);
    }

    public void getPastUserTimeLineTweets(final Long maxId, final Long userId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_USER_TIMELINE);
        final RequestParams requestParams = getTweetsRequestParams(maxId, null);
        if (userId != null && userId > 0) {
            addUserRequestParams(requestParams, userId);
        }
        client.get(apiUrl, requestParams, handler);
    }

    public void getLatestUserTimeLineTweets(final Long sinceId, final Long userId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_USER_TIMELINE);
        final RequestParams requestParams = getTweetsRequestParams(null, sinceId);
        if (userId != null && userId > 0) {
            addUserRequestParams(requestParams, userId);
        }
        client.get(apiUrl, requestParams, handler);
    }

    public void getPastUserLikes(final Long maxId, final Long userId, AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_USER_LIKE);
        final RequestParams requestParams = getTweetsRequestParams(maxId, null);
        if (userId != null && userId > 0) {
            addUserRequestParams(requestParams, userId);
        }
        client.get(apiUrl, requestParams, handler);
    }

    public void getLatestUserLikes(final Long sinceId, final Long userId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_USER_LIKE);
        final RequestParams requestParams = getTweetsRequestParams(null, sinceId);
        if (userId != null && userId > 0) {
            addUserRequestParams(requestParams, userId);
        }
        client.get(apiUrl, requestParams, handler);
    }

    public void getLatestUserProfile(final Long userId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_USER);
        final RequestParams requestParams = getUserRequestParams(userId);
        client.get(apiUrl, requestParams, handler);
    }

    public void getLatestUserProfile(final String screenName, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_USER);
        final RequestParams requestParams = new RequestParams();
        requestParams.put(REQ_PARAM_USER_SCREEN_NAME, screenName);
        client.get(apiUrl, requestParams, handler);
    }

    public void getTweet(final Long statusId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(String.format(RELATIVE_URL_STATUS, statusId));
        client.get(apiUrl, null, handler);
    }

    public void unretweet(final Long statusId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(String.format(RELATIVE_URL_UNRETWEET, statusId));
        client.post(apiUrl, null, handler);
    }

    public void retweet(final Long statusId, final AsyncHttpResponseHandler handler ) {
        final String apiUrl = getApiUrl(String.format(RELATIVE_URL_RETWEET, statusId));
        client.post(apiUrl, null, handler);
    }

    public void favorite(final Long statusId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(String.format(RELATIVE_URL_FAVORITE, statusId));
        client.post(apiUrl, null, handler);
    }

    public void unfavorite(final Long statusId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(String.format(RELATIVE_URL_UNFAVORITE, statusId));
        client.post(apiUrl, null, handler);
    }

    public void getFollowers(final Long userId, final Integer count, final Long cursor,
                             final  AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_FOLLOWERS);
        final RequestParams requestParams = new RequestParams();
        requestParams.put(REQ_PARAM_USER_ID, userId);
        requestParams.put(REQ_PARAM_COUNT, count);
        if (cursor != null && cursor > 0) {
            requestParams.put(REQ_PARAM_CURSOR, cursor);
        }
        client.get(apiUrl, requestParams, handler);
    }

    public void getFollowing(final Long userId, final Integer count, final Long cursor,
                             final  AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_FOLLOWING);
        final RequestParams requestParams = new RequestParams();
        requestParams.put(REQ_PARAM_USER_ID, userId);
        requestParams.put(REQ_PARAM_COUNT, count);
        if (cursor != null && cursor > 0) {
            requestParams.put(REQ_PARAM_CURSOR, cursor);
        }
        client.get(apiUrl, requestParams, handler);
    }

    public void getDirectMessages(final Integer count, final Long max,
                                  final  AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_DIRECT_MESSAGES);
        final RequestParams requestParams = new RequestParams();
        if (max != null && max > 0) {
            requestParams.put(REQ_PARAM_MAX_ID, max);
        }
        requestParams.put(REQ_PARAM_COUNT, count);
        client.get(apiUrl, requestParams, handler);
    }

    // this is required because of the weird way oauth token management
    public Token getAccessToken() {
        return getClient().getAccessToken();
    }

    public void setAccessToken(final Token token) {
        getClient().setAccessToken(token);
    }

    public void getUsers(final List<Long> userIds, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_USERS_LOOKUP);
        final String userIdParam = TextUtils.join(",", userIds);
        final RequestParams requestParams = new RequestParams();
        requestParams.put(REQ_PARAM_USER_ID, userIdParam);
        requestParams.put(REQ_PARAM_INCLUDE_ENTITIES, false);
        client.get(apiUrl, requestParams, handler);
    }

    public void getSearchResults(final Long maxId, final String query, AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_SEARCH);
        final RequestParams requestParams = getTweetsRequestParams(maxId, null);
        requestParams.put(REQ_PARAM_QUERY, query);
        requestParams.put(REQ_PARAM_INCLUDE_ENTITIES, true);
        client.get(apiUrl, requestParams, handler);
    }

    private RequestParams getUserRequestParams(final Long userId) {
        final RequestParams requestParams = new RequestParams();
        addUserRequestParams(requestParams, userId);
        return requestParams;
    }

    private void addUserRequestParams(final RequestParams requestParams, final Long userId) {
        requestParams.put(REQ_PARAM_USER_ID, userId);
    }

    private RequestParams getTweetsRequestParams(final Long maxId, final Long sinceId) {
        final RequestParams requestParams = new RequestParams();
        if (maxId != null) {
            requestParams.put(REQ_PARAM_MAX_ID, maxId);
        }

        if (sinceId != null) {
            requestParams.put(REQ_PARAM_SINCE_ID, sinceId);
        }
        return requestParams;
    }
}