package ru.holyway.botplatform.scripting;

import java.util.HashMap;
import java.util.Map;
import ru.holyway.botplatform.scripting.entity.ForwardScriptEntity;
import ru.holyway.botplatform.scripting.entity.MessageScriptEntity;
import ru.holyway.botplatform.scripting.entity.ReplyScriptEntity;

public class ScriptContext {

  private Map<String, String> contextMap = new HashMap<>();


  public MessageScriptEntity message = new MessageScriptEntity();

  public TelegramScriptEntity telegram = new TelegramScriptEntity();

  public ReplyScriptEntity reply = new ReplyScriptEntity();

  ForwardScriptEntity forward = new ForwardScriptEntity();

  public ScriptContext() {
  }

  public ScriptContext(MessageScriptEntity message, TelegramScriptEntity telegram) {
    this.message = message;
    this.telegram = telegram;
  }

  public String getContextValue(String key) {
    return contextMap.get(key);
  }

  public void setContextValue(String key, String value) {
    contextMap.put(key, value);
  }
}
