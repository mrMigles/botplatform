package ru.holyway.botplatform.core.data;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class ProcessorsContext {

  private Map<String, Set<Long>> bannedAdmins = new ConcurrentHashMap<>();

  private final static ProcessorsContext PROCESSORS_CONTEXT = new ProcessorsContext();

  public static ProcessorsContext getInstance() {
    return PROCESSORS_CONTEXT;
  }

  public Set<Long> getBannedAdmins(final String chatID) {
    return bannedAdmins.get(chatID);
  }

  public void addBannedAdmin(final String chatID, final Long userID) {
    Set<Long> userIDs = getBannedAdmins(chatID);
    if (userIDs == null) {
      userIDs = new CopyOnWriteArraySet<>();
    }
    userIDs.add(userID);
    bannedAdmins.put(chatID, userIDs);
  }

  public void removeBannedAdmin(final String chatID, final Long userID) {
    Set<Long> userIDs = getBannedAdmins(chatID);
    if (userIDs != null) {
      userIDs.remove(userID);
      bannedAdmins.put(chatID, userIDs);
    }
  }
}
