package com.sharathp.blabber.repositories.rest.resources;

import android.net.UrlQuerySanitizer;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.sharathp.blabber.repositories.rest.TwitterClient;

import java.util.List;

public class SearchResultsResource {

    List<TweetResource> statuses;

    @SerializedName("search_metadata")
    SearchMetaData searchMetaData;


    public List<TweetResource> getStatuses() {
        return statuses;
    }

    public void setStatuses(final List<TweetResource> statuses) {
        this.statuses = statuses;
    }

    public SearchMetaData getSearchMetaData() {
        return searchMetaData;
    }

    public void setSearchMetaData(final SearchMetaData searchMetaData) {
        this.searchMetaData = searchMetaData;
    }

    public static class SearchMetaData {
        @SerializedName("max_id_str")
        Long maxId;

        @SerializedName("next_results")
        String nextResults;

        public Long getMaxId() {
            return maxId;
        }

        public void setMaxId(final Long maxId) {
            this.maxId = maxId;
        }

        public String getNextResults() {
            return nextResults;
        }

        public void setNextResults(String nextResults) {
            this.nextResults = nextResults;
        }

        public Long parseMaxFromNextResults() {
            final UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
            sanitizer.setAllowUnregisteredParamaters(true);
            sanitizer.parseUrl(TwitterClient.REST_URL + nextResults);
            final String maxId = sanitizer.getValue("max_id");
            if (TextUtils.isEmpty(maxId)) {
                return 0L;
            }

            return Long.parseLong(maxId);
        }
    }
}
