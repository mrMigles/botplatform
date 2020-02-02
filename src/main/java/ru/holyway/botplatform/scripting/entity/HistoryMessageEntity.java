package ru.holyway.botplatform.scripting.entity;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.holyway.botplatform.scripting.ScriptContext;

import java.util.function.Function;

public class HistoryMessageEntity extends AbstractTelegramEntity {

    private Integer messageId;

    private Function<ScriptContext, Number> messageIdFunction;

    public HistoryMessageEntity(Integer messageId, Function<ScriptContext, Number> messageIdFunction) {
        this.messageId = messageId;
        this.messageIdFunction = messageIdFunction;
    }

    @Override
    public Function<ScriptContext, Message> entity() {
        return scriptContext -> {
            final String chatId = scriptContext.message.getChatId().apply(scriptContext);
            final Number mId = messageId != null ? messageId : messageIdFunction.apply(scriptContext);
            return (Message) new StaticMessage(chatId, mId);
        };
    }

    public static HistoryMessageEntity message(Integer messageId) {
        return new HistoryMessageEntity(messageId, null);
    }

    public static HistoryMessageEntity message(Function<ScriptContext, Number> messageIdFunction) {
        return new HistoryMessageEntity(null, messageIdFunction);
    }
}
