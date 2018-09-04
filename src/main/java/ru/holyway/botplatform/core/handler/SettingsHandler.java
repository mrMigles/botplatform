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
          + "Пахом - 2 серия.\n" +
          "`Пахом, -` - выключить для данного чата\n" +
          "`Пахом, +` - включить для данного чата\n" +
          "`Пахом, что такое [слово]?` - попытаюсь объяснить, если знаю\n" +
          "`Пахом, сделай мем` иди /meme - могу сделать мем по картинке\n" +
          "/secret - секрктный контекст. Стераю все сообщения между первым и вторым вызовом данной команды.\n" +
          "/anon - анонимный контекст. Анонимизирую все сообщения, начиная с ввода данной команды.\n" +
          "`@all` - вызвать всех в чате\n" +
          "`/stats` - Статистика по чату\n" +
          "`Пахом, кик` + reply сообщения - Создаёт голосование на временный бан отправителя пересланного сообщения\n" +
          "`Пахом, старт [ИМЯ]` - Начать считать для указанного имени\n" +
          "`Пахом, стоп [ИМЯ]` - Остановить счёт для указанного имени\n" +
          "`Пахом, статистика [ИМЯ]` - Показать все результатты для имени\n" +
          "`Пахом, рекорд` - Показать рекорды подсчётов\n" +
          "`Пахом, ид` - показать ID данного чата\n";
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
