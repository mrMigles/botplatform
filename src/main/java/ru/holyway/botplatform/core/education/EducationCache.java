package ru.holyway.botplatform.core.education;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.data.DataHelper;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static ru.holyway.botplatform.core.education.EducationUtils.getTokenizeMessage;

/**
 * Created by seiv0814 on 10-10-17.
 */
@Component
public class EducationCache {

  private static final Logger LOGGER = LoggerFactory.getLogger(EducationCache.class);

  private ConcurrentMap<String, List<List<String>>> learningTokenizedDictionary = new ConcurrentHashMap<>();

  private ConcurrentMap<String, List<String>> listCurrentLearning = new ConcurrentHashMap<>();

  private ConcurrentMap<String, List<String>> learningDictionary = new ConcurrentHashMap<>();

  private List<List<String>> simpleDictionary = new ArrayList<>();
  private List<String> list2Easy = new ArrayList<>();

  private Set<String> learningChats = new HashSet<>();

  private Map<String, Integer> dictionarySize = new HashMap<>();


  @Autowired
  private DataHelper dataHelper;

  @PostConstruct
  public void postConstruct() {
    init();
  }

  public synchronized void init() {

    learningTokenizedDictionary.clear();
    learningDictionary.clear();
    simpleDictionary.clear();
    list2Easy.clear();
    dictionarySize.clear();

    Map<String, List<String>> learnWords = null;

    List<String> simpleWords = null;
    try {
      learnWords = dataHelper.getLearn();
      simpleWords = dataHelper.getSimple();

    } catch (Exception e) {
      LOGGER.error("Error loading learning data", e);
    }
    for (Map.Entry<String, List<String>> line : learnWords.entrySet()) {
      final List<List<String>> tokenizedAnswers = new ArrayList<>();
      final List<String> notEmptyStrings = new ArrayList<>();
      for (String lineStr : line.getValue()) {
        if (lineStr != null) {
          if (lineStr.length() > 1) {
            tokenizedAnswers.add(getTokenizeMessage(lineStr));
            notEmptyStrings.add(lineStr);
          }
        }
      }
      learningDictionary.put(line.getKey(), notEmptyStrings);
      learningTokenizedDictionary.put(line.getKey(), tokenizedAnswers);
      dictionarySize.put(line.getKey(), notEmptyStrings.size());
    }
    if (listCurrentLearning.size() == 0) {
      listCurrentLearning.putAll(learningDictionary);
    }

    for (String line : simpleWords) {
      if (line.length() > 1) {
        simpleDictionary.add(getTokenizeMessage(line));
        list2Easy.add(line);
      }
    }
  }


  public ConcurrentMap<String, List<List<String>>> getLearningTokenizedDictionary() {
    return learningTokenizedDictionary;
  }

  public ConcurrentMap<String, List<String>> getLearningDictionary() {
    return learningDictionary;
  }

  public List<List<String>> getSimpleDictionary() {
    return simpleDictionary;
  }

  public List<String> getList2Easy() {
    return list2Easy;
  }

  public Set<String> getLearningChats() {
    return learningChats;
  }

  public ConcurrentMap<String, List<String>> getListCurrentLearning() {
    return listCurrentLearning;
  }

  public Map<String, Integer> getDictionarySize() {
    return dictionarySize;
  }
}
