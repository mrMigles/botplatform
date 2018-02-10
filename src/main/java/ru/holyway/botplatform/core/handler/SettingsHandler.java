package ru.holyway.botplatform.core.handler;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.MessageEntity;
import ru.holyway.botplatform.core.data.DataService;
import ru.holyway.botplatform.core.entity.JSettings;

import javax.annotation.PostConstruct;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by seiv0814 on 10-10-17.
 */
@Component
public class SettingsHandler implements MessageHandler {

    @Autowired
    private DataService dataService;

    private JSettings settings;

    @PostConstruct
    public void postConstruct() {
        settings = dataService.getSettings();
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
            return "Ооой, я много что умею, ну хочешь я спою?\n" +
                    "Пахом, [любая фраза] - и я попробую ответить\n" +
                    "Пахом, учись - включить режим обучения\n" +
                    "Пахом, отдыхай - выключить режим обучения и запомнить фразы\n" +
                    "Пахом, забудь [n] - исключить последние n-фраз во время обучения\n" +
                    "Пахом, анализ - бесполезная херота\n" +
                    "Пахом, - - выключить для данного чата\n" +
                    "Пахом, + - включить для данного чата\n" +
                    "Пахом, умный - использовать только обученные фразы\n" +
                    "Пахом, глупый - использовать обученные и заскриптованные фразы\n" +
                    "Пахом, [0-100]% - установить вероятность случайного ответа в значение []\n" +
                    "Пахом, процент - показать процент ответа для данного чата\n" +
                    "Пахом, что такое [слово]? - попытаюсь объяснить, если знаю\n" +
                    "Пахом, ид - показать ID данного чата\n" +
                    "Пахом, миграция ID-чата - миграция обучения из определенного чата\n";
        }
        if (StringUtils.containsIgnoreCase(mes, "Пахом, процент")) {
            int percent = settings.getAnswerProximity(chatId) == null ? 15 : settings.getAnswerProximity(chatId);
            return percent + "%";
        }
        if (StringUtils.equals(mes, "Пахом, ид") || StringUtils.containsIgnoreCase(mes, "Пахом, что это за чат?")) {
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
                        dataService.updateSettings();
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
            dataService.updateSettings();
        }

    }

    private void removeFromMute(String chatID) {
        if (settings.getMuteChats().contains(chatID)) {
            settings.removeMuteChat(chatID);
            dataService.updateSettings();
        }
    }

    private void addToEazy(String chatID) {
        if (!settings.getEasyChats().contains(chatID)) {
            settings.addEasyChat(chatID);
            dataService.updateSettings();
        }

    }

    private void removeFromEazy(String chatID) {
        if (settings.getEasyChats().contains(chatID)) {
            settings.removeEasyChat(chatID);
            dataService.updateSettings();
        }
    }

}
