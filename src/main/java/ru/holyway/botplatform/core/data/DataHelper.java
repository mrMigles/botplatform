package ru.holyway.botplatform.core.data;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.entity.Chat;
import ru.holyway.botplatform.core.entity.JSettings;
import ru.holyway.botplatform.core.entity.SimpleDictionary;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sergey on 4/19/2017.
 */
@Component
public class DataHelper {

    private JSettings settings;

    @Autowired
    private ChatRepository repository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private SimpleRepository simpleRepository;

    @Autowired
    private MappingMongoConverter mappingMongoConverter;

    @PostConstruct
    public void postConstruct() {
//
//        Map<String, List<String>> learnWords = null;
//
//        List<String> simpleWords = null;
//
//        GsonBuilder gson = new GsonBuilder();
//        Type collectionType = new TypeToken<HashMap<String, List<String>>>() {
//        }.getType();
//        try {
//            learnWords = gson.create().fromJson(Files.newBufferedReader(Paths.get("G:\\storage\\learnDictionary"), StandardCharsets.UTF_8), collectionType);
//            simpleWords = Files.readAllLines(Paths.get("G:\\storage\\simpleDictionary"), StandardCharsets.UTF_8);
//
//            List<Chat> chats = new ArrayList<>();
//            for (Map.Entry<String, List<String>> entry : learnWords.entrySet()) {
//                chats.add(new Chat(entry.getKey(), entry.getValue()));
//            }
//            repository.save(chats);
//
//            SimpleDictionary simpleDictionary = new SimpleDictionary("1", simpleWords);
//            simpleRepository.save(simpleDictionary);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        mappingMongoConverter.setMapKeyDotReplacement("\\\\+");
    }


    public Map<String, List<String>> getLearn() {
        Map<String, List<String>> result = new HashMap<>();
        for (Chat chat : repository.findAll()) {
            result.put(chat.id, chat.dictionary);
        }
        return result;
    }

    public void updateLearn(Map<String, List<String>> learnMap) {
        List<Chat> chats = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : learnMap.entrySet()) {
            chats.add(new Chat(entry.getKey(), entry.getValue()));
        }
        repository.save(chats);
    }

    public List<String> getSimple() {
        try {
            return simpleRepository.findOne("1").dictionary;
        }
        catch (Exception e){
            return new ArrayList<>();
        }
    }

    public JSettings getSettings() {
        if (this.settings == null) {
            JSettings settings = settingsRepository.findOne("1");
            if (settings == null) {
                settings = new JSettings();
                settings.id = "1";
            }
            this.settings = settings;
        }
        return this.settings;
    }

    public void updateSettings() {
        settingsRepository.save(this.settings);
    }
}
