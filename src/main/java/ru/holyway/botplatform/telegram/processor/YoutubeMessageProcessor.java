package ru.holyway.botplatform.telegram.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
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
import ru.holyway.botplatform.core.entity.YouTubeChanel;
import ru.holyway.botplatform.core.entity.YoutubeVideo;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

@Component
@Order(12)
public class YoutubeMessageProcessor implements MessageProcessor, MessagePostLoader {

  private final DataHelper dataHelper;
  private final TaskScheduler taskScheduler;
  private final RestTemplate restTemplate;

  private Map<String, ScheduledFuture> futureMap = new HashMap<>();

  public YoutubeMessageProcessor(DataHelper dataHelper,
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
    if (text.startsWith("/followy ") || text.startsWith("/unfollowy ") || text
        .startsWith("/youtube ")) {
      return true;
    }
    return false;
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    final String text = messageEntity.getText();
    if (text.startsWith("/followy ")) {
      String userName = text.substring(9);
      if (dataHelper.getSettings().getYoutubeFollow(messageEntity.getChatId(), userName) != null) {
        messageEntity.getSender().execute(
            new SendMessage().setText("Уже подписан на него").setChatId(messageEntity.getChatId()));
        return;
      }
      dataHelper.getSettings().addYoutubeFollow(messageEntity.getChatId(), userName);
      dataHelper.updateSettings();
      initScheduller(messageEntity.getSender(), messageEntity.getChatId());
      messageEntity.getSender()
          .execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Ok"));
    }
    if (text.startsWith("/unfollowy ")) {
      String userName = text.substring(11);
      dataHelper.getSettings().removeYoutubeFollow(messageEntity.getChatId(), userName);
      dataHelper.updateSettings();
      initScheduller(messageEntity.getSender(), messageEntity.getChatId());
      messageEntity.getSender()
          .execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Ok"));
    }
    if (text.startsWith("/youtube ")) {
      String userName = text.substring(9);
      sendYoutubeForUser(messageEntity.getSender(), messageEntity.getChatId(), userName);
    }


  }

  private void initScheduller(AbsSender sender, String chatID) {
    ScheduledFuture scheduledFuture = futureMap.get(chatID);
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
    }
    futureMap.put(chatID, taskScheduler.scheduleAtFixedRate(() -> {
      Set<YouTubeChanel> youTubeChanels = dataHelper.getSettings().getYoutubeFollows()
          .get(chatID);

      for (YouTubeChanel youTubeChanel : youTubeChanels) {
        try {
          sendYoutubeForUser(sender, chatID,
              youTubeChanel.getChannelName());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

    }, TimeUnit.MINUTES.toMillis(2)));
  }

  private void sendYoutubeForUser(AbsSender sender, String chatId, String userName)
      throws TelegramApiException {
    try {
      List<YoutubeVideo> videos = getPosts(chatId, userName);
      if (videos.size() < 1) {
        return;
      }
      int size = videos.size() > 5 ? 5 : videos.size();
      YouTubeChanel youTubeChanel = dataHelper.getSettings().getYoutubeFollow(chatId, userName);
      if (youTubeChanel != null) {
        if (youTubeChanel.getLastId() == null || youTubeChanel.getLastId()
            .equalsIgnoreCase("0")) {
          size = 1;
        }
        youTubeChanel.setLastId(videos.get(0).getId());
        dataHelper.updateSettings();
      }
      for (int i = 0; i < size; i++) {
        YoutubeVideo video = videos.get(i);
        sender.execute(new SendMessage().setText(
            "YouTube " + (video.getLive() ? "Live" : "Video") + " [.](" + video.getLink()
                + ") By channel [" + userName
                + "]("
                + "https://www.youtube.com/user/" + userName + ")\n\n"
                + video.getDescription() + "\n\nlook at [original video](" + video
                .getLink()
                + ") \n").setChatId(chatId).enableMarkdown(true));
      }
    } catch (Exception e) {
      System.out.println("UUUUUUUUUUUUUUUps");
      e.printStackTrace();
    }

  }

  private List<YoutubeVideo> getPosts(final String chatId, final String userId) {
    final YouTubeChanel youtubeFollow = dataHelper.getSettings().getYoutubeFollow(chatId, userId);
    String last = "0";
    if (youtubeFollow != null) {
      last = youtubeFollow.getLastId();
    }
    ResponseEntity<List<YoutubeVideo>> response = restTemplate.exchange(
        "https://instaprovider.now.sh/api/youtube/" + userId,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<YoutubeVideo>>() {
        });

    List<YoutubeVideo> youtubeVideos = response.getBody();
    List<YoutubeVideo> result = new ArrayList<>();

    for (YoutubeVideo youtubeVideo : youtubeVideos) {
      if (youtubeVideo.getId().equals(last)) {
        return result;
      }
      result.add(youtubeVideo);
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
    Map<String, Set<YouTubeChanel>> follows = dataHelper.getSettings().getYoutubeFollows();
    for (String chatId : follows.keySet()) {
      initScheduller(absSender, chatId);
    }
  }
}
