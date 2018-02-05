package ru.holyway.botplatform.core.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import ru.holyway.botplatform.core.entity.Chat;
import ru.holyway.botplatform.core.entity.ChatMembers;
import ru.holyway.botplatform.core.entity.JSettings;
import ru.holyway.botplatform.core.entity.Record;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by seiv0814 on 12-10-17.
 */
public class MongoDataHelper implements DataHelper {

    private JSettings settings;

    @Autowired
    private ChatRepository repository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private SimpleRepository simpleRepository;

    @Autowired
    private MappingMongoConverter mappingMongoConverter;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private ChatMemberRepository chatMemberRepository;

    @PostConstruct
    public void postConstruct() {
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
        } catch (Exception e) {
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

    public List<Record> getRecords() {
        List<Record> records = recordRepository.findAll();
        Collections.sort(records);
        return records;
    }

    public void updateRecords(List<Record> records) {
        recordRepository.save(records);
    }

    @Override
    public List<String> getChatMembers(String chatId) {
        ChatMembers chatMembers = chatMemberRepository.findById(chatId);
        if (chatMembers != null) {
            return chatMembers.members;
        }
        return null;
    }

    @Override
    public void updateChatMembers(String chatId, List<String> chatMembers) {
        final ChatMembers members = new ChatMembers(chatId, chatMembers);
        chatMemberRepository.save(members);
    }

}
