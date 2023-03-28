package ru.holyway.botplatform.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class InstaUser {

  @JsonProperty("user_name")
  private String userName;

  @JsonProperty("posts")
  private List<InstaPost> posts;

  @JsonProperty("stories")
  private List<InstaStory> stories;

  public List<InstaStory> getStories() {
    return stories;
  }

  public void setStories(List<InstaStory> stories) {
    this.stories = stories;
  }

  public InstaUser(String userName,
                   List<InstaPost> posts) {
    this.userName = userName;
    this.posts = posts;
  }

  public InstaUser() {
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public List<InstaPost> getPosts() {
    return posts;
  }

  public void setPosts(List<InstaPost> posts) {
    this.posts = posts;
  }
}
