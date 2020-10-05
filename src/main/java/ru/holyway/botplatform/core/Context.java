package ru.holyway.botplatform.core;

public class Context {

  private int count = 0;
  private int goodCount = 0;
  private long lastStamp = 0;
  private int denisCount = 0;

  public int getCount() {
    return count;
  }

  public synchronized void incrementCount() {
    this.count++;
  }

  public int getGoodCount() {
    return goodCount;
  }

  public synchronized void incrementGoodCount() {
    this.goodCount++;
  }

  public long getLastStamp() {
    return lastStamp;
  }

  public synchronized void setLastStamp(long lastStamp) {
    this.lastStamp = lastStamp;
  }

  public int getDenisCount() {
    return denisCount;
  }

  public synchronized void incrementDenisCount() {
    this.denisCount++;
  }
}
