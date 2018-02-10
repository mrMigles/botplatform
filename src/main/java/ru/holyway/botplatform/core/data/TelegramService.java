package ru.holyway.botplatform.core.data;

import org.springframework.beans.factory.annotation.Autowired;
import ru.holyway.botplatform.core.data.telegram.TelegramRepository;
import ru.holyway.botplatform.core.entity.ChatMembers;
import ru.holyway.botplatform.core.entity.JSettings;
import ru.holyway.botplatform.core.entity.Record;

import java.util.List;
import java.util.Map;

public class TelegramService implements DataService {

    @Autowired
    private TelegramRepository<ChatMembers, String> chatMembersStringTelegramRepository;

    @Override
    public Map<String, List<String>> getLearn() {
        return null;
    }

    @Override
    public void updateLearn(Map<String, List<String>> learnMap) {

    }

    @Override
    public List<String> getSimple() {
        return null;
    }

    @Override
    public JSettings getSettings() {
        return null;
    }

    @Override
    public void updateSettings() {

    }

    @Override
    public List<Record> getRecords() {
        return null;
    }

    @Override
    public void updateRecords(List<Record> records) {

    }

    @Override
    public List<String> getChatMembers(String chatId) {
        return null;
    }

    @Override
    public void updateChatMembers(String chatId, List<String> chatMembers) {

    }
}
