package ru.holyway.botplatform.core.entity;

import java.util.Objects;

public class InstaFollow {

  private String userName;
  private String lastPostIdId = "0";

  public InstaFollow(String userName, String lastPostIdId) {
    this.userName = userName;
    this.lastPostIdId = lastPostIdId;
  }

  public InstaFollow(String userName) {
    this.userName = userName;
  }

  public InstaFollow() {
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getLastPostIdId() {
    return lastPostIdId;
  }

  public void setLastPostIdId(String lastPostIdId) {
    this.lastPostIdId = lastPostIdId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InstaFollow that = (InstaFollow) o;
    return Objects.equals(userName, that.userName);
  }

  @Override
  public int hashCode() {

    return Objects.hash(userName);
  }
}
