package ru.holyway.botplatform.core.entity;

import java.util.List;
import org.springframework.data.annotation.Id;

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
