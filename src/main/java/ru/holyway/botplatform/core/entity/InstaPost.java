package ru.holyway.botplatform.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InstaPost {

  @JsonProperty("description")
  private String description;

  @JsonProperty("photo_url")
  private String photoUrl;

  @JsonProperty("id")
  private String ID;

  @JsonProperty("post_url")
  private String postUrl;

  @JsonProperty("likes")
  private Integer likes;

  public InstaPost(String description, String photoUrl, String ID, String postUrl,
                   Integer likes) {
    this.description = description;
    this.photoUrl = photoUrl;
    this.ID = ID;
    this.postUrl = postUrl;
    this.likes = likes;
  }

  public InstaPost() {
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }

  public String getID() {
    return ID;
  }

  public void setID(String ID) {
    this.ID = ID;
  }

  public String getPostUrl() {
    return postUrl;
  }

  public void setPostUrl(String postUrl) {
    this.postUrl = postUrl;
  }

  public Integer getLikes() {
    return likes;
  }

  public void setLikes(Integer likes) {
    this.likes = likes;
  }
}
