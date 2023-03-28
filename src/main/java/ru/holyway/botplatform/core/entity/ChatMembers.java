package ru.holyway.botplatform.core.entity;

import org.springframework.data.annotation.Id;

import java.util.List;

public class ChatMembers {

  @Id
  public String id;

  public List<String> members;

  public ChatMembers(String id, List<String> members) {
    this.id = id;
    this.members = members;
  }

  public ChatMembers() {
  }
}
