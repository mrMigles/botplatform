package ru.holyway.botplatform.core.handler;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.MessageEntity;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.core.education.EducationCache;
import ru.holyway.botplatform.core.education.EducationUtils;
import ru.holyway.botplatform.core.entity.JSettings;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by seiv0814 on 10-10-17.
// */
//@Component
public class LogicalAnswerHandler implements MessageHandler {

    @Autowired
    private DataHelper dataHelper;

    @Autowired
    private EducationCache educationCache;


    private JSettings settings;

    @PostConstruct
    public void postConstruct() {
        settings = dataHelper.getSettings();
    }


    @Override
    public String provideAnswer(final MessageEntity messageEntity) {
        final String mes = messageEntity.getText();
        final String chatId = messageEntity.getChatId();
        String rnd = generateZSAnswer(mes, chatId);
        if (StringUtils.isNotBlank(rnd)) {
            return rnd;
        }
        return null;
    }

    private List<String> getAnswersList(String chatID) {
        if (settings.getEasyChats().contains(chatID)) {
            List<String> ansList = new ArrayList<>();
            if (educationCache.getLearningDictionary().get(chatID) != null) {
                ansList.addAll(educationCache.getLearningDictionary().get(chatID));
            }
            return ansList;
        } else {
            List<String> ansList = new ArrayList<>(educationCache.getList2Easy());
            if (educationCache.getLearningDictionary().get(chatID) != null) {
                ansList.addAll(educationCache.getLearningDictionary().get(chatID));
            }
            return ansList;
        }
    }

    private List<List<String>> getQuestionsList(String chatID) {
        if (settings.getEasyChats().contains(chatID)) {
            List<List<String>> qstList = new ArrayList<>();
            if (educationCache.getLearningTokenizedDictionary().get(chatID) != null) {
                qstList.addAll(educationCache.getLearningTokenizedDictionary().get(chatID));
            }
            return qstList;
        } else {
            List<List<String>> qstList = new ArrayList<>(educationCache.getSimpleDictionary());
            if (educationCache.getLearningTokenizedDictionary().get(chatID) != null) {
                qstList.addAll(educationCache.getLearningTokenizedDictionary().get(chatID));
            }
            return qstList;
        }
    }

    private String generateZSAnswer(String mesage, String chatID) {
        if (isNeedReply(mesage, chatID)) {
            mesage = mesage.replaceAll("Пахом,", "").replaceAll("пахом,", "");

            List<String> messageWords = EducationUtils.getTokenizeMessage(mesage);
            List<String> answerList = getAnswersList(chatID);
            List<List<String>> questionList = getQuestionsList(chatID);
            HashMap<Integer, String> answers = new HashMap<>();
            for (int i = 0; i < getQuestionsList(chatID).size(); i++) {
                int n = 0;
                int f = -1;
                ArrayList<String> checkedWords = new ArrayList<>();
                for (int j = 0; j < questionList.get(i).size(); j++) {
                    String curWordInDict = questionList.get(i).get(j);
                    if (containsIgnoreCase(curWordInDict, messageWords)) {
                        if (questionList.get(i).size() == 1 && curWordInDict.length() > 3) {
                            answers.put(curWordInDict.length(), answerList.get(i + 1));
                            break;
                        }
                        if (!containsIgnoreCase(curWordInDict, checkedWords)) {
                            if (f != -1 && j - f > 3) {
                                f = j;
                                n = 1;
                            } else {
                                n++;
                                f = j;
                            }
                            checkedWords.add(curWordInDict);
                        }
                    }
                }
                if (n > 1) {
                    int sum = 0;
                    for (String str : checkedWords) {
                        sum += str.length();
                    }
                    if (sum > 4 && i < answerList.size() - 1) {
                        answers.put(sum * checkedWords.size(), answerList.get(i + 1));
                    }
                }
            }
            if (!answers.isEmpty()) {
                int sum = 0;
                for (Integer i : answers.keySet()) {
                    sum += i;
                }
                ArrayList<String> answersT = new ArrayList<>();
                for (Integer i : answers.keySet()) {
                    if (i >= sum / answers.keySet().size()) {
                        answersT.add(answers.get(i));
                    }
                }
                int i = new Random().nextInt(answersT.size());
                return answersT.get(i);
            }
        }
        return null;
    }

    private boolean isNeedReply(String message, String chatID) {
        if (message.contains("пахом") || message.contains("Пахом")) {
            return true;
        }
        int ansPerc = settings.getAnswerProximity(chatID) == null ? 15 : settings.getAnswerProximity(chatID);
        if (new Random().nextInt(100) > 100 - ansPerc) {
            return true;
        }
        return false;
    }


    private int editdist(String S1, String S2) {
        int m = S1.length(), n = S2.length();
        int[] D1;
        int[] D2 = new int[n + 1];

        for (int i = 0; i <= n; i++)
            D2[i] = i;

        for (int i = 1; i <= m; i++) {
            D1 = D2;
            D2 = new int[n + 1];
            for (int j = 0; j <= n; j++) {
                if (j == 0) D2[j] = i;
                else {
                    int cost = (S1.charAt(i - 1) != S2.charAt(j - 1)) ? 1 : 0;
                    if (D2[j - 1] < D1[j] && D2[j - 1] < D1[j - 1] + cost)
                        D2[j] = D2[j - 1] + 1;
                    else if (D1[j] < D1[j - 1] + cost)
                        D2[j] = D1[j] + 1;
                    else
                        D2[j] = D1[j - 1] + cost;
                }
            }
        }
        return D2[n];
    }

    public boolean containsIgnoreCase(String curWordInDict, List<String> messageWords) {
        for (String i : messageWords) {
            int levDist = editdist(curWordInDict.toLowerCase(), i.toLowerCase());
            if (levDist == 0) {
                return true;
            } else if (curWordInDict.length() / (levDist) > 3) {
                return true;
            }
        }
        return false;

    }
}
