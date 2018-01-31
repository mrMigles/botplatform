package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.util.ArrayList;
import java.util.List;

@Component
public class TeaMessageProcessor implements MessageProcessor {

    @Override
    public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
        final String mes = messageEntity.getText();
        if (StringUtils.equals(mes, "/tea")) {
            return true;
        }
        return false;
    }

    @Override
    public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(messageEntity.getChatId());
        message.setText("Custom message text");
        // Create ReplyKeyboardMarkup object
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        // Create the keyboard (list of keyboard rows)
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        // Create a keyboard row

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Да");
        inlineKeyboardButton.setCallbackData("param");
        inlineKeyboardButton.setSwitchInlineQuery("tea");

        inlineKeyboardButtons.add(inlineKeyboardButton);

        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Нет");
        inlineKeyboardButton.setCallbackData("param");
        inlineKeyboardButton.setSwitchInlineQuery("tea");

        inlineKeyboardButtons.add(inlineKeyboardButton);
        keyboard.add(inlineKeyboardButtons);

        keyboardMarkup.setKeyboard(keyboard);


        //keyboardMarkup.setKeyboard(keyboard);
        // Add it to the message
        message.setReplyMarkup(keyboardMarkup);

        messageEntity.getSender().sendMessage(message);

    }
}
