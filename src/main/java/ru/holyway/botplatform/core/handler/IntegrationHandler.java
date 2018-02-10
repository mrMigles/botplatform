package ru.holyway.botplatform.core.handler;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.MessageEntity;
import ru.holyway.botplatform.core.data.DataService;
import ru.holyway.botplatform.core.education.EducationCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seiv0814 on 10-10-17.
 */
@Component
public class IntegrationHandler implements MessageHandler {

    @Autowired
    private EducationCache educationCache;

    @Autowired
    private DataService dataService;

    @Override
    public String provideAnswer(final MessageEntity messageEntity) {
        final String mes = messageEntity.getText();
        final String chatId = messageEntity.getChatId();
        if (StringUtils.containsIgnoreCase(mes, "Пахом, миграция ")) {
            if (mes.length() > 17) {
                final String migChatId = mes.substring(16);
                List<String> dictionary = educationCache.getLearningDictionary().get(migChatId);
                if (dictionary != null) {
                    List<String> curDictionary = educationCache.getListCurrentLearning().get(chatId);
                    if (curDictionary == null) {
                        curDictionary = new ArrayList<>();
                    }
                    curDictionary.addAll(dictionary);
                    educationCache.getListCurrentLearning().put(chatId, curDictionary);
                    writeNew();
                    educationCache.init();
                    return "Готово, братишка";
                }
            }
            return "Чет не понял";
        }
        if (StringUtils.containsIgnoreCase(mes, "Пахом, синхронизация ")) {
            try {

                if (mes.length() > 22) {
                    final String migChatId = mes.substring(21);

                    if (dataService.getSettings().syncChat(chatId, migChatId)) {
                        dataService.updateSettings();
                        return "Синхроинзация установлена";
                    } else {
                        dataService.updateSettings();
                        return "Запрос на синхронизацию получен, повторите команду на стороне чата " + migChatId;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Чет не понял";
        }
        return null;
    }

    private synchronized void writeNew() {
        try {
            dataService.updateLearn(educationCache.getListCurrentLearning());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
