package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReconnaissanceMessageProcessor implements MessageProcessor {

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
        SendMessage message = new SendMessage();
        message.setChatId(messageEntity.getChatId());
        message.setText("Кто тут? Отзовись!");


        // Create ReplyKeyboardMarkup object
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        // Create the keyboard (list of keyboard rows)
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        // Create a keyboard row

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Я тутъ");
        inlineKeyboardButton.setCallbackData("who:iam");

        inlineKeyboardButtons.add(inlineKeyboardButton);
        keyboard.add(inlineKeyboardButtons);
        keyboardMarkup.setKeyboard(keyboard);


        //keyboardMarkup.setKeyboard(keyboard);
        // Add it to the message
        message.setReplyMarkup(keyboardMarkup);

        messageEntity.getSender().sendMessage(message);

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
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
        answerCallbackQuery.setShowAlert(true);
        answerCallbackQuery.setText("Спасибо, братишка " + callbackQuery.getFrom().getUserName());
        sender.answerCallbackQuery(answerCallbackQuery);

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
        editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(callbackQuery.getMessage().getChatId()));
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setText("Спасибо, я нашёл тут:" + callbackQuery.getFrom().getUserName());

        sender.editMessageReplyMarkup(editMessageReplyMarkup);
        sender.editMessageText(editMessageText);
    }
}
