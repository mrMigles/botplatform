package ru.holyway.botplatform.core.data;

import org.apache.commons.collections.map.MultiKeyMap;
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
  private Map<String, List<String>> chatMembersMap = new HashMap<>();
  private MultiKeyMap scriptMap = new MultiKeyMap();
  private MultiKeyMap secretStorage = new MultiKeyMap();


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

  @Override
  public List<String> getChatMembers(String chatId) {
    return chatMembersMap.get(chatId);
  }

  @Override
  public void updateChatMembers(String chatId, List<String> chatMembers) {
    chatMembersMap.put(chatId, chatMembers);
  }

  @Override
  public void putToScriptMap(Object chatId, Object key, Object value) {
    scriptMap.put(chatId, key, value);
  }

  @Override
  public void putToSecretStorage(Object chatId, String key, String value) {
    secretStorage.put(chatId, key, value);
  }

  @Override
  public Object getFromScriptMap(Object chatId, Object key) {
    return scriptMap.get(chatId, key);
  }

  @Override
  public String getFromSecretStorage(String chatId, String key) {
    return (String) secretStorage.get(chatId, key);
  }

  @Override
  public Map<String, String> listSecretStorage(String chatId) {
    return (Map<String, String>) secretStorage.get(chatId);
  }
}
