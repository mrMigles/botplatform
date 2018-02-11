package ru.holyway.botplatform.core.data;

import org.codehaus.jackson.map.ObjectMapper;
import org.telegram.telegrambots.api.methods.ForwardMessage;
import org.telegram.telegrambots.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.api.methods.groupadministration.SetChatDescription;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TelegramDataService extends MemoryDataService {

    private AbsSender absSender;
    private final String chatID = "-1001268340763";

    private Map<String, List<String>> chatMembersMap;

    public TelegramDataService() {
    }

    @Override
    public List<String> getChatMembers(String chatId) {
        if (chatMembersMap == null) {
            initMap();
        }
        return chatMembersMap.get(chatId);
    }

    @Override
    public void updateChatMembers(String chatId, List<String> chatMembers) {
        chatMembersMap.put(chatId, chatMembers);
        try {
            String text = new ObjectMapper().writerWithType(Map.class).writeValueAsString(chatMembersMap);
            Message message = absSender.execute(new SendMessage().setChatId(chatId).setText(text));
            absSender.execute(new SetChatDescription().setChatId(chatId).setDescription(String.valueOf(message.getMessageId())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMap() {
        this.chatMembersMap = new ConcurrentHashMap<>();
        try {
            String description = absSender.execute(new GetChat().setChatId(chatID)).getDescription();
            Integer messageID = Integer.valueOf(description);
            Message message = absSender.execute(new ForwardMessage().setChatId(chatID).setFromChatId(chatID).setMessageId(messageID));
            String text = message.getText();
            chatMembersMap.putAll(new ObjectMapper().readValue(text, HashMap.class));
            absSender.execute(new DeleteMessage().setChatId(chatID).setMessageId(messageID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAbsSender(AbsSender absSender) {
        this.absSender = absSender;
    }
}
