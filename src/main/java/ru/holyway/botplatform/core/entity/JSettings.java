package ru.holyway.botplatform.core.entity;

import org.springframework.data.annotation.Id;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Sergey on 1/18/2017.
 */
public class JSettings {

    private Set<String> muteChats;
    private Set<String> easyChats;
    private Map<String, Integer> answerProximity;
    private Map<String, Set<String>> syncChats;
    private Map<String, String> tokens;
    private Map<String, UserAccessInfo> userTokens;

    @Id
    public String id;

    public JSettings(Set<String> muteChats, Set<String> easyChats, Map<String, Integer> answerProximity, Map<String, Set<String>> syncChats, Map<String, String> tokens, Map<String, UserAccessInfo> userTokens) {
        this.muteChats = muteChats;
        this.easyChats = easyChats;
        this.answerProximity = answerProximity;
        this.syncChats = syncChats;
        this.tokens = tokens;
        this.userTokens = userTokens;
    }

    public JSettings() {
        muteChats = new HashSet<>();
        easyChats = new HashSet<>();
        answerProximity = new HashMap<>();
        syncChats = new ConcurrentHashMap<>();
        tokens = new ConcurrentHashMap<>();
        userTokens = new ConcurrentHashMap<>();
    }

    public void addMuteChat(String chatId) {
        muteChats.add(chatId);
        writeToFile();
    }

    public void removeMuteChat(String chatId) {
        muteChats.remove(chatId);
        writeToFile();
    }

    public void addEasyChat(String chatId) {
        easyChats.add(chatId);
        writeToFile();
    }

    public void removeEasyChat(String chatId) {
        easyChats.remove(chatId);
        writeToFile();
    }

    public void setProximityAnswer(String chatId, int percent) {
        answerProximity.put(chatId, percent);
        writeToFile();
    }

    public String getToken(final String chatId) {
        String token = tokens.get(chatId);
        if (token == null) {
            token = UUID.randomUUID().toString();
            tokens.put(chatId, token);
        }
        return token;
    }

    public String generateNewToken(final String chatId) {
        final String token = UUID.randomUUID().toString();
        tokens.put(chatId, token);
        return token;
    }

    public String getUserToken(final String chatId, final String login, final String userName) {
        for (Map.Entry<String, UserAccessInfo> userAccessInfo : userTokens.entrySet()) {
            if (userAccessInfo.getValue().getChatId().equals(chatId) && userAccessInfo.getValue().getUserLogin().equals(login)) {
                if (userAccessInfo.getValue().getExpirationTime() > System.currentTimeMillis()) {
                    return userAccessInfo.getKey();
                }
            }
        }

        final String token = UUID.randomUUID().toString();
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

    public Map<String, Integer> getAnswerProximity() {
        return answerProximity;
    }

    private void writeToFile() {
        //
    }

    public Set<String> getSyncForChat(String chatID) {
        Set<String> resultSync = new HashSet<>();
        resultSync.add(chatID);

        Set<String> chatsSync = syncChats.get(chatID);
        if (chatsSync != null) {

            for (String syncChat : chatsSync) {
                if (syncChats.get(syncChat) != null) {
                    if (syncChats.get(syncChat).contains(chatID)) {
                        resultSync.add(syncChat);
                    }
                }
            }
        }
        return resultSync;
    }

    public boolean syncChat(String chatId, String withChatId) {
        Set<String> chatSet = syncChats.get(chatId);
        if (chatSet == null) {
            chatSet = new HashSet<>();
            syncChats.put(chatId, chatSet);
        }
        chatSet.add(withChatId);

        chatSet = syncChats.get(withChatId);
        if (chatSet != null) {
            if (chatSet.contains(chatId)) {
                return true;
            }
        }
        return false;
    }
}
