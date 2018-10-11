package ru.holyway.botplatform.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InstaStory {

  @JsonProperty("story_url")
  private String storyURL;

  @JsonProperty("original_id")
  private String originalID;

  @JsonProperty("id")
  private String id;

  @JsonProperty("media_url")
  private String mediaUrl;

  public InstaStory() {
  }

  public String getStoryURL() {
    return storyURL;
  }

  public void setStoryURL(String storyURL) {
    this.storyURL = storyURL;
  }

  public String getOriginalID() {
    return originalID;
  }

  public void setOriginalID(String originalID) {
    this.originalID = originalID;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMediaUrl() {
    return mediaUrl;
  }

  public void setMediaUrl(String mediaUrl) {
    this.mediaUrl = mediaUrl;
  }
}
