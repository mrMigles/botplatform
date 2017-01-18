package ru.holyway.botplatform.core;

import java.util.Map;
import java.util.Set;

/**
 * Created by Sergey on 1/18/2017.
 */
public interface Settings {
    void addMuteChat(String chatId);

    void removeMuteChat(String chatId);

    void addEasyChat(String chatId);

    void removeEasyChat(String chatId);

    void setProximityAnswer(String chatId, int percent);

    Set<String> getMuteChats();

    Set<String> getEasyChats();

    Map<String, Integer> getAnswerProximity();
}
