package ru.holyway.botplatform.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class ChatAdminService {

  public List<ChatMember> getAdministrators(AbsSender sender, Object chatId) throws TelegramApiException {
    return sender.execute(GetChatAdministrators.builder().chatId(chatId.toString()).build());
  }

  public boolean isAdmin(AbsSender sender, Object chatId, Long userId) throws TelegramApiException {
    return getAdministrators(sender, chatId).stream()
        .anyMatch(chatMember -> chatMember.getUser().getId().equals(userId));
  }
}
