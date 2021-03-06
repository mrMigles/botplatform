package ru.holyway.botplatform.core.data;

import org.apache.commons.collections.map.MultiKeyMap;
import ru.holyway.botplatform.core.entity.JSettings;
import ru.holyway.botplatform.core.entity.Record;

import java.util.List;
import java.util.Map;

/**
 * Created by Sergey on 4/19/2017.
 */
public interface DataHelper {

  Map<String, List<String>> getLearn();

  void updateLearn(Map<String, List<String>> learnMap);

  List<String> getSimple();

  JSettings getSettings();

  void updateSettings();

  List<Record> getRecords();

  void updateRecords(List<Record> records);

  List<String> getChatMembers(final String chatId);

  void updateChatMembers(final String chatId, List<String> chatMembers);

  void putToScriptMap(Object chatId, Object key, Object value);

  Object getFromScriptMap(Object chatId, Object key);
}
