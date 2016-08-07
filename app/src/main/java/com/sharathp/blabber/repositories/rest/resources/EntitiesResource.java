package com.sharathp.blabber.repositories.rest.resources;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EntitiesResource {

    List<MediaResource> media;

    public List<MediaResource> getMedia() {
        return media;
    }

    public void setMedia(List<MediaResource> media) {
        this.media = media;
    }

    public static class MediaResource {

        @SerializedName("id_str")
        long id;

        String type;

        @SerializedName("media_url_https")
        String mediaUrl;

        @SerializedName("video_info")
        VideoInfoResource videoInfo;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMediaUrl() {
            return mediaUrl;
        }

        public void setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
        }

        public VideoInfoResource getVideoInfo() {
            return videoInfo;
        }

        public void setVideoInfo(VideoInfoResource videoInfo) {
            this.videoInfo = videoInfo;
        }
    }

    public static class VideoInfoResource {
        List<VideoInfoVariantResource> variants;

        public List<VideoInfoVariantResource> getVariants() {
            return variants;
        }

        public void setVariants(List<VideoInfoVariantResource> variants) {
            this.variants = variants;
        }
    }

    public static class VideoInfoVariantResource {
        @SerializedName("content_type")
        String contentType;

        String url;

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}