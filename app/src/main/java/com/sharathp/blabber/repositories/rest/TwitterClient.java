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

    private static final String RELATIVE_URL_STATUS = "/statuses/home_timeline.json";

    private static final String REQ_PARAM_MAX_ID = "max_id";
    private static final String REQ_PARAM_SINCE_ID = "since_id";


    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    public void getPastTweets(final Long maxId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_STATUS);
        final RequestParams requestParams = getRequestParams(maxId, null);
        client.get(apiUrl, requestParams, handler);
    }

    public void getLatestTweets(final Long sinceId, final AsyncHttpResponseHandler handler) {
        final String apiUrl = getApiUrl(RELATIVE_URL_STATUS);
        final RequestParams requestParams = getRequestParams(null, sinceId);
        client.get(apiUrl, requestParams, handler);
    }

    private RequestParams getRequestParams(final Long maxId, final Long sinceId) {
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