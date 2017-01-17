//package ru.holyway;
//
///**
// * Created by Sergey on 8/8/2016.
// */
//
//import com.jcabi.http.Request;
//import com.jcabi.http.request.JdkRequest;
//import com.mashape.unirest.http.Unirest;
//import org.apache.commons.lang.StringUtils;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.conn.ssl.SSLContexts;
//import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.telegram.telegrambots.TelegramBotsApi;
//import org.telegram.telegrambots.api.methods.send.SendMessage;
//import org.telegram.telegrambots.api.objects.Message;
//import org.telegram.telegrambots.api.objects.Update;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.exceptions.TelegramApiException;
//
//import javax.annotation.PostConstruct;
//import javax.net.ssl.*;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.security.KeyManagementException;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.security.cert.X509Certificate;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//import java.util.regex.Pattern;
//
///**
// * Created by seiv0814 on 02-08-16.
// */
//public class BotInitializer extends TelegramLongPollingBot {
//
//
//    private static final String botName = "pakhom_bot";
//    private static final String botToken = "261215240:AAEi6m-VCtP_wUxNaoaMw-Ffc4Ls6btB6Nk";
//    private static boolean flag = true;
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
//    private static ArrayList<Long> xuChats = new ArrayList<>();
//
//    private int count = 0;
//    private int goodCount = 0;
//    private long lastStamp = 0;
//    private static int countNow = 0;
//
//    public BotInitializer() {
//        super();
//        List<String> lines = null;
//        List<String> lines2 = null;
//        try {
//            //URL uri = this.getClass().getResource("/copipasta.txt");
//            //RL uri2 = this.getClass().getResource("/copipastaaaa2.txt");
//            lines = Files.readAllLines(Paths.get("D:\\code\\my\\botconstructor\\src\\main\\resources\\copipasta2.txt"), StandardCharsets.UTF_8);
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
//    @Override
//    public void onUpdateReceived(Update update) {
//        Message message = update.getMessage();
//        if (message != null) {
//            String mes = getMessage(message);
//            System.out.println("Message: " + mes + ", from " + message.getContact());
//            if (StringUtils.containsIgnoreCase(mes, "Пахом, -")) {
//                addToMute(message.getChatId());
//                sendMsg(message, "Ну.. если хочешь, могу полочать!");
//                return;
//            }
//            if (StringUtils.containsIgnoreCase(mes, "Пахом, +")) {
//                removeFromMute(message.getChatId());
//                sendMsg(message, "О, братишка, я вернулся!");
//                return;
//            }
//            if (!muteChats.contains(message.getChatId())) {
//                if (StringUtils.containsIgnoreCase(mes, "Пахом, зоя")){
//                    sendMsg(message, "Привет! Мне нравится им Зоя.");
//                    xuChats.add(message.getChatId());
//                    return;
//                }
//                if (StringUtils.containsIgnoreCase(mes, "Пахом, зина")){
//                    xuChats.remove(message.getChatId());
//                    sendMsg(message, "Досвидос! Мне не нравится имя Зина.");
//                    return;
//                }
//                if (StringUtils.containsIgnoreCase(mes, "Пахом, анализ")) {
//                    sendAnalize(message);
//                    return;
//                }
//                if (xuChats.contains(message.getChatId()) && (isDirectQuestions(mes) || (isNeedReply(mes, message.getChatId())))){
//                    final String answer = getXuMessage(mes.replace("Пахом,", ""));
//                    sendMsg(message, answer);
//                    return;
//                }
//                if (StringUtils.containsIgnoreCase(mes, "готов") || StringUtils.containsIgnoreCase(mes, "сделал") || StringUtils.containsIgnoreCase(mes, "купил")) {
//                    sendMsg(message, "о, уважаю, братишка!");
//                    return;
//                }
//                if (StringUtils.containsIgnoreCase(mes, "Пахом, изи")) {
//                    addToEazy(message.getChatId());
//                    sendMsg(message, "Вот такой вот, хароший я.. да.");
//                    return;
//                }
//                if (StringUtils.containsIgnoreCase(mes, "Пахом, гавно")) {
//                    removeFromEazy(message.getChatId());
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
//                String rnd = generateZSAnswer(mes, message.getChatId());
//                if (StringUtils.isNotBlank(rnd)) {
//                    sendMsg(message, rnd);
//                }
//            }
//        }
//    }
//
//    private void sendAnalize(Message message) {
//        final String text = "Анализ использования:\n Отправлено сообщений: " + count + "\n Удачных шуток: " + goodCount;
//        sendMsg(message, text);
//    }
//
//    private void sendMsg(Message message, String text) {
//        if (flag) {
//            SendMessage sendMessage = new SendMessage();
//            sendMessage.enableMarkdown(true);
//            Long chat = message.getChatId();
//            sendMessage.setChatId(message.getChatId().toString());
//            if (muteChats.contains(message.getChatId())) {
//                text = cenz(text);
//            }
//            sendMessage.setText(text);
//            lastStamp = System.currentTimeMillis();
//            count++;
//            try {
//                sendMessage(sendMessage);
//                if (false) {
//                    throw new TelegramApiException("");
//                }
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private ArrayList<String> getTokenizedMessage(String message) {
//
//        StringTokenizer tok = new StringTokenizer(message, " ,;-!.?()…");
//        ArrayList<String> a = new ArrayList<>();
//        while (tok.hasMoreTokens()) {
//            a.add(tok.nextToken());
//        }
//        return a;
//    }
//
//    private String getMessage(Message message) {
//        return message.getText() == null ? "" : message.getText();
//    }
//
//    private boolean isNeedReply(String message, Long chatID) {
//        if (message.contains("пахом") || message.contains("Пахом")) {
//            return true;
//        }
//        if (new Random().nextInt(100) > 85) {
//            return true;
//        }
//        return false;
//    }
//
//    private boolean isJock(Message message) {
//        if ((message.getSticker() != null && (message.getSticker().getEmoji().equals("\uD83D\uDE04") || message.getSticker().getEmoji().equals("\uD83D\uDE03")))) {
//            return true;
//        }
//        if (message.getText() != null && (message.getText().contains("\uD83D\uDE04") || message.getText().contains("\uD83D\uDE03")
//                || message.getText().contains("xD") || message.getText().contains(":D") || message.getText().contains("хах"))) {
//            return true;
//        }
//        return false;
//    }
//
//    private boolean isDirectQuestions(String message) {
//        if (message.contains("?")) {
//            if (message.startsWith("Пахом,") || message.startsWith("пахом,")) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private String generateZSAnswer(String mesage, Long chatID) {
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
//    private void addToMute(Long chatID) {
//        if (!muteChats.contains(chatID)) {
//            muteChats.add(chatID);
//        }
//
//    }
//
//    private void removeFromMute(Long chatID) {
//        if (muteChats.contains(chatID)) {
//            muteChats.remove(chatID);
//        }
//    }
//
//    private void addToEazy(Long chatID) {
//        if (!easyChats.contains(chatID)) {
//            easyChats.add(chatID);
//        }
//
//    }
//
//    private void removeFromEazy(Long chatID) {
//        if (easyChats.contains(chatID)) {
//            easyChats.remove(chatID);
//        }
//    }
//
//    private ArrayList<String> getAnswersList(Long chatID) {
//        if (easyChats.contains(chatID)) {
//            return list2Easy;
//        } else {
//            return list2;
//        }
//    }
//
//    private ArrayList<ArrayList<String>> getQuestionsList(Long chatID) {
//        if (easyChats.contains(chatID)) {
//            return listEasy;
//        } else {
//            return list;
//        }
//    }
//
//    private ArrayList<Integer> getNumbersList(Long chatID) {
//        if (easyChats.contains(chatID)) {
//            return sampleEasy;
//        } else {
//            return sample;
//        }
//    }
//
//    private String getRandomNum(String message) {
//        try {
//            Integer n = Integer.parseInt(message.substring(message.indexOf("й") + 1, message.indexOf("?")).trim());
//            return String.valueOf(new SecureRandom().nextInt(n) + 1);
//        } catch (Exception e) {
//            return "Ой, чёт ты шибко больно придумал";
//        }
//    }
//
//    private String cenz(String message) {
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
//    private int editdist(String S1, String S2) {
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
//    public boolean containsIgnoreCase(String curWordInDict, ArrayList<String> messageWords) {
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
//
//    private String getXuMessage(String text) {
//        try {
//            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//
//                public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                }
//
//                public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                }
//            }
//            };
//
//            // Install the all-trusting trust manager
//            SSLContext sc = SSLContext.getInstance("SSL");
//            sc.init(null, trustAllCerts, new java.security.SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//
//            // Create all-trusting host name verifier
//            HostnameVerifier allHostsValid = new HostnameVerifier() {
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            };
//
//            // Install the all-trusting host verifier
//            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//            SSLContext sslcontext = SSLContexts.custom()
//                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
//                    .build();
//
//            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
//            CloseableHttpClient httpclient = HttpClients.custom()
//                    .setSSLSocketFactory(sslsf)
//                    .build();
//            Unirest.setHttpClient(httpclient);
//
//            String body = new JdkRequest("https://xu.su/api/send")
//                    .method(Request.POST)
//                    .body().formParam("uid", "257f0691-1979-47ea-9698-d01490d14635")
//                    .formParam("text", "\"" + text + "\"")
//                    .formParam("bot", "old").back()
//                    .fetch()
//                    .body();
//            if (body != null && body.length() > 19) {
//                return body.substring(19, body.indexOf("\",", 19));
//            }
//            System.out.println(body);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private static class BotAnswer {
//        private String ok;
//        private String text;
//        private String uid;
//
//        public BotAnswer(String ok, String text, String uid) {
//            this.ok = ok;
//            this.text = text;
//            this.uid = uid;
//        }
//
//        public String getOk() {
//            return ok;
//        }
//
//        public void setOk(String ok) {
//            this.ok = ok;
//        }
//
//        public String getText() {
//            return text;
//        }
//
//        public void setText(String text) {
//            this.text = text;
//        }
//
//        public String getUid() {
//            return uid;
//        }
//
//        public void setUid(String uid) {
//            this.uid = uid;
//        }
//    }
//
//    @Override
//    public String getBotUsername() {
//        return botName;
//    }
//
//    @Override
//    public String getBotToken() {
//        return botToken;
//    }
//
//    @PostConstruct
//    public void run() {
//        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
//        try {
//            telegramBotsApi.registerBot(this);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
//
