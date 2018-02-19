package ru.holyway.botplatform.core.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.AbsSender;
import ru.holyway.botplatform.core.data.telegram.TelegramRepository;
import ru.holyway.botplatform.core.entity.ChatMembers;
import ru.holyway.botplatform.core.entity.Record;

import java.util.ArrayList;
import java.util.List;

public class TelegramDataService extends MemoryDataService {

    private AbsSender absSender;
    private final String chatID = "-1001268340763";

    @Autowired
    private TelegramRepository<ChatMembers, String> chatMembersStringTelegramRepository;


    @Autowired
    private TelegramRepository<Record, String> recordStringTelegramRepository;


    private List<ChatMembers> chatMembersMap;

    public TelegramDataService() {
    }

    @Override
    public List<String> getChatMembers(String chatId) {
        chatMembersMap = (List<ChatMembers>) chatMembersStringTelegramRepository.findAll();
        if (chatMembersMap == null) {
            chatMembersMap = new ArrayList<>();
        }
        for (ChatMembers chatMembers : chatMembersMap) {
            if (chatMembers.id.equals(chatId)) {
                return chatMembers.members;
            }
        }
        return null;
    }

    @Override
    public void updateChatMembers(String chatId, List<String> chatMembers) {
        chatMembersMap.add(new ChatMembers(chatId, chatMembers));
        chatMembersStringTelegramRepository.save(chatMembersMap);
//        try {
//            String text = new ObjectMapper().writerWithType(Map.class).writeValueAsString(chatMembersMap);
//            Message message = absSender.execute(new SendMessage().setChatId(chatId).setText(text));
//            absSender.execute(new SetChatDescription().setChatId(chatId).setDescription(String.valueOf(message.getMessageId())));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void initMap() {
        this.chatMembersMap = new ArrayList<>();
//        try {
//            String description = absSender.execute(new GetChat().setChatId(chatID)).getDescription();
//            Integer messageID = Integer.valueOf(description);
//            Message message = absSender.execute(new ForwardMessage().setChatId(chatID).setFromChatId(chatID).setMessageId(messageID));
//            String text = message.getText();
//            chatMembersMap.putAll(new ObjectMapper().readValue(text, HashMap.class));
//            absSender.execute(new DeleteMessage().setChatId(chatID).setMessageId(messageID));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void setAbsSender(AbsSender absSender) {
        this.absSender = absSender;
    }
}
