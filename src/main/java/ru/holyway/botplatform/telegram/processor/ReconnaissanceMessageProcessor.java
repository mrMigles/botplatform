package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMemberCount;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Order(2)
public class ReconnaissanceMessageProcessor implements MessageProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReconnaissanceMessageProcessor.class);

  private Map<String, List<String>> currentReconChatMembers = new HashMap<>();

  private Map<String, List<String>> chatMembers = new HashMap<>();

  private final ScheduledThreadPoolExecutor ex = (ScheduledThreadPoolExecutor) Executors
      .newScheduledThreadPool(5);

  private final TaskScheduler scheduler = new ConcurrentTaskScheduler(ex);

  private static final long DELAY_TO_UPDATE = TimeUnit.MINUTES.toMillis(2);

  private final DataHelper dataHelper;

  @Autowired
  public ReconnaissanceMessageProcessor(DataHelper dataHelper) {
    this.dataHelper = dataHelper;
  }

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    final String mes = messageEntity.getText();
    if (messageEntity.getMessage().getFrom() != null && !messageEntity.getMessage().getFrom().getIsBot()) {
      reconUser(messageEntity);
    }
    return StringUtils.equals(mes, "/who") || StringUtils.equalsIgnoreCase(mes, "Пахом, кто тут");
  }

  private void reconUser(TelegramMessageEntity messageEntity) {
    final String userId = String.valueOf(messageEntity.getMessage().getFrom().getId());
    if (chatMembers.get(messageEntity.getChatId()) != null) {
      if (!chatMembers.get(messageEntity.getChatId()).contains(userId)) {
        updateMember(messageEntity.getChatId(), userId);
      }
    } else {
      updateMember(messageEntity.getChatId(), userId);
    }
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    sendReconMessage(messageEntity);
  }

  private void sendReconMessage(TelegramMessageEntity messageEntity)
      throws TelegramApiException {
    if (currentReconChatMembers.get(messageEntity.getChatId()) != null) {
      return;
    }
    SendMessage message = new SendMessage();
    if (dataHelper.getChatMembers(messageEntity.getChatId()) != null) {

      message.setChatId(messageEntity.getChatId());
      message.setText(
          "Я уже искал тут вас, ну что вы опять начинаете... Давайте заново.\nКто тут? Отзовись!");

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
    messageEntity.getSender()
        .execute(PinChatMessage.builder().chatId(String.valueOf(mes.getChatId())).messageId(mes.getMessageId()).build());

    ex.setRemoveOnCancelPolicy(true);

    scheduler.schedule(() -> {
      try {
        showResult(message.getChatId(), mes.getMessageId(), messageEntity.getSender());
      } catch (TelegramApiException e) {
        LOGGER.error("Error showing recon result for chat {}", message.getChatId(), e);
      }
    }, new Date(System.currentTimeMillis() + DELAY_TO_UPDATE));
  }

  @Override
  public boolean isRegardingCallback(CallbackQuery callbackQuery) {
    return StringUtils.containsIgnoreCase(callbackQuery.getData(), "who:");
  }

  @Override
  public void processCallBack(CallbackQuery callbackQuery, AbsSender sender)
      throws TelegramApiException {

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
        showResult(String.valueOf(callbackQuery.getMessage().getChatId()),
            callbackQuery.getMessage().getMessageId(), sender);
      } else {
        Integer userCount = sender.execute(GetChatMemberCount.builder().chatId(chatID).build());
        if (userCount - 1 == currentChatMembers.size()) {
          showResult(String.valueOf(callbackQuery.getMessage().getChatId()),
              callbackQuery.getMessage().getMessageId(), sender);
        }
      }
    }

    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
    answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
    answerCallbackQuery.setShowAlert(true);
    answerCallbackQuery.setText(message);
    sender.execute(answerCallbackQuery);


  }

  private void showResult(String chatId, Integer messageId, AbsSender sender)
      throws TelegramApiException {
    EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
    editMessageReplyMarkup.setChatId(chatId);
    editMessageReplyMarkup.setMessageId(messageId);
    sender.execute(editMessageReplyMarkup);
    sender.execute(DeleteMessage.builder().chatId(chatId).messageId(messageId).build());
    List<String> users = new ArrayList<>();
    for (String user : currentReconChatMembers.get(chatId)) {
      users.add(
          sender.execute(GetChatMember.builder().chatId(chatId).userId(Long.valueOf(user)).build())
              .getUser().getUserName());
    }
    sender.execute(SendMessage.builder().chatId(chatId)
        .text("Спасибо за отклик, братишки:\n" + StringUtils.join(users, "\n")).build());

    try {
      sender.execute(UnpinChatMessage.builder().chatId(chatId).build());
    } catch (Exception e) {
      LOGGER.warn("Could not unpin message in chat {}", chatId, e);
    }

    if (currentReconChatMembers.get(chatId) != null && !currentReconChatMembers.get(chatId)
        .isEmpty()) {
      updateMembers(chatId, currentReconChatMembers.get(chatId));
    }
    currentReconChatMembers.remove(chatId);
  }


  private void updateMember(final String chatID, final String userId) {
    final Set<String> userIds = new HashSet<>(
        chatMembers.get(chatID) != null ? chatMembers.get(chatID)
            : dataHelper.getChatMembers(chatID) != null ? dataHelper.getChatMembers(chatID)
            : new HashSet<>());
    userIds.add(userId);
    dataHelper.updateChatMembers(chatID, new ArrayList<>(userIds));
    chatMembers.put(chatID, new ArrayList<>(userIds));
  }

  private void updateMembers(final String chatID, final List<String> newUserIds) {
    final Set<String> userIds = new HashSet<>(
        chatMembers.get(chatID) != null ? chatMembers.get(chatID)
            : dataHelper.getChatMembers(chatID) != null ? dataHelper.getChatMembers(chatID)
            : new HashSet<>());
    userIds.addAll(newUserIds);
    dataHelper.updateChatMembers(chatID, new ArrayList<>(userIds));
    chatMembers.put(chatID, new ArrayList<>(userIds));
  }
}
