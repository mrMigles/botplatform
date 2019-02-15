package ru.holyway.botplatform.core.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.MessageEntity;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.core.entity.JSettings;

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
    if (StringUtils.containsIgnoreCase(mes, "/help")) {
      return "Петь я больше не умею, в прочем, как и говорить...\n"
          + "Пахом Bot - 2 серия.\n\n" +
          "`Пахом, что такое [слово]?` - попытаюсь объяснить, если знаю\n" +
          "`Пахом, сделай мем` иди /meme - могу сделать мем по картинке\n" +
          "/secret - секретный контекст. Стираю все сообщения между первым и вторым вызовом данной команды.\n" +
          "/anon - анонимный контекст. Делаю анонимными все сообщения, начиная с ввода данной команды.\n" +
          "`@all` - вызвать всех в чате\n" +
          "`Пахом, кик` + reply сообщения - Создаёт голосование на временный бан отправителя пересланного сообщения\n" +
          "`/follow INSTAGRAM_USER` - Подписаться чатом на instagram аккаунт\n" +
          "`/followy YOUTUBE_CHANEL` - Подписаться чатом на youtube канал\n" +
          "`/followt TWITTER_USER` - Подписаться чатом на twitter аккаунт\n" +
          "`/unfollow[t,y] NAME` - Отписаться\n" +
          "`Пахом, ид` - показать ID данного чата\n";
    }

    if (StringUtils.containsIgnoreCase(mes, "/script")) {
      return "Скриптинг позволяет Вам конфигурировать фичи Пахома.\n"
          + "Пример скипта: \n`script().when(ctx.message.text.eq(\"Привет\")).then(ctx.message.reply(text(\"И тебе привет, \").add(ctx.message.user.value()).value()))`\n\n"
          + "`.where(predicate)` - содержит предикаты, объединенные по условию `.and(predicate)/.or(predicate)`, которые говорят о том, на что должен реагировать скрипт.\n"
          + "`.then(consumer)` - содержит саму реакцию, действие, которое должно быть выполнено. Может быть объеденено, с использованием `.andThen(consumer)`\n\n"
          + "Частичный список возможных предикатов:\n"
          + "`any()` - всегда `true`\n"
          + "`ctx.message.text.eq|eqic|contains|startWith|matches|(string)` - применимо к тексту сообщения\n"
          + "`ctx.message.user.eq|eqic|contains|startWith|matches|(string)` - применимо к отправителю сообщения\n"
          + "`ctx.message.hasSticker()|.hasSticker(file id)`\n\n"
          + "Список возможных действий:\n"
          + "`ctx.message.send|reply(text message)|.delete()` - работа с сообщениями в данном чате\n"
          + "`ctx.telegram.send(chatID, text message)` - отправить сообщение в другой чат\n"
          + "`sout(function)` - не возвращает ничего, просто выполняет функцию\n\n"
          + "Для получения значений необходимо использовать функции:\n"
          + "`ctx.message.text.value()` - возвращает текст сообщения.\n"
          + "`ctx.message.text.regexp(regexp, num of group)` - возвращает группу для регулярки.\n"
          + "`now().value()` - возвращает текущее время. Может быть изменено методами `now().hours(num of hours).value()`\n"
          + "`text(some text).add(another text).value()` - используется для конкатенкции.\n"
          + "`post(http://url).header(name, value).param(name, value).param(name, value).body(text).asJson(name of JSON field)|.asHtml(startTag, endTag)|.asString()` - возвращает результат выполнения запроса с возможностью выборки.\n"
          + "Команды:"
          + "`/list_scripts` - выводит список скриптов для чата\n"
          + "`/clear_scripts` - очищает все скрипты для чата\n"
          + "`/remove_script` - удаляет скприт (применимо только к пересланному сообщению со скриптом)\n\n"
          + "И да поможет вам DEBUG! Да не забудешь ты использовать `.value()` для получения значения!";
    }
    if (StringUtils.containsIgnoreCase(mes, "Пахом, процент")) {
      int percent =
          settings.getAnswerProximity(chatId) == null ? 15 : settings.getAnswerProximity(chatId);
      return percent + "%";
    }
    if (StringUtils.equals(mes, "Пахом, ид") || StringUtils
        .containsIgnoreCase(mes, "Пахом, что это за чат?")) {
      return chatId;
    }
    if (StringUtils.containsIgnoreCase(mes, "Пахом, умный")) {
      addToEazy(chatId);
      return "Разговариваю только обученными фразами.";
    }
    if (StringUtils.containsIgnoreCase(mes, "Пахом, всякий")) {
      removeFromEazy(chatId);
      return "Режим собеседника активирован, братишка.";
    }
    if (StringUtils.containsIgnoreCase(mes, "Пахом,") && mes.endsWith("%")) {
      try {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(mes);
        if (matcher.find(0)) {
          String value = mes.substring(matcher.start(), matcher.end());
          int ansPer = Integer.parseInt(value);
          if (ansPer >= 0 && ansPer <= 100) {
            settings.setProximityAnswer(chatId, ansPer);
            dataHelper.updateSettings();
            return "ок";
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return "Тебя разве не учили в 6м училище, что такое проценты?";

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

  private void addToEazy(String chatID) {
    if (!settings.getEasyChats().contains(chatID)) {
      settings.addEasyChat(chatID);
      dataHelper.updateSettings();
    }

  }

  private void removeFromEazy(String chatID) {
    if (settings.getEasyChats().contains(chatID)) {
      settings.removeEasyChat(chatID);
      dataHelper.updateSettings();
    }
  }

}
