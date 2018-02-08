package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatMemberCount;
import org.telegram.telegrambots.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.api.methods.pinnedmessages.UnpinChatMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Order(1)
public class VoteKickMessageProcessor implements MessageProcessor {

    private Map<String, BanInfo> banList = new HashMap<>();
    private Integer voteSize = 3;

    private final ScheduledThreadPoolExecutor ex = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(5);

    private final TaskScheduler scheduler = new ConcurrentTaskScheduler(ex);

    private static final long DELAY_TO_UPDATE = TimeUnit.MINUTES.toMillis(5);

    @Override
    public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
        final String mes = messageEntity.getText();
        if (StringUtils.isNotEmpty(mes) && (StringUtils.containsIgnoreCase(mes, "кик") || StringUtils.containsIgnoreCase(mes, "бан"))) {
            if (messageEntity.getMessage().getReplyToMessage() != null) {
                return true;
            }
        }
        if (StringUtils.containsIgnoreCase(mes, "/votestop")) {
            return true;
        }
        return false;
    }

    @Override
    public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
        if (banList.get(messageEntity.getChatId()) != null) {
            if (StringUtils.containsIgnoreCase(messageEntity.getText(), "/votestop")) {
                if (messageEntity.getMessage().getFrom().getId().equals(banList.get(messageEntity.getChatId()).user.getId())) {
                    messageEntity.getSender().execute(new SendMessage().setChatId(messageEntity.getChatId())
                            .setText("Ппц ты дерзкий"));
                } else {
                    messageEntity.getSender().execute(new SendMessage().setChatId(messageEntity.getChatId())
                            .setText("Ок, забыли"));
                    endVote(messageEntity.getChatId(), banList.get(messageEntity.getChatId()).getMessageID(), messageEntity.getSender());
                }
                return;
            }
            messageEntity.getSender().execute(new SendMessage().setChatId(messageEntity.getChatId())
                    .setText("Мы уже голосум против " + banList.get(messageEntity.getChatId())
                            .getUser().getFirstName()));
            return;
        }
        final Message replyMessage = messageEntity.getMessage().getReplyToMessage();
        final Integer userID = messageEntity.getMessage().getFrom().getId();
        final User banUser = replyMessage.getFrom();

        if (userID.equals(banUser.getId())) {
            messageEntity.getSender().execute(new DeleteMessage().setChatId(messageEntity.getChatId()).setMessageId(messageEntity.getMessage().getMessageId()));
            messageEntity.getSender().execute(new SendMessage().setChatId(messageEntity.getChatId()).setText(messageEntity.getMessage().getFrom().getFirstName() + ", я притворюсь, что ничего не видел"));
        }

        messageEntity.getSender().execute(new DeleteMessage().setChatId(messageEntity.getChatId()).setMessageId(messageEntity.getMessage().getMessageId()));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(messageEntity.getChatId());
        sendMessage.setText("Есть два стула, на одном написано \"В чате оставить\" на другом \"Забанить\".\n На какой " + banUser.getFirstName() + " " + banUser.getLastName()
                + " посадишь? \n/votestop для остановки голосования.\n" + banUser.getFirstName() + ", на твоём месте я бы пока написал мне в личку.");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Оставить в покое");
        inlineKeyboardButton.setCallbackData("kick:false");
        inlineKeyboardButtons.add(inlineKeyboardButton);

        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Забанить");
        inlineKeyboardButton.setCallbackData("kick:true");
        inlineKeyboardButtons.add(inlineKeyboardButton);


        keyboard.add(inlineKeyboardButtons);
        keyboardMarkup.setKeyboard(keyboard);

        sendMessage.setReplyMarkup(keyboardMarkup);

        final Message message = messageEntity.getSender().execute(sendMessage);
        banList.put(messageEntity.getChatId(), new BanInfo(banUser, message.getMessageId()).addUserID(userID));
        messageEntity.getSender().execute(new PinChatMessage().setChatId(messageEntity.getChatId()).setMessageId(message.getMessageId()));

        ex.setRemoveOnCancelPolicy(true);

        scheduler.schedule(() -> {
            try {
                endVote(messageEntity.getChatId(), banList.get(messageEntity.getChatId()).getMessageID(), messageEntity.getSender());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }, new Date(System.currentTimeMillis() + DELAY_TO_UPDATE));
    }

    @Override
    public boolean isRegardingCallback(CallbackQuery callbackQuery) {
        final String callback = callbackQuery.getData();
        if (banList.get(String.valueOf(callbackQuery.getMessage().getChatId())) != null && StringUtils.containsIgnoreCase(callback, "kick:")) {
            return true;
        }
        return false;
    }

    @Override
    public void processCallBack(CallbackQuery callbackQuery, AbsSender sender) throws TelegramApiException {
        final String chatID = String.valueOf(callbackQuery.getMessage().getChatId());
        BanInfo banInfo = banList.get(chatID);
        String message;
        if (callbackQuery.getData().equalsIgnoreCase("kick:false")) {
            message = "Слабак. Это бесполезная кнопка";
        } else {
            if (banInfo.getUserIDS().contains(callbackQuery.getFrom().getId())) {
                message = "Ты уже голосовал, почему тебе так не нравится " + banInfo.getUser().getFirstName() + "?";
            } else {
                if (banInfo.user.getId().equals(callbackQuery.getFrom().getId())) {
                    message = "Самокик!!?!??!?";
                } else {
                    banInfo.addUserID(callbackQuery.getFrom().getId());
                    message = "Твой АНОНИМНЫЙ голос учтён";
                    Integer count = sender.execute(new GetChatMemberCount().setChatId(chatID));
                    int size = count > 10 ? 2 : 1;
                    if (banInfo.getUserIDS().size() > size) {
                        //sender.execute(new KickChatMember().setChatId(chatID).setUserId(banInfo.getUser().getId()));
                        sender.execute(new RestrictChatMember().setCanSendOtherMessages(false).setChatId(chatID).setUserId(banInfo.getUser().getId()));
                        sender.execute(new SendMessage().setChatId(chatID).setText(banInfo.getUser().getFirstName() + " был забанен на 5 минут по просьбе участников."));
                        endVote(chatID, banInfo.getMessageID(), sender);

                        scheduler.schedule(() -> {
                            try {
                                sender.execute(new SendMessage().setChatId(chatID).setText(banInfo.getUser().getFirstName() + " вернулся к разговору, но контекст уже упущен..."));
                                sender.execute(new RestrictChatMember().setCanSendMessages(true).setCanSendOtherMessages(true).setCanAddWebPagePreviews(true).setCanSendMediaMessages(true).setChatId(chatID).setUserId(banInfo.getUser().getId()));
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }, new Date(System.currentTimeMillis() + DELAY_TO_UPDATE));
                    }
                }
            }
        }
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
        answerCallbackQuery.setShowAlert(true);
        answerCallbackQuery.setText(message);
        sender.execute(answerCallbackQuery);

    }

    private void endVote(final String chatID, final Integer messageID, final AbsSender sender) throws TelegramApiException {
        banList.remove(chatID);
        sender.execute(new DeleteMessage().setChatId(chatID).setMessageId(messageID));
        try {
            sender.execute(new UnpinChatMessage().setChatId(chatID));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static class BanInfo {
        private User user;
        private List<Integer> userIDS = new ArrayList<>();
        private Integer messageID;

        private BanInfo(User user, Integer messageID) {
            this.user = user;
            this.messageID = messageID;
        }

        public User getUser() {
            return user;
        }

        public BanInfo setUser(User user) {
            this.user = user;
            return this;
        }

        public List<Integer> getUserIDS() {
            return userIDS;
        }

        public BanInfo addUserID(Integer userID) {
            this.userIDS.add(userID);
            return this;
        }

        public Integer getMessageID() {
            return messageID;
        }
    }
}
