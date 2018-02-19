package ru.holyway.botplatform.core.data.telegram;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.ForwardMessage;
import org.telegram.telegrambots.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.api.methods.groupadministration.SetChatDescription;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

@Repository
public class TelegramRepositoryImp<T, ID extends Serializable> implements TelegramRepository<T, ID> {

    private final AbsSender telegtamSender;

    private final String chatID;

    public TelegramRepositoryImp(@Autowired AbsSender telegramSender, @Value("${credential.telegram.data}") final String chatID) {
        this.telegtamSender = telegramSender;
        this.chatID = chatID;
    }

//    public TelegramRepositoryImp(@Value("${credential.telegram.login}") final String botName, @Value("${credential.telegram.token}") final String botToken, @Value("${credential.telegram.chat}") final String chatID) {
//        this.chatID = chatID;
//        if (StringUtils.isNotEmpty(botName) && StringUtils.isNotEmpty(botToken)) {
//            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
//            try {
//                TelegramLongPollingBot telegramSender = new TelegramLongPollingBot() {
//                    @Override
//                    public String getBotToken() {
//                        return botToken;
//                    }
//
//                    @Override
//                    public void onUpdateReceived(Update update) {
//
//                    }
//
//                    @Override
//                    public String getBotUsername() {
//                        return botName;
//                    }
//                };
//                telegramBotsApi.registerBot(telegramSender);
//                this.telegtamSender = telegramSender;
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new RuntimeException(e);
//            }
//        }
//        throw new RuntimeException("Cannot load telegram data");
//    }

    @Override
    public <S extends T> S save(S s) {
        return null;
    }

    @Override
    public <S extends T> List<S> save(Iterable<S> entites) {
        try {
            String text = new ObjectMapper().writerWithType(entites.getClass()).writeValueAsString(entites);
            Message message = telegtamSender.execute(new SendMessage().setChatId(chatID).setText(text));
            telegtamSender.execute(new SetChatDescription().setChatId(chatID).setDescription(String.valueOf(message.getMessageId())));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            String description = telegtamSender.execute(new GetChat().setChatId(chatID)).getDescription();
            Integer messageID = Integer.valueOf(description);
            Message message = telegtamSender.execute(new ForwardMessage().setChatId(chatID).setFromChatId(chatID).setMessageId(messageID));
            ObjectMapper objectMapper = new ObjectMapper();
            String text = message.getText();
            //Class genType = GenericTypeResolver.resolveTypeArgument(getClass(), TelegramRepositoryImp.class);
            ResolvableType revType = ResolvableType.forClass(getClass()).as(TelegramRepositoryImp.class);
            Class genType = revType.getGenerics()[0].resolve();
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, genType);
            return (List<T>) new ObjectMapper().readValue(text, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Class<?> getParameterizedClass() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class<?>) pt.getActualTypeArguments()[0];
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
