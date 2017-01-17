//package ru.holyway;
//
//import com.samczsun.skype4j.Skype;
//import com.samczsun.skype4j.SkypeBuilder;
//import com.samczsun.skype4j.events.EventHandler;
//import com.samczsun.skype4j.events.Listener;
//import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
//import com.samczsun.skype4j.exceptions.ConnectionException;
//import com.samczsun.skype4j.exceptions.InvalidCredentialsException;
//import com.samczsun.skype4j.exceptions.NotParticipatingException;
//import org.apache.commons.lang.StringUtils;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.security.SecureRandom;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//import java.util.regex.Pattern;
//
///**
// * Created by Sergey on 1/16/2017.
// */
//public class MainSkype {
//    private static boolean flag = true;
//
//    private static boolean learning = false;
//
//    private static ArrayList<ArrayList<String>> list = new ArrayList<>();
//    private static ArrayList<String> list2 = new ArrayList<>();
//
//    private static ArrayList<ArrayList<String>> listEasy = new ArrayList<>();
//    private static ArrayList<String> list2Easy = new ArrayList<>();
//
//    private static ArrayList<Long> muteChats = new ArrayList<>();
//    private static ArrayList<Long> easyChats = new ArrayList<>();
//    private static ArrayList<Integer> sample = new ArrayList<>();
//    private static ArrayList<Integer> sampleEasy = new ArrayList<>();
//    private static Set<Long> xuChats = new HashSet<>();
//
//    private static ArrayList<String> listLearning = new ArrayList<>();
//
//    private static int count = 0;
//    private static int goodCount = 0;
//    private static long lastStamp = 0;
//
//    private static int countNow = 0;
//
//    public static void main(String[] args) throws ConnectionException, NotParticipatingException, InvalidCredentialsException {
//        init();
//        Skype skype = new SkypeBuilder("sergeyivanov0393", "corall93").withAllResources().withExceptionHandler((errorSource, throwable, willShutdown) -> {
//            System.out.println("Error: " + errorSource + " " + throwable + " " + willShutdown);
//        }).build();
//        skype.login();
//        skype.subscribe();
//        skype.getEventDispatcher().registerListener(new Listener() {
//            @EventHandler
//            public void onMessage(MessageReceivedEvent e) throws ConnectionException {
////                System.out.println("Got message: " + e.getMessage().getContent());
////                e.getChat().reply("Скучали по мне?");
//                onUpdateReceived(e);
//            }
//        });
//    }
//
//    private static void init() {
//        list.clear();
//        list2.clear();
//        listEasy.clear();
//        list2Easy.clear();
//        sample.clear();
//        List<String> lines = null;
//        List<String> lines2 = null;
//        listLearning.clear();
//        try {
//            //URL uri = this.getClass().getResource("/copipasta.txt");
//            //RL uri2 = this.getClass().getResource("/copipastaaaa2.txt");
//            lines = Files.readAllLines(Paths.get("D:\\code\\my\\botconstructor\\src\\main\\resources\\copipasta.txt"), StandardCharsets.UTF_8);
//            lines2 = Files.readAllLines(Paths.get("D:\\code\\my\\botconstructor\\src\\main\\resources\\copipasta2.txt"), StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        for (String line : lines) {
//            if (line.length() > 1) {
//                list.add(getTokenizedMessage(line));
//                list2.add(line);
//            }
//        }
//        listLearning.addAll(list2);
//        countNow = listLearning.size();
//        for (int i = 0; i < list.size(); i++) {
//            sample.add(i);
//        }
//
//        for (String line : lines2) {
//            if (line.length() > 1) {
//                listEasy.add(getTokenizedMessage(line));
//                list2Easy.add(line);
//            }
//        }
//        list.addAll(listEasy);
//        list2.addAll(list2Easy);
//        for (int i = 0; i < list.size(); i++) {
//            sample.add(i);
//        }
//    }
//
//    public static void onUpdateReceived(MessageReceivedEvent message) {
//        if (message != null) {
//            String mes = getMessage(message);
//            long chatId = (long) message.getChat().getIdentity().hashCode();
//            try {
//                System.out.println("Message: " + mes + ", from " + message.getMessage().getSender().getDisplayName());
//            } catch (ConnectionException e) {
//                e.printStackTrace();
//            }
//            if (StringUtils.containsIgnoreCase(mes, "Пахом, -")) {
//                addToMute(chatId);
//                sendMsg(message, "Ну.. если хочешь, могу полочать!");
//                return;
//            }
//            if (StringUtils.containsIgnoreCase(mes, "Пахом, +")) {
//                removeFromMute(chatId);
//                sendMsg(message, "О, братишка, я вернулся!");
//                return;
//            }
//            if (!muteChats.contains(chatId)) {
//                if (StringUtils.containsIgnoreCase(mes, "Пахом, учись")) {
//                    sendMsg(message, "Приступил к обучению, пожалуйста, аккуратнее с выражениями.");
//                    xuChats.add(chatId);
//                    return;
//                }
//                if (StringUtils.containsIgnoreCase(mes, "Пахом, отдыхай")) {
//                    xuChats.remove(chatId);
//                    sendMsg(message, "Уф! Пошёл переваривать информацию.");
//                    writeNew();
//                    init();
//                    return;
//                }
//                if (StringUtils.containsIgnoreCase(mes, "Пахом, забудь")) {
//                    int currentCount = listLearning.size();
//                    if (mes.length() > 14) {
//                        int toDelete = Integer.parseInt(mes.substring(14, 15));
//                        if (currentCount - countNow < toDelete) {
//                            toDelete = currentCount - countNow;
//                        }
//                        for (int i = listLearning.size() - 1; i > currentCount - toDelete; i--) {
//                            listLearning.remove(i);
//                        }
//                        sendMsg(message, "Я ничего не видел, нет... нет.");
//                    }
//                    else {
//                        sendMsg(message, "Не понимаю...");
//                    }
//                    return;
//                }
//                if (StringUtils.containsIgnoreCase(mes, "Пахом, анализ")) {
//                    sendAnalize(message);
//                    return;
//                }
//                if (xuChats.contains(chatId) && !StringUtils.containsIgnoreCase(mes, "Пахом,")) {
//                    listLearning.add(mes);
//                    return;
//                }
//                if (StringUtils.containsIgnoreCase(mes, "готов") || StringUtils.containsIgnoreCase(mes, "сделал") || StringUtils.containsIgnoreCase(mes, "купил")) {
//                    sendMsg(message, "о, уважаю, братишка!");
//                    return;
//                }
//                if (StringUtils.containsIgnoreCase(mes, "Пахом, изи")) {
//                    addToEazy(chatId);
//                    sendMsg(message, "Вот такой вот, хароший я.. да.");
//                    return;
//                }
//                if (StringUtils.containsIgnoreCase(mes, "Пахом, гавно")) {
//                    removeFromEazy(chatId);
//                    sendMsg(message, "Ты блядь, уже не понимаешь, что ты поехавший?!");
//                    return;
//                }
//                if (mes.equalsIgnoreCase("пахом") || StringUtils.containsIgnoreCase(mes, "Пахом")) {
//                    if (isDirectQuestions(mes)) {
//                        if (StringUtils.containsIgnoreCase(mes, "сгенерируй")) {
//                            sendMsg(message, getRandomNum(mes));// +URLEncoder.encode( mes.substring(7)));
//                            return;
//                        }
//                    }
//                    if (mes.equalsIgnoreCase("пахом")) {
//                        sendMsg(message, "Что.. что, что случилося то?");
//                        return;
//                    }
//                }
//                if (StringUtils.containsIgnoreCase(mes, "?")) {
//                    if (StringUtils.containsIgnoreCase(mes, "как ")) {
//                        sendMsg(message, "да как земля");
//                        return;
//                    }
//                }
//
//                if (StringUtils.containsIgnoreCase(mes, "как дела") || StringUtils.containsIgnoreCase(mes, "Как дела") || StringUtils.containsIgnoreCase(mes, "как сам")) {
//                    sendMsg(message, "да как земля");
//                    return;
//                }
//
//                if (StringUtils.containsIgnoreCase(mes, "Привет") || StringUtils.containsIgnoreCase(mes, "Хай") || StringUtils.containsIgnoreCase(mes, "привет")) {
//                    sendMsg(message, "Здрасти, Дравсвуйте!");
//                    return;
//                }
//
//                if (StringUtils.containsIgnoreCase(mes, "короч") || StringUtils.containsIgnoreCase(mes, "кароч") || StringUtils.containsIgnoreCase(mes, "вобщем")) {
//                    sendMsg(message, "Ну давай давай, рассказывай давай...");
//                    return;
//                }
//                if (StringUtils.containsIgnoreCase(mes, "скучн") || StringUtils.containsIgnoreCase(mes, "он умеет")) {
//                    sendMsg(message, "Хочешь я на одной ноге постою, Как цапля, хочешь?");
//                    return;
//                }
//                if (StringUtils.containsIgnoreCase(mes, "цапл") || StringUtils.containsIgnoreCase(mes, "чайк") || StringUtils.containsIgnoreCase(mes, "голуб")) {
//                    sendMsg(message, "курлык-курлык!");
//                    return;
//                }
//                if (mes.equals("а?") || mes.equals("а")) {
//                    sendMsg(message, "Не-а, блеать!");
//                    return;
//                }
//                if (isJock(message)) {
//                    if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastStamp) < 5) {
//                        goodCount++;
//                        lastStamp = 0;
//                    }
//                    if (new Random().nextInt(100) > 80) {
//                        sendMsg(message, "\uD83D\uDE04");
//                        lastStamp = 0;
//                        return;
//                    }
//                    lastStamp = 0;
//                    return;
//                }
//                String rnd = generateZSAnswer(mes, chatId);
//                if (StringUtils.isNotBlank(rnd)) {
//                    sendMsg(message, rnd);
//                }
//            }
//        }
//    }
//
//    private static void writeNew() {
//        try {
//            Files.write(Paths.get("D:\\code\\my\\botconstructor\\src\\main\\resources\\copipasta.txt"), listLearning, StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void sendAnalize(MessageReceivedEvent message) {
//        final String text = "Анализ использования:\n Отправлено сообщений: " + count + "\n Удачных шуток: " + goodCount;
//        sendMsg(message, text);
//    }
//
//    private static void sendMsg(MessageReceivedEvent message, String text) {
//        if (flag) {
////            SendMessage reply = new SendMessage();
////            reply.enableMarkdown(true);
////            Long chat = message.getChatId();
////            reply.setChatId(message.getChatId().toString());
////            if (muteChats.contains(message.getChatId())) {
////                text = cenz(text);
////            }
////            reply.setText(text);
//            try {
//                message.getChat().sendMessage(text);
//            } catch (ConnectionException e) {
//                e.printStackTrace();
//            }
//            lastStamp = System.currentTimeMillis();
//            count++;
////            try {
//////                reply(reply);
//////                if (false) {
//////                    throw new TelegramApiException("");
//////                }
////            } catch (TelegramApiException e) {
////                e.printStackTrace();
////            }
//        }
//    }
//
//    private static ArrayList<String> getTokenizedMessage(String message) {
//
//        StringTokenizer tok = new StringTokenizer(message, " ,;-!.?()…");
//        ArrayList<String> a = new ArrayList<>();
//        while (tok.hasMoreTokens()) {
//            a.add(tok.nextToken());
//        }
//        return a;
//    }
//
//    private static String getMessage(MessageReceivedEvent message) {
//        return message.getMessage().getContent().asPlaintext() == null ? "" : message.getMessage().getContent().asPlaintext();
//    }
//
//    private static boolean isNeedReply(String message, Long chatID) {
//        if (message.contains("пахом") || message.contains("Пахом")) {
//            return true;
//        }
//        if (new Random().nextInt(100) > 85) {
//            return true;
//        }
//        return false;
//    }
//
//    private static boolean isJock(MessageReceivedEvent message) {
//        if (message.getMessage().getContent() != null && (message.getMessage().getContent().asPlaintext().contains("\uD83D\uDE04") || message.getMessage().getContent().asPlaintext().contains("\uD83D\uDE03")
//                || message.getMessage().getContent().asPlaintext().contains("xD") || message.getMessage().getContent().asPlaintext().contains(":D") || message.getMessage().getContent().asPlaintext().contains("хах"))) {
//            return true;
//        }
//        return false;
//    }
//
//    private static boolean isDirectQuestions(String message) {
//        if (message.contains("?")) {
//            if (message.startsWith("Пахом,") || message.startsWith("пахом,")) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private static String generateZSAnswer(String mesage, Long chatID) {
//        if (isNeedReply(mesage, chatID)) {
//            ArrayList<String> messageWords = getTokenizedMessage(mesage);
//            HashMap<Integer, String> answers = new HashMap<>();
//            for (int i = 0; i < getQuestionsList(chatID).size(); i++) {
//                int n = 0;
//                int f = -1;
//                ArrayList<String> checkedWords = new ArrayList<>();
//                for (int j = 0; j < getQuestionsList(chatID).get(i).size(); j++) {
//                    String curWordInDict = getQuestionsList(chatID).get(i).get(j);
//                    if (containsIgnoreCase(curWordInDict, messageWords)) {
//                        if (getQuestionsList(chatID).get(i).size() == 1 && curWordInDict.length() > 3) {
//                            answers.put(curWordInDict.length(), getAnswersList(chatID).get(i + 1));
//                            break;
//                        }
//                        if (!containsIgnoreCase(curWordInDict, checkedWords)) {
//                            if (f != -1 && j - f > 3) {
//                                f = j;
//                                n = 1;
//                            } else {
//                                n++;
//                                f = j;
//                            }
//                            checkedWords.add(curWordInDict);
//                        }
//                    }
//                }
//                if (n > 1) {
//                    int sum = 0;
//                    for (String str : checkedWords) {
//                        sum += str.length();
//                    }
//                    if (sum > 4) {
//                        answers.put(sum * checkedWords.size(), getAnswersList(chatID).get(i + 1));
//                    }
//                }
//            }
//            if (!answers.isEmpty()) {
////                int max = 0;
////                int min = 0;
//                int sum = 0;
//                for (Integer i : answers.keySet()) {
////                    max = Math.max(i, max);
////                    min = Math.min(i, min);
//                    sum += i;
//                }
//                ArrayList<String> answersT = new ArrayList<>();
//                for (Integer i : answers.keySet()) {
//                    if (i >= sum / answers.keySet().size()) {
//                        answersT.add(answers.get(i));
//                    }
//                }
//                int i = new Random().nextInt(answersT.size());
//                return answersT.get(i);
//            }
//        }
//        return null;
//    }
//
//    private static void addToMute(Long chatID) {
//        if (!muteChats.contains(chatID)) {
//            muteChats.add(chatID);
//        }
//
//    }
//
//    private static void removeFromMute(Long chatID) {
//        if (muteChats.contains(chatID)) {
//            muteChats.remove(chatID);
//        }
//    }
//
//    private static void addToEazy(Long chatID) {
//        if (!easyChats.contains(chatID)) {
//            easyChats.add(chatID);
//        }
//
//    }
//
//    private static void removeFromEazy(Long chatID) {
//        if (easyChats.contains(chatID)) {
//            easyChats.remove(chatID);
//        }
//    }
//
//    private static ArrayList<String> getAnswersList(Long chatID) {
//        if (easyChats.contains(chatID)) {
//            return list2Easy;
//        } else {
//            return list2;
//        }
//    }
//
//    private static ArrayList<ArrayList<String>> getQuestionsList(Long chatID) {
//        if (easyChats.contains(chatID)) {
//            return listEasy;
//        } else {
//            return list;
//        }
//    }
//
//    private static ArrayList<Integer> getNumbersList(Long chatID) {
//        if (easyChats.contains(chatID)) {
//            return sampleEasy;
//        } else {
//            return sample;
//        }
//    }
//
//    private static String getRandomNum(String message) {
//        try {
//            Integer n = Integer.parseInt(message.substring(message.indexOf("й") + 1, message.indexOf("?")).trim());
//            return String.valueOf(new SecureRandom().nextInt(n) + 1);
//        } catch (Exception e) {
//            return "Ой, чёт ты шибко больно придумал";
//        }
//    }
//
//    private static String cenz(String message) {
//        message = message.replaceAll("блядь", "блин");
//        message = message.replaceAll("сука", "капибара");
//        message = message.replaceAll("Сука", "Капибара");
//        message = message.replaceAll("пиздец", "писец");
//        message = message.replaceAll(Pattern.quote("Охуел"), "Офигел");
//        message = message.replaceAll("заебал", "задолбал");
//        message = message.replaceAll("бля", "блин");
//        message = message.replaceAll("Бля", "Лол");
//        message = message.replaceAll("съебаться", "свалить");
//        message = message.replaceAll("Нафиг", "нафиг");
//        message = message.replaceAll("хуйню", "фигню");
//        message = message.replaceAll("нахуй", "нафиг");
//        message = message.replaceAll("ебаный", "глупый");
//        message = message.replaceAll("ёбаны", "фиговы");
//
//        message = message.replaceAll("нихуя", "нифига");
//        message = message.replaceAll("еба", "оой,");
//        return message;
//    }
//
//    private static int editdist(String S1, String S2) {
//        int m = S1.length(), n = S2.length();
//        int[] D1;
//        int[] D2 = new int[n + 1];
//
//        for (int i = 0; i <= n; i++)
//            D2[i] = i;
//
//        for (int i = 1; i <= m; i++) {
//            D1 = D2;
//            D2 = new int[n + 1];
//            for (int j = 0; j <= n; j++) {
//                if (j == 0) D2[j] = i;
//                else {
//                    int cost = (S1.charAt(i - 1) != S2.charAt(j - 1)) ? 1 : 0;
//                    if (D2[j - 1] < D1[j] && D2[j - 1] < D1[j - 1] + cost)
//                        D2[j] = D2[j - 1] + 1;
//                    else if (D1[j] < D1[j - 1] + cost)
//                        D2[j] = D1[j] + 1;
//                    else
//                        D2[j] = D1[j - 1] + cost;
//                }
//            }
//        }
//        return D2[n];
//    }
//
//    public static boolean containsIgnoreCase(String curWordInDict, ArrayList<String> messageWords) {
//        for (String i : messageWords) {
//            int levDist = editdist(curWordInDict.toLowerCase(), i.toLowerCase());
//            if (levDist == 0) {
//                return true;
//            } else if (curWordInDict.length() / (levDist) > 3) {
//                return true;
//            }
//        }
//        return false;
//
//    }
//}
