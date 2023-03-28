package ru.holyway.botplatform.scripting;

import ru.holyway.botplatform.scripting.entity.ForwardScriptEntity;
import ru.holyway.botplatform.scripting.entity.MessageScriptEntity;
import ru.holyway.botplatform.scripting.entity.ReplyScriptEntity;

import java.util.HashMap;
import java.util.Map;

public class ScriptContext {

  private Map<String, String> contextMap = new HashMap<>();

  public MessageScriptEntity message = new MessageScriptEntity();

  public Script script;

  public TelegramScriptEntity telegram = new TelegramScriptEntity();

  public ReplyScriptEntity reply = new ReplyScriptEntity();

  ForwardScriptEntity forward = new ForwardScriptEntity();

  public ScriptContext() {
  }

  public ScriptContext(MessageScriptEntity message, TelegramScriptEntity telegram, Script script) {
    this.message = message;
    this.telegram = telegram;
    this.script = script;
  }

  public String getContextValue(String key) {
    return contextMap.get(key);
  }

  public void setContextValue(String key, String value) {
    contextMap.put(key, value);
  }
}
