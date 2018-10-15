package ru.holyway.botplatform.core.entity;

import java.util.Objects;

public class YouTubeChanel {

  private String channelName;
  private String lastId;

  public YouTubeChanel() {
  }

  public YouTubeChanel(String channelName) {
    this.channelName = channelName;
  }

  public String getChannelName() {
    return channelName;
  }

  public void setChannelName(String channelName) {
    this.channelName = channelName;
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
    YouTubeChanel that = (YouTubeChanel) o;
    return Objects.equals(channelName, that.channelName);
  }

  @Override
  public int hashCode() {

    return Objects.hash(channelName);
  }
}
