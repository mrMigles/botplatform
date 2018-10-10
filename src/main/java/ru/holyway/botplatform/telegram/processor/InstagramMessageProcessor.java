package ru.holyway.botplatform.telegram.processor;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
public class InstagramMessageProcessor implements MessageProcessor {

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
      initScheduller(messageEntity);
      messageEntity.getSender()
          .execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Ok"));
    }
    if (text.startsWith("/unfollow ")) {
      String userName = text.substring(10);
      dataHelper.getSettings().removeFollow(messageEntity.getChatId(), userName);
      dataHelper.updateSettings();
      initScheduller(messageEntity);
      messageEntity.getSender()
          .execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Ok"));
    }
    if (text.startsWith("/insta ")) {
      String userName = text.substring(7);
      sendInstaForUser(messageEntity, userName);
    }


  }

  private void initScheduller(TelegramMessageEntity messageEntity) {
    ScheduledFuture scheduledFuture = futureMap.get(messageEntity.getChatId());
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
    }
    futureMap.put(messageEntity.getChatId(), taskScheduler.scheduleAtFixedRate(() -> {
      Set<InstaFollow> instaFollows = dataHelper.getSettings().getFollows()
          .get(messageEntity.getChatId());

      for (InstaFollow instaFollow : instaFollows) {
        try {
          sendInstaForUser(messageEntity, instaFollow.getUserName());
        } catch (TelegramApiException e) {
          e.printStackTrace();
        }
      }

    }, TimeUnit.MINUTES.toMillis(2)));
  }

  private void sendInstaForUser(TelegramMessageEntity messageEntity, String userName)
      throws TelegramApiException {
    InstaUser instaUser = perfrom(messageEntity.getChatId(), userName);
    int size = instaUser.getPosts().size() > 1 ? 1 : instaUser.getPosts().size();
    if (size > 0) {
      InstaFollow instaFollow = dataHelper.getSettings()
          .getFollow(messageEntity.getChatId(), userName);
      if (instaFollow != null) {
        instaFollow.setLastId(instaUser.getPosts().get(0).getID());
        dataHelper.updateSettings();
      }
    }
    for (int i = 0; i < size; i++) {
      InstaPost instaPost = instaUser.getPosts().get(i);
      messageEntity.getSender().execute(new SendMessage().setText(
          "Instapost[.](" + instaPost.getPhotoUrl() + ") By user [" + instaUser.getUserName() + "]("
              + "https://instagram.com/" + instaUser.getUserName() + ")\n\n"
              + instaPost.getDescription() + "\n\nlook at [original post](" + instaPost.getPostUrl()
              + ") \n").setChatId(messageEntity.getChatId()).enableMarkdown(true));
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
