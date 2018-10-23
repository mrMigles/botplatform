package ru.holyway.botplatform.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TwitterResponse {

  public static class Tweet {

    private String id;
    private String text;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }

    public Tweet() {
    }
  }

  @JsonProperty("tweets")
  private List<Tweet> tweetList;

  @JsonProperty("name")
  private String name;

  @JsonProperty("screen_name")
  private String screenName;

  public List<Tweet> getTweetList() {
    return tweetList;
  }

  public void setTweetList(
      List<Tweet> tweetList) {
    this.tweetList = tweetList;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getScreenName() {
    return screenName;
  }

  public void setScreenName(String screenName) {
    this.screenName = screenName;
  }

  public TwitterResponse() {
  }
}
