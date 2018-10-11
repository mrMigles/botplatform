package ru.holyway.botplatform.telegram.processor;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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
public class InstagramMessageProcessor implements MessageProcessor, MessagePostLoader {

  private final DataHelper dataHelper;
  private final TaskScheduler taskScheduler;
  private final RestTemplate restTemplate;

  private Map<String, ScheduledFuture> futureMap = new HashMap<>();

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
    if (text.startsWith("/follow ")) {
      String userName = text.substring(8);
      if (dataHelper.getSettings().getFollow(messageEntity.getChatId(), userName) != null) {
        messageEntity.getSender().execute(
            new SendMessage().setText("Уже подписан на него").setChatId(messageEntity.getChatId()));
        return;
      }
      dataHelper.getSettings().addFollow(messageEntity.getChatId(), userName);
      dataHelper.updateSettings();
      initScheduller(messageEntity.getSender(), messageEntity.getChatId());
      messageEntity.getSender()
          .execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Ok"));
    }
    if (text.startsWith("/unfollow ")) {
      String userName = text.substring(10);
      dataHelper.getSettings().removeFollow(messageEntity.getChatId(), userName);
      dataHelper.updateSettings();
      initScheduller(messageEntity.getSender(), messageEntity.getChatId());
      messageEntity.getSender()
          .execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Ok"));
    }
    if (text.startsWith("/insta ")) {
      String userName = text.substring(7);
      sendInstaForUser(messageEntity.getSender(), messageEntity.getChatId(), userName);
    }


  }

  private void initScheduller(AbsSender sender, String chatID) {
    ScheduledFuture scheduledFuture = futureMap.get(chatID);
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
    }
    futureMap.put(chatID, taskScheduler.scheduleAtFixedRate(() -> {
      Set<InstaFollow> instaFollows = dataHelper.getSettings().getFollows()
          .get(chatID);

      for (InstaFollow instaFollow : instaFollows) {
        try {
          sendInstaForUser(sender, chatID,
              instaFollow.getUserName());
        } catch (TelegramApiException e) {
          e.printStackTrace();
        }
      }

    }, TimeUnit.MINUTES.toMillis(2)));
  }

  private void sendInstaForUser(AbsSender sender, String chatId, String userName)
      throws TelegramApiException {
    InstaUser instaUser = getPosts(chatId, userName);
    if (instaUser.getPosts().size() < 1) {
      return;
    }
    int size = instaUser.getPosts().size() > 5 ? 5 : instaUser.getPosts().size();
    InstaFollow instaFollow = dataHelper.getSettings().getFollow(chatId, userName);
    if (instaFollow != null) {
      if (instaFollow.getLastPostIdId() == null || instaFollow.getLastPostIdId().equalsIgnoreCase("0")) {
        size = 1;
      }
      instaFollow.setLastPostIdId(instaUser.getPosts().get(0).getID());
      dataHelper.updateSettings();
    }
    for (int i = 0; i < size; i++) {
      InstaPost instaPost = instaUser.getPosts().get(i);
      sender.execute(new SendMessage().setText(
          "Instapost[.](" + instaPost.getPhotoUrl() + ") By user [" + instaUser.getUserName() + "]("
              + "https://instagram.com/" + instaUser.getUserName() + ")\n\n"
              + instaPost.getDescription() + "\n\nlook at [original post](" + instaPost.getPostUrl()
              + ") \n").setChatId(chatId).enableMarkdown(true));
    }
  }

  private InstaUser getPosts(final String chatId, final String userId) {
    final InstaFollow instaFollow = dataHelper.getSettings().getFollow(chatId, userId);
    String last = "0";
    if (instaFollow != null) {
      last = instaFollow.getLastPostIdId();
    }
    return restTemplate
        .getForObject(URI.create("https://instaprovider.now.sh/api/posts/" + userId + "/" + last),
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

  @Override
  public void postRun(AbsSender absSender) {
    Map<String, Set<InstaFollow>> follows = dataHelper.getSettings().getFollows();
    for (String chatId : follows.keySet()) {
      initScheduller(absSender, chatId);
    }
  }
}
