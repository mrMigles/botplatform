package ru.holyway.botplatform.core.handler;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.MessageEntity;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.core.education.EducationActiveException;
import ru.holyway.botplatform.core.education.EducationCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seiv0814 on 10-10-17.
 */
@Component
public class EducationHandler implements MessageHandler {

    @Autowired
    private EducationCache educationCache;

    @Autowired
    private DataHelper dataHelper;

    @Override
    public String provideAnswer(final MessageEntity messageEntity) {
        final String mes = messageEntity.getText();
        final String chatId = messageEntity.getChatId();
        if (StringUtils.containsIgnoreCase(mes, "Пахом, учись")) {
            educationCache.getLearningChats().add(chatId);
            return "Приступил к обучению, пожалуйста, аккуратнее с выражениями.";
        }
        if (StringUtils.containsIgnoreCase(mes, "Пахом, отдыхай")) {
            educationCache.getLearningChats().remove(chatId);
            writeNew();
            educationCache.init();
            return "Уф! Пошёл переваривать информацию.";
        }
        if (StringUtils.containsIgnoreCase(mes, "Пахом, забудь")) {
            if (educationCache.getListCurrentLearning().get(chatId) == null) {
                return "То, что сказано не было... быть забытым не может!.";
            }
            int currentCount = educationCache.getListCurrentLearning().get(chatId).size();
            if (mes.length() > 14) {
                int toDelete = Integer.parseInt(mes.substring(14, 15));
                if (currentCount - educationCache.getLearningDictionary().get(chatId).size() < toDelete) {
                    toDelete = currentCount - educationCache.getDictionarySize().get(chatId);
                }
                for (int i = educationCache.getListCurrentLearning().size() - 1; i > currentCount - toDelete; i--) {
                    educationCache.getListCurrentLearning().remove(i);
                }
                return "Я ничего не видел, нет... нет.";
            } else {
                return "Не понимаю...";
            }
        }
        if (educationCache.getLearningChats().contains(chatId) && !StringUtils.containsIgnoreCase(mes, "Пахом,")) {
            for (String chatWithSync : dataHelper.getSettings().getSyncForChat(chatId)) {
                List<String> current = educationCache.getListCurrentLearning().get(chatWithSync);
                if (current == null) {
                    current = new ArrayList<>();
                }
                current.add(mes);
                educationCache.getListCurrentLearning().put(chatWithSync, current);
            }
            throw new EducationActiveException("education is active for chat: " + chatId);
        }
        return null;
    }

    private synchronized void writeNew() {
        try {
            dataHelper.updateLearn(educationCache.getListCurrentLearning());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
