package com.sharathp.blabber.repositories.rest.resources;

import com.google.gson.annotations.SerializedName;

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

        public Long getMaxId() {
            return maxId;
        }

        public void setMaxId(final Long maxId) {
            this.maxId = maxId;
        }
    }
}
