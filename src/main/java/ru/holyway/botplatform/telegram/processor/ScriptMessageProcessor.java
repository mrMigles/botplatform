package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.scripting.entity.MessageScriptEntity;
import ru.holyway.botplatform.scripting.Script;
import ru.holyway.botplatform.scripting.ScriptCompiler;
import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.TelegramScriptEntity;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

@Component
@Order(99)
public class ScriptMessageProcessor implements MessageProcessor {

  private ScriptCompiler scriptCompiler;
  private MultiValueMap<String, Script> scripts = new LinkedMultiValueMap<>();

  public ScriptMessageProcessor(ScriptCompiler scriptCompiler) {
    this.scriptCompiler = scriptCompiler;
  }

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    return CollectionUtils.isNotEmpty(scripts.get(messageEntity.getChatId())) || (
        messageEntity.getMessage().hasText() && messageEntity.getMessage().getText()
            .startsWith("script()"));
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    if (messageEntity.getMessage().hasText() && messageEntity.getMessage().getText()
        .startsWith("script()")) {
      final Script script = scriptCompiler.compile(messageEntity.getText());
      scripts.add(messageEntity.getChatId(), script);
      messageEntity.getSender()
          .execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Ok"));
      return;
    }

    for (Script script : scripts.get(messageEntity.getChatId())) {
      MessageScriptEntity message = new MessageScriptEntity(messageEntity);
      TelegramScriptEntity telegram = new TelegramScriptEntity(messageEntity.getSender());
      ScriptContext ctx = new ScriptContext(message, telegram);
      try {
        if (script.check(ctx)) {
          script.execute(ctx);
          return;
        }
      } catch (Exception e) {
        System.out.println(e);
      }
    }
  }

  @Override
  public boolean isRegardingCallback(CallbackQuery callbackQuery) {
    return false;
  }

  @Override
  public void processCallBack(CallbackQuery callbackQuery, AbsSender sender)
      throws TelegramApiException {

  }
}
