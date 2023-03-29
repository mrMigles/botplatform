package ru.holyway.botplatform.scripting.util;

import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.scripting.ScriptContext;

import java.util.function.Function;

public class SecretChatStorage {

  private final DataHelper dataHelper;

  public SecretChatStorage(final DataHelper dataHelper) {
    this.dataHelper = dataHelper;
  }

  public TextJoiner get(String key) {
    return TextJoiner.text(ctx -> dataHelper.getFromSecretStorage(ctx.message.messageEntity.getChatId(), key) != null
        ? dataHelper.getFromSecretStorage(ctx.message.messageEntity.getChatId(), key) : dataHelper.getFromSecretStorage(String.valueOf(ctx.script.getOwner()), key));
  }

  public TextJoiner get(Function<ScriptContext, Object> key) {
    return TextJoiner.text(ctx -> dataHelper.getFromSecretStorage(ctx.message.messageEntity.getChatId(), String.valueOf(key.apply(ctx))) != null
        ? dataHelper.getFromSecretStorage(ctx.message.messageEntity.getChatId(), String.valueOf(key.apply(ctx))) : dataHelper.getFromSecretStorage(String.valueOf(ctx.script.getOwner()), String.valueOf(key.apply(ctx))));
  }
}
