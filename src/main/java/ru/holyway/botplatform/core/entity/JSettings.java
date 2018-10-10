package ru.holyway.botplatform.core.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.annotation.Id;

/**
 * Created by Sergey on 1/18/2017.
 */
public class JSettings {

  private final static Map<String, UserAccessInfo> userTokens = new ConcurrentHashMap<>();

  private Set<String> muteChats;
  private Set<String> easyChats;
  private Map<String, Integer> answerProximity;
  private Map<String, Set<String>> syncChats;
  private Map<String, String> tokens;
  private Map<String, Set<InstaFollow>> instaFollows;

  @Id
  public String id;

  public JSettings(Set<String> muteChats, Set<String> easyChats,
      Map<String, Integer> answerProximity, Map<String, Set<String>> syncChats,
      Map<String, String> tokens) {
    this.muteChats = muteChats;
    this.easyChats = easyChats;
    this.answerProximity = answerProximity;
    this.syncChats = syncChats;
    this.tokens = tokens;
  }

  public JSettings() {
    muteChats = new HashSet<>();
    easyChats = new HashSet<>();
    answerProximity = new HashMap<>();
    syncChats = new ConcurrentHashMap<>();
    tokens = new ConcurrentHashMap<>();
    instaFollows = new ConcurrentHashMap<>();
  }

  public void addMuteChat(String chatId) {
    muteChats.add(chatId);
  }

  public void removeMuteChat(String chatId) {
    muteChats.remove(chatId);
  }

  public void addEasyChat(String chatId) {
    easyChats.add(chatId);
  }

  public void addFollow(String chatId, String follow) {
    if (instaFollows.get(chatId) != null) {
      instaFollows.get(chatId).add(new InstaFollow(follow, "0"));
    } else {
      Set<InstaFollow> instaFollowsSet = new HashSet<>();
      instaFollowsSet.add(new InstaFollow(follow, "0"));
      instaFollows.put(chatId, instaFollowsSet);
    }
  }

  public void removeFollow(String chatId, String follow) {
    if (instaFollows.get(chatId) != null) {
      instaFollows.get(chatId).remove(new InstaFollow(follow));
    }
  }

  public void updateFollow(String chatId, String follow, String lastId) {
    if (instaFollows.get(chatId) != null) {
      instaFollows.get(chatId).add(new InstaFollow(follow, lastId));
    }
  }

  public Map<String, Set<InstaFollow>> getFollows() {
    return instaFollows;
  }

  public InstaFollow getFollow(String chatId, String follow) {
    if (instaFollows.get(chatId) != null) {
      return instaFollows.get(chatId).stream()
          .filter(instaFollow -> instaFollow.getUserName().equals(follow)).findFirst().orElse(null);
    }
    return null;
  }

  public void removeEasyChat(String chatId) {
    easyChats.remove(chatId);
  }

  public void setProximityAnswer(String chatId, int percent) {
    answerProximity.put(chatId.replace(".", ""), percent);
  }

  public String getToken(final String chatId) {
    String token = tokens.get(chatId.replace(".", ""));
    if (token == null) {
      token = UUID.randomUUID().toString().replace("-", "");
      tokens.put(chatId.replace(".", ""), token);
    }
    return token;
  }

  public String generateNewToken(final String chatId) {
    final String token = UUID.randomUUID().toString().replace("-", "");
    tokens.put(chatId.replace(".", ""), token);
    return token;
  }

  public String getUserToken(final String chatId, final String login, final String userName) {
    for (Map.Entry<String, UserAccessInfo> userAccessInfo : userTokens.entrySet()) {
      if (userAccessInfo.getValue().getChatId().equals(chatId) && userAccessInfo.getValue()
          .getUserLogin().equals(login)) {
        if (userAccessInfo.getValue().getExpirationTime() > System.currentTimeMillis()) {
          return userAccessInfo.getKey();
        }
      }
    }

    final String token = UUID.randomUUID().toString().replace("-", "");
    userTokens.put(token, new UserAccessInfo(userName, login, chatId));

    return token;
  }

  public UserAccessInfo getUserAccessInfo(final String token) {
    return userTokens.get(token);
  }

  public Set<String> getMuteChats() {
    return muteChats;
  }

  public Set<String> getEasyChats() {
    return easyChats;
  }

  public Integer getAnswerProximity(final String chatId) {
    return answerProximity.get(chatId.replace(".", ""));
  }

  public Set<String> getSyncForChat(String chatID) {
    Set<String> resultSync = new HashSet<>();
    resultSync.add(chatID);

    Set<String> chatsSync = syncChats.get(chatID.replace(".", ""));
    if (chatsSync != null) {

      for (String syncChat : chatsSync) {
        if (syncChats.get(syncChat.replace(".", "")) != null) {
          if (syncChats.get(syncChat.replace(".", "")).contains(chatID)) {
            resultSync.add(syncChat);
          }
        }
      }
    }
    return resultSync;
  }

  public boolean syncChat(String chatId, String withChatId) {
    Set<String> chatSet = syncChats.get(chatId.replace(".", ""));
    if (chatSet == null) {
      chatSet = new HashSet<>();
      syncChats.put(chatId.replace(".", ""), chatSet);
    }
    chatSet.add(withChatId);

    chatSet = syncChats.get(withChatId.replace(".", ""));
    if (chatSet != null) {
      if (chatSet.contains(chatId)) {
        return true;
      }
    }
    return false;
  }
}
