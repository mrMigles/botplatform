package ru.holyway.botplatform.core.data;

import ru.holyway.botplatform.core.entity.JSettings;
import ru.holyway.botplatform.core.entity.Record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by seiv0814 on 12-10-17.
 */
public class MemoryDataHelper implements DataHelper {

    private Map<String, List<String>> learnChat = new HashMap<>();
    private List<String> simpleChat = new ArrayList<>();
    private JSettings jSettings = new JSettings();
    private List<Record> records = new ArrayList<>();


    @Override
    public Map<String, List<String>> getLearn() {
        return learnChat;
    }

    @Override
    public void updateLearn(Map<String, List<String>> learnMap) {
        learnChat.putAll(learnMap);
    }

    @Override
    public List<String> getSimple() {
        return simpleChat;
    }

    @Override
    public JSettings getSettings() {
        return jSettings;
    }

    @Override
    public void updateSettings() {

    }

    @Override
    public List<Record> getRecords() {
        return records;
    }

    @Override
    public void updateRecords(List<Record> records) {

    }
}
