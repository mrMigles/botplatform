package ru.holyway.botplatform.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class YoutubeVideo {

  @JsonProperty("Id")
  private String id;

  @JsonProperty("link")
  private String link;

  @JsonProperty("description")
  private String description;

  @JsonProperty("live")
  private Boolean isLive;

  public YoutubeVideo() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getLive() {
    return isLive;
  }

  public void setLive(Boolean live) {
    isLive = live;
  }
}
