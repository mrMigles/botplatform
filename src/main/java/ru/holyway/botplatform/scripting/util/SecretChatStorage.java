package ru.holyway.botplatform.scripting.util;

import ru.holyway.botplatform.core.data.DataHelper;

public class SecretChatStorage {

  private final DataHelper dataHelper;

  public SecretChatStorage(final DataHelper dataHelper) {
    this.dataHelper = dataHelper;
  }

  public TextJoiner get(String key) {
    return TextJoiner.text(ctx -> dataHelper.getFromSecretStorage(ctx.message.messageEntity.getChatId(), key) != null
        ? dataHelper.getFromSecretStorage(ctx.message.messageEntity.getChatId(), key) : dataHelper.getFromSecretStorage(String.valueOf(ctx.script.getOwner()), key));
  }
}
