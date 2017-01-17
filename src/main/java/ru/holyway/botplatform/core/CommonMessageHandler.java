package ru.holyway.botplatform.core;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sergey on 1/17/2017.
 */
public abstract class CommonMessageHandler implements MessageHandler {
    private boolean flag = true;

    private boolean learning = false;

    private Map<String, List<String>> learningDictionary = new HashMap<>();
    private List<String> list2 = new ArrayList<>();
    private Map<String, List<Integer>> sample = new HashMap<>();

    private List<ArrayList<String>> simpleDictionary = new ArrayList<>();
    private List<String> list2Easy = new ArrayList<>();

    private List<String> muteChats = new ArrayList<>();
    private List<String> easyChats = new ArrayList<>();
    private Map<String, List<Integer>> sampleEasy = new HashMap<>();

    private Set<String> learningChats = new HashSet<>();
    private Map<String, List<String>> listLearning = new HashMap<>();

    private int count = 0;
    private int goodCount = 0;
    private long lastStamp = 0;

    private int countNow = 0;

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
            if (!muteChats.contains(chatId)) {
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
                    int currentCount = listLearning.size();
                    if (mes.length() > 14) {
                        int toDelete = Integer.parseInt(mes.substring(14, 15));
                        if (currentCount - countNow < toDelete) {
                            toDelete = currentCount - countNow;
                        }
                        for (int i = listLearning.size() - 1; i > currentCount - toDelete; i--) {
                            listLearning.remove(i);
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
                if (learningChats.contains(chatId) && !StringUtils.containsIgnoreCase(mes, "Пахом,")) {
                    listLearning.add(mes);
                    return;
                }
                if (StringUtils.containsIgnoreCase(mes, "готов") || StringUtils.containsIgnoreCase(mes, "сделал") || StringUtils.containsIgnoreCase(mes, "купил")) {
                    sendMessage(messageEntity, "о, уважаю, братишка!");
                    return;
                }
                if (StringUtils.containsIgnoreCase(mes, "Пахом, изи")) {
                    addToEazy(chatId);
                    sendMessage(messageEntity, "Вот такой вот, хароший я.. да.");
                    return;
                }
                if (StringUtils.containsIgnoreCase(mes, "Пахом, гавно")) {
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
                if (StringUtils.containsIgnoreCase(mes, "?")) {
                    if (StringUtils.containsIgnoreCase(mes, "как ")) {
                        sendMessage(messageEntity, "да как земля");
                        return;
                    }
                }

                if (StringUtils.containsIgnoreCase(mes, "как дела") || StringUtils.containsIgnoreCase(mes, "Как дела") || StringUtils.containsIgnoreCase(mes, "как сам")) {
                    sendMessage(messageEntity, "да как земля");
                    return;
                }

                if (StringUtils.containsIgnoreCase(mes, "Привет") || StringUtils.containsIgnoreCase(mes, "Хай") || StringUtils.containsIgnoreCase(mes, "привет")) {
                    sendMessage(messageEntity, "Здрасти, Дравсвуйте!");
                    return;
                }

                if (StringUtils.containsIgnoreCase(mes, "короч") || StringUtils.containsIgnoreCase(mes, "кароч") || StringUtils.containsIgnoreCase(mes, "вобщем")) {
                    sendMessage(messageEntity, "Ну давай давай, рассказывай давай...");
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
        list.clear();
        list2.clear();
        simpleDictionary.clear();
        list2Easy.clear();
        sample.clear();

        Map<String, List<String>> learnWords = null;

        List<String> simpleWords = null;
        listLearning.clear();
        try {
            GsonBuilder gson = new GsonBuilder();
            Type collectionType = new TypeToken<HashMap<String, List<String>>>() {}.getType();

            learnWords = gson.create().fromJson(Files.newBufferedReader(Paths.get(getClass().getResource("learnDictionary").toURI()), StandardCharsets.UTF_8), collectionType);

            //learnWords = Files.readAllLines(Paths.get(getClass().getResource("copipasta.txt").toURI()), StandardCharsets.UTF_8);
            simpleWords = Files.readAllLines(Paths.get(getClass().getResource("copipasta2.txt").toURI()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        for (String line : learnWords) {
            if (line.length() > 1) {
                list.add(getTokenizedMessage(line));
                list2.add(line);
            }
        }

        //listLearning.addAll(list2);

        countNow = listLearning.size();
        for (int i = 0; i < list.size(); i++) {
            sample.add(i);
        }

        for (String line : simpleWords) {
            if (line.length() > 1) {
                simpleDictionary.add(getTokenizedMessage(line));
                list2Easy.add(line);
            }
        }
        list.addAll(simpleDictionary);
        list2.addAll(list2Easy);
        for (int i = 0; i < list.size(); i++) {
            sample.add(i);
        }
    }

    private void writeNew() {
        try {
            Files.write(Paths.get("D:\\code\\my\\botconstructor\\src\\main\\resources\\copipasta.txt"), listLearning, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAnalize(MessageEntity message) {
        final String text = "Анализ использования:\n Отправлено сообщений: " + count + "\n Удачных шуток: " + goodCount;
        sendMessage(message, text);
    }

    private ArrayList<String> getTokenizedMessage(String message) {

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
        if (new Random().nextInt(100) > 85) {
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
            ArrayList<String> messageWords = getTokenizedMessage(mesage);
            HashMap<Integer, String> answers = new HashMap<>();
            for (int i = 0; i < getQuestionsList(chatID).size(); i++) {
                int n = 0;
                int f = -1;
                ArrayList<String> checkedWords = new ArrayList<>();
                for (int j = 0; j < getQuestionsList(chatID).get(i).size(); j++) {
                    String curWordInDict = getQuestionsList(chatID).get(i).get(j);
                    if (containsIgnoreCase(curWordInDict, messageWords)) {
                        if (getQuestionsList(chatID).get(i).size() == 1 && curWordInDict.length() > 3) {
                            answers.put(curWordInDict.length(), getAnswersList(chatID).get(i + 1));
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
                    if (sum > 4) {
                        answers.put(sum * checkedWords.size(), getAnswersList(chatID).get(i + 1));
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
        if (!muteChats.contains(chatID)) {
            muteChats.add(chatID);
        }

    }

    private void removeFromMute(String chatID) {
        if (muteChats.contains(chatID)) {
            muteChats.remove(chatID);
        }
    }

    private void addToEazy(String chatID) {
        if (!easyChats.contains(chatID)) {
            easyChats.add(chatID);
        }

    }

    private void removeFromEazy(String chatID) {
        if (easyChats.contains(chatID)) {
            easyChats.remove(chatID);
        }
    }

    private List<String> getAnswersList(String chatID) {
        if (easyChats.contains(chatID)) {
            return list2Easy;
        } else {
            return list2;
        }
    }

    private List<ArrayList<String>> getQuestionsList(String chatID) {
        if (easyChats.contains(chatID)) {
            return simpleDictionary;
        } else {
            return list;
        }
    }

    private List<Integer> getNumbersList(String chatID) {
        if (easyChats.contains(chatID)) {
            return sampleEasy;
        } else {
            return sample;
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

    public boolean containsIgnoreCase(String curWordInDict, ArrayList<String> messageWords) {
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

    protected abstract void sendMessageInternal(MessageEntity messageEntity, String text);
}
