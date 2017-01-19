package ru.holyway.botplatform.core;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sergey on 1/17/2017.
 */
public class CommonMessageHandler implements MessageHandler {
    private boolean flag = true;

    private ConcurrentMap<String, List<List<String>>> learningTokenizedDictionary = new ConcurrentHashMap<>();

    private ConcurrentMap<String, List<String>> learningDictionary = new ConcurrentHashMap<>();

    private List<List<String>> simpleDictionary = new ArrayList<>();
    private List<String> list2Easy = new ArrayList<>();

    private Set<String> learningChats = new HashSet<>();
    private ConcurrentMap<String, List<String>> listCurrentLearning = new ConcurrentHashMap<>();

    private int count = 0;
    private int goodCount = 0;
    private long lastStamp = 0;

    private Map<String, Integer> dictionarySize = new HashMap<>();

    @Autowired
    private Settings settings;

    public CommonMessageHandler() {
        init();
    }

    @Override
    public void handleMessage(MessageEntity messageEntity) {
        if (messageEntity != null) {
            String mes = messageEntity.getText();
            String chatId = messageEntity.getChatId();
            System.out.println("Message: " + mes + ", from " + messageEntity.getSender());
            if (StringUtils.containsIgnoreCase(mes, "Пахом, -")) {
                addToMute(chatId);
                sendMessage(messageEntity, "Ну.. если хочешь, могу полочать!");
                return;
            }
            if (StringUtils.containsIgnoreCase(mes, "Пахом, +")) {
                removeFromMute(chatId);
                sendMessage(messageEntity, "О, братишка, я вернулся!");
                return;
            }
            if (!settings.getMuteChats().contains(chatId)) {
                if (StringUtils.containsIgnoreCase(mes, "Пахом, учись")) {
                    sendMessage(messageEntity, "Приступил к обучению, пожалуйста, аккуратнее с выражениями.");
                    learningChats.add(chatId);
                    return;
                }
                if (StringUtils.containsIgnoreCase(mes, "Пахом, отдыхай")) {
                    learningChats.remove(chatId);
                    sendMessage(messageEntity, "Уф! Пошёл переваривать информацию.");
                    writeNew();
                    init();
                    return;
                }
                if (StringUtils.containsIgnoreCase(mes, "Пахом, забудь")) {
                    if (listCurrentLearning.get(chatId) == null) {
                        sendMessage(messageEntity, "То, что сказано не было... быть забытым не может!.");
                        return;
                    }
                    int currentCount = listCurrentLearning.get(chatId).size();
                    if (mes.length() > 14) {
                        int toDelete = Integer.parseInt(mes.substring(14, 15));
                        if (currentCount - learningDictionary.get(chatId).size() < toDelete) {
                            toDelete = currentCount - dictionarySize.get(chatId);
                        }
                        for (int i = listCurrentLearning.size() - 1; i > currentCount - toDelete; i--) {
                            listCurrentLearning.remove(i);
                        }
                        sendMessage(messageEntity, "Я ничего не видел, нет... нет.");
                    } else {
                        sendMessage(messageEntity, "Не понимаю...");
                    }
                    return;
                }
                if (StringUtils.containsIgnoreCase(mes, "Пахом, анализ")) {
                    sendAnalize(messageEntity);
                    return;
                }
                if (StringUtils.containsIgnoreCase(mes, "/help")) {
                    sendMessage(messageEntity, "Ооой, я много что умею, ну хочешь я спою?\n" +
                            "Пахом, [любая фраза] - и я попробую ответить\n" +
                            "Пахом, учись - включить режим обучения\n" +
                            "Пахом, отдыхай - выключить режим обучения и запомнить фразы\n" +
                            "Пахом, забудь [n] - исключить последние n-фраз во время обучения\n" +
                            "Пахом, анализ - бесполезная херота\n" +
                            "Пахом, - - выключить для данного чата\n" +
                            "Пахом, + - включить для данного чата\n" +
                            "Пахом, умный - использовать обученные фразы\n" +
                            "Пахом, глупый - использовать только заскриптованные фразы\n" +
                            "Пахом, [0-100]% - установить вероятность случайного ответа в значение []\n" +
                            "Пахом, процент - показать процент ответа для данного чата\n" +
                            "Пахом, ид - показать ID данного чата\n" +
                            "Пахом, миграция ID-чата - миграция обучения из определенного чата\n");
                    return;
                }
                if (learningChats.contains(chatId) && !StringUtils.containsIgnoreCase(mes, "Пахом,")) {
                    List<String> current = listCurrentLearning.get(chatId);
                    if (current == null) {
                        current = new ArrayList<>();
                    }
                    current.add(mes);
                    listCurrentLearning.put(chatId, current);
                    return;
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
                                sendMessage(messageEntity, "ок");
                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sendMessage(messageEntity, "Тебя разве не учили в 6м училище, что такое проценты?");

                }
                if (StringUtils.containsIgnoreCase(mes, "готов") || StringUtils.containsIgnoreCase(mes, "сделал") || StringUtils.containsIgnoreCase(mes, "купил")) {
                    sendMessage(messageEntity, "о, уважаю, братишка!");
                    return;
                }
                if (StringUtils.containsIgnoreCase(mes, "Пахом, процент")) {
                    int percent = settings.getAnswerProximity().get(chatId) == null ? 15 : settings.getAnswerProximity().get(chatId);
                    sendMessage(messageEntity, percent + "%");
                    return;
                }
                if (StringUtils.containsIgnoreCase(mes, "Пахом, миграция ")) {
                    if (mes.length() > 17) {
                        final String migChatId = mes.substring(17);
                        List<String> dictionary = learningDictionary.get(migChatId);
                        if (dictionary != null) {
                            List<String> curDictionary = listCurrentLearning.get(chatId);
                            if (curDictionary == null) {
                                curDictionary = new ArrayList<>();
                            }
                            curDictionary.addAll(dictionary);
                            listCurrentLearning.put(chatId, curDictionary);
                            writeNew();
                            init();
                            sendMessage(messageEntity, "Готово, братишка");
                            return;
                        }
                    }
                    sendMessage(messageEntity, "Чет не понял");
                    return;
                }
                if (StringUtils.containsIgnoreCase(mes, "Пахом, ид") || StringUtils.containsIgnoreCase(mes, "Пахом, что это за чат?")) {
                    sendMessage(messageEntity, chatId);
                    return;
                }
                if (StringUtils.containsIgnoreCase(mes, "Пахом, умный")) {
                    addToEazy(chatId);
                    sendMessage(messageEntity, "Вот такой вот, хароший я.. да.");
                    return;
                }
                if (StringUtils.containsIgnoreCase(mes, "Пахом, глупый")) {
                    removeFromEazy(chatId);
                    sendMessage(messageEntity, "Ты блядь, уже не понимаешь, что ты поехавший?!");
                    return;
                }
                if (mes.equalsIgnoreCase("пахом") || StringUtils.containsIgnoreCase(mes, "Пахом")) {
                    if (isDirectQuestions(mes)) {
                        if (StringUtils.containsIgnoreCase(mes, "сгенерируй")) {
                            sendMessage(messageEntity, getRandomNum(mes));// +URLEncoder.encode( mes.substring(7)));
                            return;
                        }
                    }
                    if (mes.equalsIgnoreCase("пахом")) {
                        sendMessage(messageEntity, "Что.. что, что случилося то?");
                        return;
                    }
                }

                if (StringUtils.containsIgnoreCase(mes, "Пахом, как дела") || StringUtils.containsIgnoreCase(mes, "Пахом, Как дела") || StringUtils.containsIgnoreCase(mes, "Пахом, как сам")) {
                    sendMessage(messageEntity, "да как земля");
                    return;
                }

                if (StringUtils.containsIgnoreCase(mes, "Привет") || StringUtils.containsIgnoreCase(mes, "Хай") || StringUtils.containsIgnoreCase(mes, "привет")) {
                    sendMessage(messageEntity, "Здрасти, Дравсвуйте!");
                    return;
                }
                if (StringUtils.containsIgnoreCase(mes, "скучн") || StringUtils.containsIgnoreCase(mes, "он умеет")) {
                    sendMessage(messageEntity, "Хочешь я на одной ноге постою, Как цапля, хочешь?");
                    return;
                }
                if (StringUtils.containsIgnoreCase(mes, "цапл") || StringUtils.containsIgnoreCase(mes, "чайк") || StringUtils.containsIgnoreCase(mes, "голуб")) {
                    sendMessage(messageEntity, "курлык-курлык!");
                    return;
                }
                if (mes.equals("а?") || mes.equals("а")) {
                    sendMessage(messageEntity, "Не-а, блеать!");
                    return;
                }
                if (isJock(messageEntity)) {
                    if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastStamp) < 5) {
                        goodCount++;
                        lastStamp = 0;
                    }
                    if (new Random().nextInt(100) > 80) {
                        sendMessage(messageEntity, "\uD83D\uDE04");
                        lastStamp = 0;
                        return;
                    }
                    lastStamp = 0;
                    return;
                }
                String rnd = generateZSAnswer(mes, chatId);
                if (StringUtils.isNotBlank(rnd)) {
                    sendMessage(messageEntity, rnd);
                }
            }
        }
    }

    private void init() {
        learningTokenizedDictionary.clear();
        learningDictionary.clear();
        simpleDictionary.clear();
        list2Easy.clear();
        dictionarySize.clear();

        Map<String, List<String>> learnWords = null;

        List<String> simpleWords = null;
        try {
            GsonBuilder gson = new GsonBuilder();
            Type collectionType = new TypeToken<HashMap<String, List<String>>>() {
            }.getType();
            learnWords = gson.create().fromJson(Files.newBufferedReader(Paths.get("C:\\storage\\learnDictionary"), StandardCharsets.UTF_8), collectionType);

            //learnWords = Files.readAllLines(Paths.get(getClass().getResource("copipasta.txt").toURI()), StandardCharsets.UTF_8);
            simpleWords = Files.readAllLines(Paths.get("C:\\storage\\simpleDictionary"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, List<String>> line : learnWords.entrySet()) {
            final List<List<String>> tokenizedAnswers = new ArrayList<>();
            final List<String> notEmptyStrings = new ArrayList<>();
            for (String lineStr : line.getValue()) {
                if (lineStr.length() > 1) {
                    tokenizedAnswers.add(getTokenizedMessage(lineStr));
                    notEmptyStrings.add(lineStr);
                }
            }
            learningDictionary.put(line.getKey(), notEmptyStrings);
            learningTokenizedDictionary.put(line.getKey(), tokenizedAnswers);
            dictionarySize.put(line.getKey(), notEmptyStrings.size());
        }
        if (learningDictionary.size() == 0) {
            listCurrentLearning.putAll(learningDictionary);
        }

        for (String line : simpleWords) {
            if (line.length() > 1) {
                simpleDictionary.add(getTokenizedMessage(line));
                list2Easy.add(line);
            }
        }
//        list.addAll(simpleDictionary);
//        learningDictionary.addAll(list2Easy);
//        for (int i = 0; i < list.size(); i++) {
//            sample.add(i);
//        }
    }

    private void writeNew() {
        try {
            GsonBuilder gson = new GsonBuilder();
            Files.write(Paths.get(("C:\\storage\\learnDictionary")), gson.create().toJson(listCurrentLearning).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAnalize(MessageEntity message) {
        final String text = "Анализ использования:\n Отправлено сообщений: " + count + "\n Удачных шуток: " + goodCount;
        sendMessage(message, text);
    }

    private List<String> getTokenizedMessage(String message) {

        StringTokenizer tok = new StringTokenizer(message, " ,;-!.?()…");
        ArrayList<String> a = new ArrayList<>();
        while (tok.hasMoreTokens()) {
            a.add(tok.nextToken());
        }
        return a;
    }

    private boolean isNeedReply(String message, String chatID) {
        if (message.contains("пахом") || message.contains("Пахом")) {
            return true;
        }
        int ansPerc = settings.getAnswerProximity().get(chatID) == null ? 15 : settings.getAnswerProximity().get(chatID);
        if (new Random().nextInt(100) > 100 - ansPerc) {
            return true;
        }
        return false;
    }

    private boolean isJock(MessageEntity message) {
        if (message.getText() != null && (message.getText().contains("\uD83D\uDE04") || message.getText().contains("\uD83D\uDE03")
                || message.getText().contains("xD") || message.getText().contains(":D") || message.getText().contains("хах"))) {
            return true;
        }
        return false;
    }

    private boolean isDirectQuestions(String message) {
        if (message.contains("?")) {
            if (message.startsWith("Пахом,") || message.startsWith("пахом,")) {
                return true;
            }
        }
        return false;
    }

    private String generateZSAnswer(String mesage, String chatID) {
        if (isNeedReply(mesage, chatID)) {
            mesage = mesage.replaceAll("Пахом,", "").replaceAll("пахом,", "");
            List<String> messageWords = getTokenizedMessage(mesage);
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
//                int max = 0;
//                int min = 0;
                int sum = 0;
                for (Integer i : answers.keySet()) {
//                    max = Math.max(i, max);
//                    min = Math.min(i, min);
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

    private void addToMute(String chatID) {
        if (!settings.getMuteChats().contains(chatID)) {
            settings.addMuteChat(chatID);
        }

    }

    private void removeFromMute(String chatID) {
        if (settings.getMuteChats().contains(chatID)) {
            settings.removeMuteChat(chatID);
        }
    }

    private void addToEazy(String chatID) {
        if (!settings.getEasyChats().contains(chatID)) {
            settings.addEasyChat(chatID);
        }

    }

    private void removeFromEazy(String chatID) {
        if (settings.getEasyChats().contains(chatID)) {
            settings.removeEasyChat(chatID);
        }
    }

    private List<String> getAnswersList(String chatID) {
        if (settings.getEasyChats().contains(chatID)) {
            return list2Easy;
        } else {
            List<String> ansList = new ArrayList<>(list2Easy);
            if (learningDictionary.get(chatID) != null) {
                ansList.addAll(learningDictionary.get(chatID));
            }
            return ansList;
        }
    }

    private List<List<String>> getQuestionsList(String chatID) {
        if (settings.getEasyChats().contains(chatID)) {
            return simpleDictionary;
        } else {
            List<List<String>> qstList = new ArrayList<>(simpleDictionary);
            if (learningTokenizedDictionary.get(chatID) != null) {
                qstList.addAll(learningTokenizedDictionary.get(chatID));
            }
            return qstList;
        }
    }

    private String getRandomNum(String message) {
        try {
            Integer n = Integer.parseInt(message.substring(message.indexOf("й") + 1, message.indexOf("?")).trim());
            return String.valueOf(new SecureRandom().nextInt(n) + 1);
        } catch (Exception e) {
            return "Ой, чёт ты шибко больно придумал";
        }
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

    private void sendMessage(MessageEntity messageEntity, String text) {
        sendMessageInternal(messageEntity, text);
        lastStamp = System.currentTimeMillis();
        count++;
    }

    protected void sendMessageInternal(MessageEntity messageEntity, String text) {
        messageEntity.reply(text);
    }
}
