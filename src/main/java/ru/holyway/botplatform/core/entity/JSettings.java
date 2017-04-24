package ru.holyway.botplatform.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import ru.holyway.botplatform.core.data.DataHelper;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sergey on 1/18/2017.
 */
public class JSettings {

    private Set<String> muteChats;
    private Set<String> easyChats;
    private Map<String, Integer> answerProximity;

    @Id
    public String id;

    public JSettings(Set<String> muteChats, Set<String> easyChats, Map<String, Integer> answerProximity) {
        this.muteChats = muteChats;
        this.easyChats = easyChats;
        this.answerProximity = answerProximity;
    }

    public JSettings() {
        muteChats = new HashSet<>();
        easyChats = new HashSet<>();
        answerProximity = new HashMap<>();
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
}
