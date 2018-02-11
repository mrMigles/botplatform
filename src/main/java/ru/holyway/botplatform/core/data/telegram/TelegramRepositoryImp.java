package ru.holyway.botplatform.core.data.telegram;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.io.Serializable;
import java.util.List;

public class TelegramRepositoryImp<T, ID extends Serializable> implements TelegramRepository<T, ID> {

    private final AbsSender telegtamSender;

    private final String chatID;

    public TelegramRepositoryImp(@Autowired AbsSender telegtamSender, @Value("${credential.telegram.login}") final String chatID) {
        this.telegtamSender = telegtamSender;
        this.chatID = chatID;
    }

    public TelegramRepositoryImp(@Value("${credential.telegram.login}") final String botName, @Value("${credential.telegram.token}") final String botToken, @Value("${credential.telegram.chat}") final String chatID) {
        this.chatID = chatID;
        if (StringUtils.isNotEmpty(botName) && StringUtils.isNotEmpty(botToken)) {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            try {
                TelegramLongPollingBot telegramSender = new TelegramLongPollingBot() {
                    @Override
                    public String getBotToken() {
                        return botToken;
                    }

                    @Override
                    public void onUpdateReceived(Update update) {

                    }

                    @Override
                    public String getBotUsername() {
                        return botName;
                    }
                };
                telegramBotsApi.registerBot(telegramSender);
                this.telegtamSender = telegramSender;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Cannot load telegram data");
    }

    @Override
    public <S extends T> S save(S s) {
        return null;
    }

    @Override
    public <S extends T> List<S> save(Iterable<S> entites) {
        return null;
    }

    @Override
    public T findOne(ID id) {
        return null;
    }

    @Override
    public boolean exists(ID id) {
        return false;
    }

    @Override
    public List<T> findAll() {
        return null;
    }

    @Override
    public Iterable<T> findAll(Iterable<ID> iterable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(ID id) {

    }

    @Override
    public void delete(T t) {

    }

    @Override
    public void delete(Iterable<? extends T> iterable) {

    }

    @Override
    public void deleteAll() {

    }
}
