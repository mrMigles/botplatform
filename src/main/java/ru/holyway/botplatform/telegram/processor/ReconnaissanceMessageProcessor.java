package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatMemberCount;
import org.telegram.telegrambots.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.api.methods.pinnedmessages.UnpinChatMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.botplatform.core.data.DataService;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Order(1)
public class ReconnaissanceMessageProcessor implements MessageProcessor {

    private Map<String, List<String>> currentReconChatMembers = new HashMap<>();

    private final ScheduledThreadPoolExecutor ex = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(5);

    private final TaskScheduler scheduler = new ConcurrentTaskScheduler(ex);

    private static final long DELAY_TO_UPDATE = TimeUnit.MINUTES.toMillis(2);

    @Autowired
    DataService dataService;

    @Override
    public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
        final String mes = messageEntity.getText();
        if (StringUtils.equals(mes, "/who") || StringUtils.equalsIgnoreCase(mes, "Пахом, кто тут")) {
            return true;
        }
        return false;
    }

    @Override
    public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
        if (currentReconChatMembers.get(messageEntity.getChatId()) != null) {
            return;
        }
        SendMessage message = new SendMessage();
        if (dataService.getChatMembers(messageEntity.getChatId()) != null) {

            message.setChatId(messageEntity.getChatId());
            message.setText("Я уже искал тут вас, ну что вы опять начинаете... Давайте заново.\nКто тут? Отзовись!");

        } else {
            message.setChatId(messageEntity.getChatId());
            message.setText("Кто тут? Отзовись!");
        }


        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Я тутъ");
        inlineKeyboardButton.setCallbackData("who:iam");

        inlineKeyboardButtons.add(inlineKeyboardButton);
        keyboard.add(inlineKeyboardButtons);
        keyboardMarkup.setKeyboard(keyboard);

        message.setReplyMarkup(keyboardMarkup);

        final Message mes = messageEntity.getSender().execute(message);
        messageEntity.getSender().execute(new PinChatMessage().setChatId(mes.getChatId()).setMessageId(mes.getMessageId()));

        ex.setRemoveOnCancelPolicy(true);

        scheduler.schedule(() -> {
            try {
                showResult(message.getChatId(), mes.getMessageId(), messageEntity.getSender());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }, new Date(System.currentTimeMillis() + DELAY_TO_UPDATE));

    }

    @Override
    public boolean isRegardingCallback(CallbackQuery callbackQuery) {
        final String callback = callbackQuery.getData();
        if (StringUtils.containsIgnoreCase(callback, "who:")) {
            return true;
        }
        return false;
    }

    @Override
    public void processCallBack(CallbackQuery callbackQuery, AbsSender sender) throws TelegramApiException {

        final String chatID = String.valueOf(callbackQuery.getMessage().getChatId());
        List<String> currentChatMembers = currentReconChatMembers.get(chatID);
        if (currentChatMembers == null) {
            currentChatMembers = new ArrayList<>();
        }

        final String userID = String.valueOf(callbackQuery.getFrom().getId());

        final String message;
        if (currentChatMembers.contains(userID)) {
            message = "Да всё всё, браток, я понял что ты тут.";
        } else {
            message = "Спасибо, братишка, вижу тебя";
            currentChatMembers.add(userID);
            currentReconChatMembers.put(chatID, currentChatMembers);

            if (callbackQuery.getMessage().getChat().isUserChat()) {
                showResult(String.valueOf(callbackQuery.getMessage().getChatId()), callbackQuery.getMessage().getMessageId(), sender);
            } else {
                Integer userCount = sender.execute(new GetChatMemberCount().setChatId(chatID));
                if (userCount - 1 == currentChatMembers.size()) {
                    showResult(String.valueOf(callbackQuery.getMessage().getChatId()), callbackQuery.getMessage().getMessageId(), sender);
                }
            }
        }

        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
        answerCallbackQuery.setShowAlert(true);
        answerCallbackQuery.setText(message);
        sender.execute(answerCallbackQuery);


    }

    private void showResult(String chatId, Integer messageId, AbsSender sender) throws TelegramApiException {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        sender.execute(editMessageReplyMarkup);
        sender.execute(new DeleteMessage().setChatId(chatId).setMessageId(messageId));
        List<String> users = new ArrayList<>();
        for (String user : currentReconChatMembers.get(chatId)) {
            users.add(sender.execute(new GetChatMember().setChatId(chatId).setUserId(Integer.valueOf(user))).getUser().getUserName());
        }
        sender.execute(new SendMessage().setChatId(chatId).setText("Спасибо за отклик, братишки:\n" + StringUtils.join(users, "\n")));

        try {
            sender.execute(new UnpinChatMessage().setChatId(chatId));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (currentReconChatMembers.get(chatId) != null && !currentReconChatMembers.get(chatId).isEmpty()) {
            dataService.updateChatMembers(chatId, currentReconChatMembers.get(chatId));
        }
        currentReconChatMembers.remove(chatId);
    }
}
