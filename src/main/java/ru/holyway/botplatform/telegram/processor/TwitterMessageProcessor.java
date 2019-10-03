package ru.holyway.botplatform.telegram.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.core.entity.TwitterFollow;
import ru.holyway.botplatform.core.entity.TwitterResponse;
import ru.holyway.botplatform.core.entity.TwitterResponse.Tweet;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

@Component
@Order(13)
public class TwitterMessageProcessor implements MessageProcessor, MessagePostLoader {

  private final DataHelper dataHelper;
  private final TaskScheduler taskScheduler;
  private final RestTemplate restTemplate;

  private Map<String, ScheduledFuture> futureMap = new HashMap<>();

  public TwitterMessageProcessor(DataHelper dataHelper,
      TaskScheduler taskScheduler,
      @Qualifier("instaproviderTemplate") RestTemplate restTemplate) {
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
    if (text.startsWith("/followt ") || text.startsWith("/unfollowt ") || text
        .startsWith("/twitter ")) {
      return true;
    }
    return false;
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    final String text = messageEntity.getText();
    if (text.startsWith("/followt ")) {
      String userName = text.substring(9);
      if (dataHelper.getSettings().getTwitterFollow(messageEntity.getChatId(), userName) != null) {
        messageEntity.getSender().execute(
            new SendMessage().setText("Уже подписан на него").setChatId(messageEntity.getChatId()));
        return;
      }
      dataHelper.getSettings().addTwitterFollow(messageEntity.getChatId(), userName);
      dataHelper.updateSettings();
      initScheduller(messageEntity.getSender(), messageEntity.getChatId());
      messageEntity.getSender()
          .execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Ok"));
    }
    if (text.startsWith("/unfollowt ")) {
      String userName = text.substring(11);
      dataHelper.getSettings().removeTwitterFollow(messageEntity.getChatId(), userName);
      dataHelper.updateSettings();
      initScheduller(messageEntity.getSender(), messageEntity.getChatId());
      messageEntity.getSender()
          .execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Ok"));
    }
    if (text.startsWith("/twitter ")) {
      String userName = text.substring(9);
      sendTweetsForUser(messageEntity.getSender(), messageEntity.getChatId(), userName);
    }


  }

  private void initScheduller(AbsSender sender, String chatID) {
    ScheduledFuture scheduledFuture = futureMap.get(chatID);
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
    }
    futureMap.put(chatID, taskScheduler.scheduleAtFixedRate(() -> {
      Set<TwitterFollow> twitterFollows = dataHelper.getSettings().getTwitterFollows()
          .get(chatID);

      for (TwitterFollow twitterFollow : twitterFollows) {
        try {
          sendTweetsForUser(sender, chatID,
              twitterFollow.getUserName());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

    }, TimeUnit.MINUTES.toMillis(5)));
  }

  private void sendTweetsForUser(AbsSender sender, String chatId, String userName)
      throws TelegramApiException {
    try {
      TwitterResponse twitterResponse = getTweets(chatId, userName);
      List<TwitterResponse.Tweet> tweets = twitterResponse.getTweetList();
      if (tweets.size() < 1) {
        return;
      }
      int size = tweets.size() > 5 ? 5 : tweets.size();
      TwitterFollow twitterFollow = dataHelper.getSettings().getTwitterFollow(chatId, userName);
      if (twitterFollow != null) {
        if (twitterFollow.getLastId() == null || twitterFollow.getLastId()
            .equalsIgnoreCase("0")) {
          size = 1;
        }
        twitterFollow.setLastId(tweets.get(0).getId());
        dataHelper.updateSettings();
      }
      for (int i = 0; i < size; i++) {
        Tweet tweet = tweets.get(i);

        sender.execute(new SendMessage().setText(
            "New tweet by <b>" + twitterResponse.getName() + "</b>:\n\n" + StringEscapeUtils
                .escapeHtml4(tweet.getText())
                + "\n\nlook at " + makeLink("original tweet",
                "https://twitter.com/" + twitterResponse
                    .getScreenName())).setChatId(chatId).enableHtml(true));
      }
    } catch (Exception e) {
      System.out.println("UUUUUUUUUUUUUUUps");
      e.printStackTrace();
    }

  }

  private String makeLink(String name, String link) {
    return "<a href=\"" + link + "\">" + name + "</a>";
  }

  private TwitterResponse getTweets(final String chatId, final String userId) {
    final TwitterFollow twitterFollow = dataHelper.getSettings().getTwitterFollow(chatId, userId);
    String last = "0";
    if (twitterFollow != null) {
      last = twitterFollow.getLastId();
    }
    ResponseEntity<TwitterResponse> response = restTemplate.exchange(
        "/api/twitter/" + userId,
        HttpMethod.GET,
        null,
        TwitterResponse.class);

    TwitterResponse twitterResponse = response.getBody();
    twitterResponse.setTweetList(getTwitterResponse(last, twitterResponse.getTweetList()));
    return twitterResponse;
  }

  @NotNull
  private List<Tweet> getTwitterResponse(String last, List<Tweet> tweets) {
    List<Tweet> result = new ArrayList<>();

    for (Tweet tweet : tweets) {
      if (tweet.getId().equals(last)) {
        return result;
      }
      result.add(tweet);
    }
    return result;
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
    Map<String, Set<TwitterFollow>> follows = dataHelper.getSettings().getTwitterFollows();
    for (String chatId : follows.keySet()) {
      initScheduller(absSender, chatId);
    }
  }
}
