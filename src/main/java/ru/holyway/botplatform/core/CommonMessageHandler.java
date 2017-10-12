package ru.holyway.botplatform.core;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.holyway.botplatform.config.JobInitializer;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.core.entity.JSettings;
import ru.holyway.botplatform.core.handler.MessageHandler;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sergey on 1/17/2017.
 */
public class CommonMessageHandler implements CommonHandler {

    private int count = 0;
    private int goodCount = 0;
    private long lastStamp = 0;
    private int denisCount = 0;

    private JSettings settings;

    @Autowired
    private DataHelper dataHelper;

    @Autowired
    private List<MessageHandler> messageHandlers;

    @Value("${bot.config.silentPeriod}")
    private String silentPeriodString;

    private long srartTime = 0;
    private long silentPeriod = TimeUnit.SECONDS.toMillis(60);


    public CommonMessageHandler() {

    }

    @PostConstruct
    public void postConstruct() {
        settings = dataHelper.getSettings();
        srartTime = System.currentTimeMillis();
        if (StringUtils.isNotEmpty(silentPeriodString)){
            silentPeriod = TimeUnit.SECONDS.toMillis(Long.parseLong(silentPeriodString));
        }
    }

    @Override
    public String generateAnswer(MessageEntity messageEntity) {
        if (messageEntity != null) {
            String mes = messageEntity.getText();
            String chatId = messageEntity.getChatId();
            System.out.println("Message: " + mes + ", from " + messageEntity.getSender());

            if (!settings.getMuteChats().contains(chatId)) {

                if (StringUtils.containsIgnoreCase(mes, "Пахом, анализ")) {
                    return sendAnalize(messageEntity);
                }

                if (isJock(messageEntity)) {
                    if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastStamp) < 10) {
                        goodCount++;
                        lastStamp = 0;
                    }
                    if (new Random().nextInt(100) > 85) {
                        lastStamp = 0;
                        return "\uD83D\uDE04";
                    }
                    lastStamp = 0;
                }

                for (MessageHandler messageHandler : messageHandlers) {
                    try {
                        String message = messageHandler.provideAnswer(messageEntity);
                        if (message != null) {
                            return message;
                        }
                    } catch (ProcessStopException e) {
                        System.out.println("Stop because: " + e.getMessage());
                        break;
                    }
                }

            }
        }
        return null;
    }

    @Override
    public void handleMessage(MessageEntity messageEntity) {
        final String answer = generateAnswer(messageEntity);
        if (!StringUtils.isEmpty(answer)) {
            sendMessage(messageEntity, answer);
        }
    }

    private String sendAnalize(MessageEntity message) {
        final String text = "Анализ использования:\nОтправлено сообщений: " + count + "\nУдачных шуток: " + goodCount + "\nШуток про Дениcа: " + denisCount + "\nСамый поехавший: я";
        return text;
    }

    private boolean isJock(MessageEntity message) {
        if (message.getText() != null && (message.getText().contains("\uD83D\uDE04") || message.getText().contains("\uD83D\uDE03")
                || message.getText().contains("xD") || message.getText().contains(":D") || message.getText().contains("хах")
                || message.getText().contains("лол") || message.getText().contains("lol") || message.getText().contains("Лол"))) {
            return true;
        }
        return false;
    }


    private void sendMessage(MessageEntity messageEntity, String text) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.srartTime > silentPeriod) {
            if (StringUtils.containsIgnoreCase(text, "Денис") || StringUtils.containsIgnoreCase(messageEntity.getText(), "Денис")) {
                denisCount++;
            }
            sendMessageInternal(messageEntity, text);
            JobInitializer.ITER = 0;
            lastStamp = System.currentTimeMillis();
            count++;
        }
    }

    protected void sendMessageInternal(MessageEntity messageEntity, String text) {
        messageEntity.reply(text);
    }
}
