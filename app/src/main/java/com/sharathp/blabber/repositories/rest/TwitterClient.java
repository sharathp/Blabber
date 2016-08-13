package com.sharathp.blabber.repositories.rest;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sharathp.blabber.BuildConfig;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

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

    private static final String REST_URL = "https://api.twitter.com/1.1";
    private static final String REST_CONSUMER_KEY = BuildConfig.TWITTER_API_KEY;
    private static final String REST_CONSUMER_SECRET = BuildConfig.TWITTER_API_SECRET;
    private static final String REST_CALLBACK_URL = "oauth://myblabber";

    private static final String RELATIVE_URL_HOME_TIMELINE = "/statuses/home_timeline.json";
    private static final String RELATIVE_URL_CREDENTIALS = "/account/verify_credentials.json";
    private static final String RELATIVE_URL_STATUS_UPDATE = "/statuses/update.json";
    private static final String RELATIVE_URL_FAVORITE = "/favorites/create.json";
    private static final String RELATIVE_URL_UNFAVORITE = "/favorites/destroy.json";
    private static final String RELATIVE_URL_RETWEET = "/statuses/retweet/%d.json";
    private static final String RELATIVE_URL_UNRETWEET = "/statuses/unretweet/%d.json";
    private static final String RELATIVE_URL_STATUS = "/statuses/show/%d.json";

    private static final String RELATIVE_URL_MENTION = "/statuses/mentions_timeline.json";
    private static final String RELATIVE_URL_USER_TIMELINE = "/statuses/user_timeline.json";

    private static final String REQ_STATUS_ID = "id";
    private static final String REQ_PARAM_MAX_ID = "max_id";
    private static final String REQ_PARAM_SINCE_ID = "since_id";
    private static final String REQ_PARAM_STATUS = "status";
    private static final String REQ_PARAM_IN_REPLY_TO_STATUS_ID = "in_reply_to_status_id";
    private static final String REQ_PARAM_USER_ID = "user_id";

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

    public void getLatestUserTimeLineTweets(final Long maxId, final Long userId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_USER_TIMELINE);
        final RequestParams requestParams = getTweetsRequestParams(maxId, null);
        if (userId != null && userId > 0) {
            requestParams.put(REQ_PARAM_USER_ID, userId);
        }
        client.get(apiUrl, requestParams, handler);
    }

    public void getPastUserTimeLineTweets(final Long sinceId, final Long userId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_USER_TIMELINE);
        final RequestParams requestParams = getTweetsRequestParams(null, sinceId);
        if (userId != null && userId > 0) {
            requestParams.put(REQ_PARAM_USER_ID, userId);
        }
        client.get(apiUrl, requestParams, handler);
    }

    public void getTweet(final Long statusId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(String.format(RELATIVE_URL_STATUS, statusId));
        client.post(apiUrl, getStatusRequestParams(statusId), handler);
    }

    public void unretweet(final Long statusId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(String.format(RELATIVE_URL_RETWEET, statusId));
        client.post(apiUrl, getStatusRequestParams(statusId), handler);
    }

    public void retweet(final Long statusId, final AsyncHttpResponseHandler handler ) {
        final String apiUrl = getApiUrl(String.format(RELATIVE_URL_UNRETWEET, statusId));
        client.post(apiUrl, getStatusRequestParams(statusId), handler);
    }

    public void favorite(final Long statusId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_FAVORITE);
        client.post(apiUrl, getStatusRequestParams(statusId), handler);
    }

    public void unfavorite(final Long statusId, final AsyncHttpResponseHandler handler ) {
        final String apiUrl = getApiUrl(RELATIVE_URL_UNFAVORITE);
        client.post(apiUrl, getStatusRequestParams(statusId), handler);
    }

    private RequestParams getStatusRequestParams(final Long statusId) {
        final RequestParams requestParams = new RequestParams();
        requestParams.put(REQ_STATUS_ID, statusId);
        return requestParams;
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