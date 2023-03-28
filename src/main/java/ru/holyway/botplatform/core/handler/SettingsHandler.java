package ru.holyway.botplatform.core.handler;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.MessageEntity;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.core.entity.JSettings;
import ru.holyway.botplatform.scripting.MetricCollector;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import javax.annotation.PostConstruct;

/**
 * Created by seiv0814 on 10-10-17.
 */
@Component
public class SettingsHandler implements MessageHandler {

  @Autowired
  private DataHelper dataHelper;

  private JSettings settings;

  @PostConstruct
  public void postConstruct() {
    settings = dataHelper.getSettings();
  }


  @Override
  public String provideAnswer(final MessageEntity messageEntity) {

    final String mes = messageEntity.getText();
    final String chatId = messageEntity.getChatId();

    if (StringUtils.containsIgnoreCase(mes, "Пахом, -")) {
      addToMute(chatId);
      return "Ну.. если хочешь, могу полочать!";
    }
    if (StringUtils.containsIgnoreCase(mes, "Пахом, +")) {
      removeFromMute(chatId);
      return "О, братишка, я вернулся!";
    }
    if (StringUtils.equalsIgnoreCase(mes, "/help@pakhom_bot") || StringUtils.equalsIgnoreCase(mes, "/help")) {
      return "Пахом Bot - 2 серия.\n\nПеть я больше не умею... но умею гораздо больше.\n"
          + "" +
          "/script - help для скриптинга, основной и неповторимый функционал бота\n" +
          "`Пахом, сделай мем` иди /meme - могу сделать мем по картинке\n" +
          "/secret - секретный контекст. Стираю все сообщения между первым и вторым вызовом данной команды.\n" +
          "/anon - анонимный контекст. Делаю анонимными все сообщения, начиная с ввода данной команды.\n" +
          "/all или `@all` - вызвать всех в чате\n" +
          "`Пахом, кик` + reply сообщения - Создаёт голосование на временный бан отправителя пересланного сообщения\n" +
          "/id - показать ID данного чата или сообщения в реплай\n";
    } else if (StringUtils.equalsIgnoreCase(mes, "/script@pakhom_bot") || StringUtils.equalsIgnoreCase(mes, "/script")) {
      return "Скриптинг позволяет создавать, хранить и кастомизировать фичи Пахома в рантайме.\n"
          + "Пример скипта: \n`script().when(message.text.eqic(\"Привет\").or(message.text.eqic(\"Хай\"))).then(message.reply(text(\"И тебе привет, \").add(message.user)).andThen(message.sendSticker(\"CAADAgADFQADu1dTA7RfTc0IAoxIAg\")))`\n\n"
          + "- `.where(predicate)` - содержит предикаты, объединенные по условию `.and(predicate)/.or(predicate)`, которые говорят о том, на что должен реагировать скрипт.\n"
          + "- `.then(consumer)` - содержит саму реакцию, действие, которое должно быть выполнено. Может быть объеденено, с использованием `.andThen(consumer)`\n\n"
          + "Частичный список возможных предикатов:\n"
          + "- `any()` - всегда `true`\n"
          + "- `message.text.[eq|eqic|contains|startWith|matches](string)` - применимо к тексту сообщения\n"
          + "- `reply.text.[eq|eqic|contains|startWith|matches](string)` - применимо к тексту пересланного сообщения\n"
          + "- `message.text.[isReply()|isForward()]` - является ли сообщение реплаем или форвардом\n"
          + "- `message.user.[eq|eqic|contains|startWith|matches](string)` - применимо к отправителю сообщения\n"
          + "- `reply.user.[eq|eqic|contains|startWith|matches](string)` - применимо к отправителю пересланного сообщения\n"
          + "- `message.[hasSticker()|.hasSticker(file id)] - проверка на стикер`\n\n"
          + "Список возможных действий:\n"
          + "- `message.[send|reply(text message)|.sendSticker(sticker file id)|.delete()]` - работа с сообщениями в данном чате\n"
          + "- `message.[.sendMedia(file url)|.sendVideo(video or gif url)]` - позвляет прикреплять видео или фото к чату\n"
          + "- `telegram.send(chatID, text message)` - отправить сообщение в другой чат\n"
          + "- `sout(function)` - не возвращает ничего, просто выполняет функцию\n\n"
          + "Для получения значений необходимо использовать функции:\n"
          + "- `message.text` - возвращает текст сообщения.\n"
          + "- `message.text.regexp(regexp, num of group)` - возвращает группу для регулярки.\n"
          + "- `now()` - возвращает текущее время. Может быть изменено методами `now().hours(num of hours)`\n"
          + "- `text(some text).add(another text)` - используется для конкатенкции. \n"
          + "- `cron(\"0 * * * *\")` - создаёт boolean predicate, который срабатывает на принимаемый Cron формат.\n"
          + "- `text(function).eq|eqic(text)...` также можно использвовать для применения предикатов для результатов функции\n"
          + "- `number(some text or function value).[add|divide|multiply|subtract|(number value)].` - используется для конкатенкции. \n"
          + "- `number(some text or function value).[eq|gt|gtoe(number value)]` - возвращает предикат, примененный к функции или значению. \n"
          + "- `random(0, 50)` - рандомное значение от 0 до 50. Для конверации в число можно использовать `.asNumber()`\n"
          + "- `request.post|get(http://url).header(name, value).param(name, value).param(name, value).body(text).asJson(\"$.param\")[|.asHtml(startTag, endTag)|.asString()|.asXpath(xpath)]` - возвращает результат выполнения http запроса с возможностью выборки.\n\n"
          + "Команды:\n"
          + "/list - выводит список скриптов для чата\n"
          + "/clear - очищает все скрипты для чата\n"
          + "/remove - удаляет скприт (применимо только к пересланному сообщению со скриптом)\n\n"
          + "/put \"NAME\" \"VALUE\" - сохраняет в секретном контексте пользователя (или чата) переменную и значение. В дальнейшем можно использовать её в своих скриптах, в том числе в других чатах, используя `secret.get(\"NAME\")`.\n\n"
          + "\n\n"
          + "Синтаксис приближен к groovy Shell QueryDSL. Да - сложно. Можешь ознакомиться с примерами: /script_examples";
    } else if (StringUtils.equalsIgnoreCase(mes, "/script_examples@pakhom_bot") || StringUtils.equalsIgnoreCase(mes, "/script_examples")) {
      return "Примеры:\n" +
          "Скрипт, который для любого сообщения, содержащего слово \"Привет\", отвечаетс реплаем \"Привет, {Имя пользователя}!\": \n`script().when(message.text.cic(\"Привет\")).then(message.send(text(\"Привет, \").add(message.user).add(\"!\")))`\n\n" +
          "Скрипт, который проверяет, если сообщение является реплаем и сообщение содержит только \"/delete\", то удаляет реплай сообщение и пишет в чат \"выполнено\":\n `script().when(message.isReply().and(message.text.eq(\"/delete\"))).then(reply.delete().andThen(message.send(\"выполнено\")))`\n\n" +
          "Скрипт, который проверяет, если сообщение начинается с \"/what \", то отправляет POST запрос на адрес http://example.com/api/post с content хидером json и телом, содержащим текст сообщения без \"/what \", достаёт из ответа поле $.response.text и отправляет в реплай к сообщению:\n `script().when(message.text.startWith(\"/what \")).then(message.send(request().post(\"http://example.com/api/post\").header(\"Content-Type\",\"application/json\").body(message.text.replace(\"/what \", \"\")).asJson(\"$.response.text\")))`";
    } else if (StringUtils.equalsIgnoreCase(mes, "/metrics@pakhom_bot") || StringUtils.equalsIgnoreCase(mes, "/metrics")) {
      return MetricCollector.getInstance().getExecutionInfo();
    }
    if (StringUtils.equals(mes, "Пахом, ид") || mes.equals("/id")) {
      if (messageEntity instanceof TelegramMessageEntity && ((TelegramMessageEntity) messageEntity).getMessage().isReply()) {
        return String.valueOf(((TelegramMessageEntity) messageEntity).getMessage().getReplyToMessage().getMessageId());
      }
      return chatId;
    }
    return null;
  }

  private void addToMute(String chatID) {
    if (!settings.getMuteChats().contains(chatID)) {
      settings.addMuteChat(chatID);
      dataHelper.updateSettings();
    }

  }

  private void removeFromMute(String chatID) {
    if (settings.getMuteChats().contains(chatID)) {
      settings.removeMuteChat(chatID);
      dataHelper.updateSettings();
    }
  }

}
