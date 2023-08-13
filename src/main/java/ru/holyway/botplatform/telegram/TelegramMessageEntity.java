package ru.holyway.botplatform.telegram;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.logging.log4j.util.Strings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.core.MessageEntity;

import java.lang.reflect.Method;

/**
 * Created by Sergey on 1/17/2017.
 */
public class TelegramMessageEntity implements MessageEntity {

  private final Message message;

  private final CallbackQuery callbackQuery;

  private final AbsSender sender;

  public TelegramMessageEntity(Message message, CallbackQuery callbackQuery, AbsSender sender) {

    Enhancer enhancer = new Enhancer();
    enhancer.setClassLoader(Message.class.getClassLoader());
    enhancer.setSuperclass(Message.class);
    enhancer.setCallback(new CustomMethodInterceptor(message));

    this.message = (Message) enhancer.create();
    this.callbackQuery = callbackQuery;
    this.sender = sender;
  }

  @Override
  public String getText() {
    return Strings.isNotBlank(message.getText()) ? message.getText() : message.getCaption();
  }

  @Override
  public String getSenderName() {
    return message.getFrom().getFirstName() + " " + message.getFrom().getLastName();
  }

  @Override
  public String getSenderLogin() {
    return message.getFrom().getUserName();
  }

  @Override
  public String getChatId() {
    return String.valueOf(message.getChatId());
  }

  @Override
  public void reply(String text) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.enableMarkdown(true);
    sendMessage.setChatId(getChatId());
    sendMessage.setText(text);
    try {
      sender.execute(sendMessage);
    } catch (TelegramApiException telegramApiException) {
      telegramApiException.printStackTrace();
    }
  }

  public Message getMessage() {

    return message;
  }

  public CallbackQuery getCallbackQuery() {
    return callbackQuery;
  }

  public AbsSender getSender() {
    return sender;
  }
}


class CustomMethodInterceptor implements MethodInterceptor {
  private final Message originalObject;

  public CustomMethodInterceptor(Message originalObject) {
    this.originalObject = originalObject;
  }

  @Override
  public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
    if (method.getName().equals("getText")) {
      return Strings.isNotBlank(originalObject.getText()) ? originalObject.getText() : originalObject.getCaption();
    } else if (method.getName().equals("getReplyToMessage")) {
      Message replyToMessage = originalObject.getReplyToMessage();
      if (replyToMessage != null) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(Message.class.getClassLoader());
        enhancer.setSuperclass(Message.class);
        enhancer.setCallback(new CustomMethodInterceptor(replyToMessage));

        replyToMessage = (Message) enhancer.create();
      }
      return replyToMessage;
    } else {
      // Delegate the rest of the methods to the original object
      return method.invoke(originalObject, args);
    }
  }
}
