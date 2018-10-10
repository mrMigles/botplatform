package ru.holyway.botplatform.telegram.processor;

import java.net.URI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.core.entity.InstaFollow;
import ru.holyway.botplatform.core.entity.InstaPost;
import ru.holyway.botplatform.core.entity.InstaUser;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

@Component
@Order(11)
public class InstagramMessageProcessor implements MessageProcessor {

  private final DataHelper dataHelper;
  private final TaskScheduler taskScheduler;
  private final RestTemplate restTemplate;

  public InstagramMessageProcessor(DataHelper dataHelper,
      TaskScheduler taskScheduler, RestTemplate restTemplate) {
    this.dataHelper = dataHelper;
    this.taskScheduler = taskScheduler;
    this.restTemplate = restTemplate;
  }

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    final String text = messageEntity.getText();
    if (StringUtils.isEmpty(text)) {
      return false;
    }
    if (text.startsWith("/follow ") || text.startsWith("/unfollow ") || text
        .startsWith("/insta ")) {
      return true;
    }
    return false;
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    final String text = messageEntity.getText();
//    if (text.startsWith("/follow ")) {
//      String userName = text.substring(8);
//      if (dataHelper.getSettings().getFollow(messageEntity.getChatId(), userName) != null) {
//        messageEntity.getSender().execute(
//            new SendMessage().setText("Уже подписан на него").setChatId(messageEntity.getChatId()));
//        return;
//      }
//      dataHelper.getSettings().addFollow(messageEntity.getChatId(), userName);
//      taskScheduler.scheduleAtFixedRate(() -> {
//        InstaUser instaUser = perfrom(messageEntity.getChatId(), userName);
//
//      }, TimeUnit.MINUTES.toMillis(5));
//    }
    if (text.startsWith("/insta ")) {
      String userName = text.substring(7);
      InstaUser instaUser = perfrom(messageEntity.getChatId(), userName);
      int size = instaUser.getPosts().size() > 5 ? 5 : instaUser.getPosts().size();
      for (int i = 0; i < size; i++) {
        InstaPost instaPost = instaUser.getPosts().get(i);
        messageEntity.getSender().execute(new SendMessage().setText(
            "[.](" +instaPost.getPhotoUrl() + ") \n"
                + instaPost.getPostUrl()
                + "\n" + instaPost
                .getDescription()).setChatId(messageEntity.getChatId()).enableMarkdown(true));
      }
    }


  }

  private InstaUser perfrom(final String chatId, final String userId) {
    final InstaFollow instaFollow = dataHelper.getSettings().getFollow(chatId, userId);
    String last = "0";
    if (instaFollow != null) {
      last = instaFollow.getLastId();
    }
    return restTemplate
        .getForObject(URI.create("https://instaprovider.now.sh/api/insta/" + userId + "/" + last),
            InstaUser.class);
  }

  @Override
  public boolean isRegardingCallback(CallbackQuery callbackQuery) {
    return false;
  }

  @Override
  public void processCallBack(CallbackQuery callbackQuery, AbsSender sender)
      throws TelegramApiException {

  }
}
