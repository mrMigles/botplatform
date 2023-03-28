package ru.holyway.botplatform.core.entity;

import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * Created by Sergey on 4/24/2017.
 */
public class SimpleDictionary {

  @Id
  public String id;

  public List<String> dictionary;

  public SimpleDictionary(String id, List<String> dictionary) {
    this.id = id;
    this.dictionary = dictionary;
  }

  public SimpleDictionary() {

  }
}
