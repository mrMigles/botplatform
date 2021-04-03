package ru.holyway.botplatform.telegram.processor;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.retry.support.RetryTemplate;
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
import ru.holyway.botplatform.core.entity.InstaStory;
import ru.holyway.botplatform.core.entity.InstaUser;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

//@Component
//@Order(11)
public class InstagramMessageProcessor implements MessageProcessor, MessagePostLoader {

  private final DataHelper dataHelper;
  private final TaskScheduler taskScheduler;
  private final RestTemplate restTemplate;
  private final RetryTemplate retryTemplate;

  private Map<String, ScheduledFuture> futureMap = new HashMap<>();

  public InstagramMessageProcessor(DataHelper dataHelper,
      TaskScheduler taskScheduler,
      @Qualifier("instaproviderTemplate") RestTemplate restTemplate,
      RetryTemplate retryTemplate) {
    this.dataHelper = dataHelper;
    this.taskScheduler = taskScheduler;
    this.restTemplate = restTemplate;
    this.retryTemplate = retryTemplate;
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
      if (dataHelper.getSettings().getInstaFollow(messageEntity.getChatId(), userName) != null) {
        messageEntity.getSender().execute(
            new SendMessage().setText("Уже подписан на него").setChatId(messageEntity.getChatId()));
        return;
      }
      dataHelper.getSettings().addInstaFollow(messageEntity.getChatId(), userName);
      dataHelper.updateSettings();
      initScheduller(messageEntity.getSender(), messageEntity.getChatId());
      messageEntity.getSender()
          .execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Ok"));
    }
    if (text.startsWith("/unfollow ")) {
      String userName = text.substring(10);
      dataHelper.getSettings().removeInstaFollow(messageEntity.getChatId(), userName);
      dataHelper.updateSettings();
      initScheduller(messageEntity.getSender(), messageEntity.getChatId());
      messageEntity.getSender()
          .execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Ok"));
    }
    if (text.startsWith("/insta ")) {
      String userName = text.substring(7);
      sendInstaForUser(messageEntity.getSender(), messageEntity.getChatId(), userName);
      sendStoryForUser(messageEntity.getSender(), messageEntity.getChatId(), userName);
    }


  }

  private void initScheduller(AbsSender sender, String chatID) {
    ScheduledFuture scheduledFuture = futureMap.get(chatID);
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
    }
    futureMap.put(chatID, taskScheduler.scheduleAtFixedRate(() -> {
      Set<InstaFollow> instaFollows = dataHelper.getSettings().getInstaFollows()
          .get(chatID);

      for (InstaFollow instaFollow : instaFollows) {
        try {
          sendInstaForUser(sender, chatID,
              instaFollow.getUserName());
          sendStoryForUser(sender, chatID,
              instaFollow.getUserName());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

    }, TimeUnit.MINUTES.toMillis(5)));
  }

  private void sendInstaForUser(AbsSender sender, String chatId, String userName)
      throws TelegramApiException {
    try {
      InstaUser instaUser = getPosts(chatId, userName);
      if (instaUser.getPosts().size() < 1) {
        return;
      }
      int size = instaUser.getPosts().size() > 5 ? 5 : instaUser.getPosts().size();
      InstaFollow instaFollow = dataHelper.getSettings().getInstaFollow(chatId, userName);
      if (instaFollow != null) {
        if (instaFollow.getLastPostIdId() == null || instaFollow.getLastPostIdId()
            .equalsIgnoreCase("0")) {
          size = 1;
        }
        instaFollow.setLastPostIdId(instaUser.getPosts().get(0).getID());
        dataHelper.updateSettings();
      }
      for (int i = 0; i < size; i++) {
        InstaPost instaPost = instaUser.getPosts().get(i);
        retryTemplate.execute(retryContext -> sender.execute(new SendMessage().setText(
            "Instapost[.](" + instaPost.getPhotoUrl() + ") By user [" + instaUser.getUserName()
                + "]("
                + "https://instagram.com/" + instaUser.getUserName() + ")\n\n"
                + instaPost.getDescription() + "\n\nlook at [original post](" + instaPost
                .getPostUrl()
                + ") \n").setChatId(chatId).enableMarkdown(true)));
      }
    } catch (Exception e) {
      System.out.println("UUUUUUUUUUUUUUUps");
      e.printStackTrace();
    }

  }

  private InstaUser getPosts(final String chatId, final String userId) {
    final InstaFollow instaFollow = dataHelper.getSettings().getInstaFollow(chatId, userId);
    String last = "0";
    if (instaFollow != null) {
      last = instaFollow.getLastPostIdId();
    }
    return restTemplate
        .getForObject("/api/instagram/posts/" + userId + "/" + last,
            InstaUser.class);
  }

  private void sendStoryForUser(AbsSender sender, String chatId, String userName)
      throws TelegramApiException {
    try {
      InstaUser instaUser = getStories(chatId, userName);
      if (instaUser.getStories().size() < 1) {
        return;
      }
      int size = instaUser.getStories().size() > 5 ? 5 : instaUser.getStories().size();
      InstaFollow instaFollow = dataHelper.getSettings().getInstaFollow(chatId, userName);
      if (instaFollow != null) {
        if (instaFollow.getLastStoryId() == null || instaFollow.getLastStoryId()
            .equalsIgnoreCase("0")) {
          size = 1;
        }
        instaFollow
            .setLastStoryId(instaUser.getStories().get(instaUser.getStories().size() - 1).getId());
        dataHelper.updateSettings();
      }
      for (int i = instaUser.getStories().size() - 1; i >= instaUser.getStories().size() - size;
          i--) {
        InstaStory instaPost = instaUser.getStories().get(i);
        retryTemplate.execute(retryContext -> sender.execute(new SendMessage().setText(
            "Instastory[.](" + instaPost.getMediaUrl() + ") By user [" + instaUser.getUserName()
                + "]("
                + "https://instagram.com/" + instaUser.getUserName() + ")\n\n"
                + "look at [original post](" + instaPost
                .getStoryURL()
                + ") \n").setChatId(chatId).enableMarkdown(true)));
      }
    } catch (Exception e) {
      System.out.println("UUUUUUUUUUUUUUUps");
      e.printStackTrace();
    }

  }

  private InstaUser getStories(final String chatId, final String userId) {
    final InstaFollow instaFollow = dataHelper.getSettings().getInstaFollow(chatId, userId);
    String last = "0";
    if (instaFollow != null) {
      last = instaFollow.getLastStoryId();
    }
    return restTemplate
        .getForObject("/api/instagram/stories/" + userId + "/" + last,
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
    Map<String, Set<InstaFollow>> follows = dataHelper.getSettings().getInstaFollows();
    for (String chatId : follows.keySet()) {
      initScheduller(absSender, chatId);
    }
  }
}
