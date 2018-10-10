package ru.holyway.botplatform.core.entity;

import java.util.Objects;

public class InstaFollow {

  private String userName;
  private String lastId;

  public InstaFollow(String userName, String lastId) {
    this.userName = userName;
    this.lastId = lastId;
  }

  public InstaFollow(String userName) {
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
    InstaFollow that = (InstaFollow) o;
    return Objects.equals(userName, that.userName);
  }

  @Override
  public int hashCode() {

    return Objects.hash(userName);
  }
}
