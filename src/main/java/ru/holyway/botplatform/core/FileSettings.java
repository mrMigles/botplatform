package ru.holyway.botplatform.core;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sergey on 1/18/2017.
 */
public class FileSettings implements Settings {
    private Set<String> muteChats = new HashSet<>();
    private Set<String> easyChats = new HashSet<>();
    private Map<String, Integer> answerProximity = new HashMap<>();


    @PostConstruct
    private void init() {
        GsonBuilder gson = new GsonBuilder();
        Type collectionType = new TypeToken<FileSettings>() {
        }.getType();
        try {
            FileSettings settings = gson.create().fromJson(Files.newBufferedReader(Paths.get("C:\\storage\\settings"), StandardCharsets.UTF_8), collectionType);
            if (settings != null) {
                if (settings.answerProximity != null)
                    this.answerProximity = new HashMap<>(settings.answerProximity);
                if (settings.easyChats != null)
                    this.easyChats = new HashSet<>(settings.easyChats);
                if (settings.muteChats != null)
                    this.muteChats = new HashSet<>(settings.muteChats);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addMuteChat(String chatId) {
        muteChats.add(chatId);
        writeToFile();
    }

    @Override
    public void removeMuteChat(String chatId) {
        muteChats.remove(chatId);
        writeToFile();
    }

    @Override
    public void addEasyChat(String chatId) {
        easyChats.add(chatId);
        writeToFile();
    }

    @Override
    public void removeEasyChat(String chatId) {
        easyChats.remove(chatId);
        writeToFile();
    }

    @Override
    public void setProximityAnswer(String chatId, int percent) {
        answerProximity.put(chatId, percent);
        writeToFile();
    }

    @Override
    public Set<String> getMuteChats() {
        return muteChats;
    }

    @Override
    public Set<String> getEasyChats() {
        return easyChats;
    }

    @Override
    public Map<String, Integer> getAnswerProximity() {
        return answerProximity;
    }

    private void writeToFile() {
        try {
            GsonBuilder gson = new GsonBuilder();
            Files.write(Paths.get("C:\\storage\\settings"), gson.create().toJson(this).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
