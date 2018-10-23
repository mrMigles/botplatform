package ru.holyway.botplatform.core.entity;

import java.util.Objects;

public class TwitterFollow {

  private String userName;
  private String lastId;

  public TwitterFollow() {
  }

  public TwitterFollow(String userName) {
    this.userName = userName;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getLastId() {
    return lastId;
  }

  public void setLastId(String lastId) {
    this.lastId = lastId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TwitterFollow that = (TwitterFollow) o;
    return Objects.equals(userName, that.userName);
  }

  @Override
  public int hashCode() {

    return Objects.hash(userName);
  }
}
