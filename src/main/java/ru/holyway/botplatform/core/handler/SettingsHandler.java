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
          "/script - help для скриптинга\n" +
          "`/follow INSTAGRAM_USER` - Подписаться чатом на instagram аккаунт\n" +
          "`/followy YOUTUBE_CHANEL` - Подписаться чатом на youtube канал\n" +
          "`/followt TWITTER_USER` - Подписаться чатом на twitter аккаунт\n" +
          "`/unfollow[t,y] NAME` - Отписаться\n" +
          "`Пахом, ид` - показать ID данного чата\n";
    }

    if (StringUtils.containsIgnoreCase(mes, "/script")) {
      return "Скриптинг позволяет создавать, хранить и кастомизировать фичи Пахома в рантайме и без передеплоя.\n"
          + "Пример скипта: \n`script().when(message.text.eqic(\"Привет\").or(message.text.eqic(\"Хай\"))).then(message.reply(text(\"И тебе привет, \").add(message.user.value()).value()).andThen(message.sendSticker(\"CAADAgADFQADu1dTA7RfTc0IAoxIAg\")))`\n\n"
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
          + "- `telegram.send(chatID, text message)` - отправить сообщение в другой чат\n"
          + "- `sout(function)` - не возвращает ничего, просто выполняет функцию\n\n"
          + "Для получения значений необходимо использовать функции:\n"
          + "- `message.text.value()` - возвращает текст сообщения.\n"
          + "- `.value() - также можно использовать для получения значения других сущностей (`message.user`, `reply.text` ...).`"
          + "- `message.text.regexp(regexp, num of group)` - возвращает группу для регулярки.\n"
          + "- `now().value()` - возвращает текущее время. Может быть изменено методами `now().hours(num of hours).value()`\n"
          + "- `text(some text).add(another text).value()` - используется для конкатенкции. \n"
          + "- `text(function).eq|eqic(text)...` также можно использвовать для применения предикатов для результатов функции\n"
          + "- `number(some text or function value).[add|divide|multiply|subtract|(number value)].value()` - используется для конкатенкции. \n"
          + "- `number(some text or function value).[eq|gt|gtoe(number value)].value()` - возвращает предикат, примененный к функции или значению. \n"
          + "- `random(0, 50)` - рандомное значение от 0 до 50. Для конверации в число можно использовать `.asNumber()`\n"
          + "- `request.post|get(http://url).header(name, value).param(name, value).param(name, value).body(text).[asJson(\"$.param\")|.asHtml(startTag, endTag)|.asString()].value()` - возвращает результат выполнения http запроса с возможностью выборки.\n\n"
          + "Команды:\n"
          + "`/list_scripts` - выводит список скриптов для чата\n"
          + "`/clear_scripts` - очищает все скрипты для чата\n"
          + "`/remove_script` - удаляет скприт (применимо только к пересланному сообщению со скриптом)\n\n"
          + "И да поможет вам DEBUG! Да не забудешь ты использовать `.value()` для получения значения!\n\n"
          + "P.S. Синтаксис. Да - сложно, но тыж программист \uD83D\uDE0A \nНа самом деле стоит учитывать, что groovy/java скрипты компилируются в рантайме и должны"
          + " быть достаточно безопасными. Предикатно/функциональный подход с заранее определенным АПИ пока единсвенное видимое решение. "
          + " Синтаксис ± приближен к Java QueryDSL, надеюсь это поможет.";
    }
    if (StringUtils.containsIgnoreCase(mes, "Пахом, процент")) {
      int percent =
          settings.getAnswerProximity(chatId) == null ? 15 : settings.getAnswerProximity(chatId);
      return percent + "%";
    }
    if (StringUtils.equals(mes, "Пахом, ид") || mes.equals("/id")) {
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
