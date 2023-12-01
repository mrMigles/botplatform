package ru.holyway.botplatform.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class InlineWorker implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(InlineWorker.class);

  private AbsSender absSender;
  private final BlockingQueue<InlineQuery> queue;

  public InlineWorker(AbsSender absSender, BlockingQueue<InlineQuery> queue) {
    this.absSender = absSender;
    this.queue = queue;
  }

  @Override
  public void run() {
    while (true) {
      try {
        // Dequeue a chat ID from the priority queue
        InlineQuery update = queue.take();
        List<InlineQueryResult> inlineQueryResults = new ArrayList<>();
        inlineQueryResults.add(InlineQueryResultArticle.builder().id("1").title("Скачать рилс или пост из инстаграмма по ссылке").description("script().when(message.text.cic(\"instagram.com/\")).then(message.reply(text(\"<a href=\\\"\").add(request().get(text(\"http://instaprovider:8080/convert?url=\").add(message.text)).asString()).add(\"\\\">insta</a>\"), ParseMode.HTML))").inputMessageContent(InputTextMessageContent.builder().messageText("script().when(message.text.cic(\"instagram.com/\")).then(message.reply(text(\"<a href=\\\"\").add(request().get(text(\"http://instaprovider:8080/convert?url=\").add(message.text)).asString()).add(\"\\\">insta</a>\"), ParseMode.HTML))").build()).build());
        inlineQueryResults.add(InlineQueryResultArticle.builder().id("2").title("ChatGPT интеграция для текста").description("script().when(message.text.startWith(\"/gpt \").and(message.isReply())).then(message.reply(request().post(\"https://api.openai.com/v1/chat/completions\").header(\"Content-Type\",\"application/json\").header(\"Authorization\", secret.get(\"openai-token-1\")).body(text(\"{\\\"model\\\":\\\"gpt-3.5-turbo\\\",\\\"messages\\\":[{\\\"role\\\":\\\"assistant\\\",\\\"content\\\":\\\"\").add(reply.text.replace(\"\\n\",\" \").replace(\"\\\"\",\"\\\\\\\"\")).add(\"\\\"},{\\\"role\\\":\\\"user\\\",\\\"content\\\":\\\"\").add(message.text.replace(\"/gpt\", \"\").replace(\"\\n\",\"\\\\\\n \").replace(\"\\\"\",\"\\\\\\\"\")).add(\"\\\"}]}\")).asJson(\"$.choices[0].message.content\"))).order(1);\n" +
            "script().when(message.text.startWith(\"/gpt \")).then(message.reply(request().post(\"https://api.openai.com/v1/chat/completions\").header(\"Content-Type\",\"application/json\").header(\"Authorization\",secret.get(\"openai-token-1\")).body(text(\"{\\\"model\\\":\\\"gpt-3.5-turbo\\\",\\\"messages\\\":[{\\\"role\\\":\\\"user\\\",\\\"content\\\":\\\"\").add(message.text.replace(\"/gpt\", \"\").replace(\"\\n\",\"\\\\\\n\").replace(\"\\\"\",\"\\\\\\\"\")).add(\"\\\"}]}\")).asJson(\"$.choices[0].message.content\"))).order(2)").inputMessageContent(InputTextMessageContent.builder().messageText("script().when(message.text.startWith(\"/gpt \").and(message.isReply())).then(message.reply(request().post(\"https://api.openai.com/v1/chat/completions\").header(\"Content-Type\",\"application/json\").header(\"Authorization\", secret.get(\"openai-token-1\")).body(text(\"{\\\"model\\\":\\\"gpt-3.5-turbo\\\",\\\"messages\\\":[{\\\"role\\\":\\\"assistant\\\",\\\"content\\\":\\\"\").add(reply.text.replace(\"\\n\",\" \").replace(\"\\\"\",\"\\\\\\\"\")).add(\"\\\"},{\\\"role\\\":\\\"user\\\",\\\"content\\\":\\\"\").add(message.text.replace(\"/gpt\", \"\").replace(\"\\n\",\"\\\\\\n \").replace(\"\\\"\",\"\\\\\\\"\")).add(\"\\\"}]}\")).asJson(\"$.choices[0].message.content\"))).order(1);\n" +
            "script().when(message.text.startWith(\"/gpt \")).then(message.reply(request().post(\"https://api.openai.com/v1/chat/completions\").header(\"Content-Type\",\"application/json\").header(\"Authorization\",secret.get(\"openai-token-1\")).body(text(\"{\\\"model\\\":\\\"gpt-3.5-turbo\\\",\\\"messages\\\":[{\\\"role\\\":\\\"user\\\",\\\"content\\\":\\\"\").add(message.text.replace(\"/gpt\", \"\").replace(\"\\n\",\"\\\\\\n\").replace(\"\\\"\",\"\\\\\\\"\")).add(\"\\\"}]}\")).asJson(\"$.choices[0].message.content\"))).order(2)").build()).build());
        inlineQueryResults.add(InlineQueryResultArticle.builder().id("3").title("DeepAI интеграция для изображений").description("script().when(message.text.startWith(\"/image \")).then(message.reply(text(\"[\\\\.](\").add(request().post(\"https://api.openai.com/v1/images/generations\").header(\"Content-Type\",\"application/json\").header(\"Authorization\",secret.get(\"openai-token\")).body(text(\"{\\\"prompt\\\":\\\"\").add(message.text.replace(\"/image \",\"\")).add(\"\\\",\\\"n\\\":1,\\\"size\\\":\\\"512x512\\\"}\")).asJson(\"$.data[0].url\")).add(\")\"), ParseMode.MARKDOWNV2))").inputMessageContent(InputTextMessageContent.builder().messageText("script().when(message.text.startWith(\"/image \")).then(message.reply(text(\"[\\\\.](\").add(request().post(\"https://api.openai.com/v1/images/generations\").header(\"Content-Type\",\"application/json\").header(\"Authorization\",secret.get(\"openai-token\")).body(text(\"{\\\"prompt\\\":\\\"\").add(message.text.replace(\"/image \",\"\")).add(\"\\\",\\\"n\\\":1,\\\"size\\\":\\\"512x512\\\"}\")).asJson(\"$.data[0].url\")).add(\")\"), ParseMode.MARKDOWNV2))").build()).build());
        inlineQueryResults.add(InlineQueryResultArticle.builder().id("4").title("Курс валют по запросу").description("script().when(message.text.startWith(\"/usd\")).then(message.send(request().get(\"https://ru.investing.com/currencies/usd-rub\").asXPath(\"/html/body/div[1]/div/div/div/div[2]/main/div/div[1]/div[2]/div[1]/span\")))").inputMessageContent(InputTextMessageContent.builder().messageText("script().when(message.text.startWith(\"/usd\")).then(message.send(request().get(\"https://ru.investing.com/currencies/usd-rub\").asXPath(\"/html/body/div[1]/div/div/div/div[2]/main/div/div[1]/div[2]/div[1]/span\")))").build()).build());
        inlineQueryResults.add(InlineQueryResultArticle.builder().id("5").title("Курс в шапке чата c обновлением каждые 5 минут").description("script().when(every(\"5m\")).then(message(139665).edit(builder(text(\"Курс доллара: \").add(request().get(\"https://ru.investing.com/currencies/usd-rub\").asXPath(\"/html/body/div[1]/div/div/div/div[2]/main/div/div[1]/div[2]/div[1]/span\")).add(\" руб, \").add(\"Курс евро: \").add(request().get(\"https://ru.investing.com/currencies/eur-rub\").asXPath(\"/html/body/div[1]/div/div/div/div/main/div/div[1]/div[2]/div[1]/span\")).add(\" руб.\"))))").inputMessageContent(InputTextMessageContent.builder().messageText("script().when(every(\"5m\")).then(message(139665).edit(builder(text(\"Курс доллара: \").add(request().get(\"https://ru.investing.com/currencies/usd-rub\").asXPath(\"/html/body/div[1]/div/div/div/div[2]/main/div/div[1]/div[2]/div[1]/span\")).add(\" руб, \").add(\"Курс евро: \").add(request().get(\"https://ru.investing.com/currencies/eur-rub\").asXPath(\"/html/body/div[1]/div/div/div/div/main/div/div[1]/div[2]/div[1]/span\")).add(\" руб.\"))))").build()).build());
        inlineQueryResults.add(InlineQueryResultArticle.builder().id("6").title("История дня: пересылка старых сообщений из чата").description("script().when(cron(\"0 4 * * *\")).then(message.send(\"История дня:\").andThen(loop(wrap(retry(message(number(random(1409,100000)).asLong()).forward())),6)))").inputMessageContent(InputTextMessageContent.builder().messageText("script().when(cron(\"0 4 * * *\")).then(message.send(\"История дня:\").andThen(loop(wrap(retry(message(number(random(1409,100000)).asLong()).forward())),6)))").build()).build());
        inlineQueryResults.add(InlineQueryResultArticle.builder().id("7").title("Press F to pay respect").description("script().when(text.eqic(\"F\")).then(message.sendStickerFromSet(\"FforRespect\"))").inputMessageContent(InputTextMessageContent.builder().messageText("script().when(text.eqic(\"F\")).then(message.sendStickerFromSet(\"FforRespect\"))").build()).build());
        inlineQueryResults.add(InlineQueryResultArticle.builder().id("8").title("Игра в дартс").description("script().when(message.json.path(\"$.dice.emoji\").eq(\"\uD83C\uDFAF\")).then(message.send(text(message.user).add(\" набрал \").add(message.json.path(\"$.dice.value\")).add(\" очк.\")).andThen(condition(message.json.path(\"$.dice.value\").eq(\"6\")).then(message.reply(request().get(\"https://api.giphy.com/v1/gifs/search?api_key=ZVaHoP3egMTBDEywdwcXetMDYQFQtVxI&q=hat-off&limit=50\").asJson(text(\"$.data[\").add(random(0, 50)).add(\"].bitly_gif_url\")))).otherwise(condition(message.json.path(\"$.dice.value\").eq(\"1\")).then(message.reply(request().get(\"https://api.giphy.com/v1/gifs/search?api_key=ZVaHoP3egMTBDEywdwcXetMDYQFQtVxI&q=you-suck&limit=50\").asJson(text(\"$.data[\").add(random(0, 50)).add(\"].bitly_gif_url\")))))))").inputMessageContent(InputTextMessageContent.builder().messageText("script().when(message.json.path(\"$.dice.emoji\").eq(\"\uD83C\uDFAF\")).then(message.send(text(message.user).add(\" набрал \").add(message.json.path(\"$.dice.value\")).add(\" очк.\")).andThen(condition(message.json.path(\"$.dice.value\").eq(\"6\")).then(message.reply(request().get(\"https://api.giphy.com/v1/gifs/search?api_key=ZVaHoP3egMTBDEywdwcXetMDYQFQtVxI&q=hat-off&limit=50\").asJson(text(\"$.data[\").add(random(0, 50)).add(\"].bitly_gif_url\")))).otherwise(condition(message.json.path(\"$.dice.value\").eq(\"1\")).then(message.reply(request().get(\"https://api.giphy.com/v1/gifs/search?api_key=ZVaHoP3egMTBDEywdwcXetMDYQFQtVxI&q=you-suck&limit=50\").asJson(text(\"$.data[\").add(random(0, 50)).add(\"].bitly_gif_url\")))))))").build()).build());
        inlineQueryResults.add(InlineQueryResultArticle.builder().id("9").title("Fast Food Coub").description("script().when(message.text.cic(\"/coub\").or(message.hasCallback().and(message.callback.data.cic(\"coub_\")))).then(var(\"coub_link\").set(text(\"https://coub.com/view/\").add(request().get(\"https://coub.com/api/v2/timeline/explore/random?order_by=&page=1&per_page=1\").asJson(text(\"$.coubs[0].permalink\")))).andThen(condition(message.hasCallback()).then(condition(message.callback.data.eq(\"coub_like\")).then(message.edit(builder().cleanButtons())).otherwise(message.delete())).andThen(builder(var(\"coub_link\")).button(\"\uD83D\uDC4D\",\"coub_like\").button(\"\uD83D\uDC4E\",\"coub_dislike\").send())))").inputMessageContent(InputTextMessageContent.builder().messageText("script().when(message.text.cic(\"/coub\").or(message.hasCallback().and(message.callback.data.cic(\"coub_\")))).then(var(\"coub_link\").set(text(\"https://coub.com/view/\").add(request().get(\"https://coub.com/api/v2/timeline/explore/random?order_by=&page=1&per_page=1\").asJson(text(\"$.coubs[0].permalink\")))).andThen(condition(message.hasCallback()).then(condition(message.callback.data.eq(\"coub_like\")).then(message.edit(builder().cleanButtons())).otherwise(message.delete())).andThen(builder(var(\"coub_link\")).button(\"\uD83D\uDC4D\",\"coub_like\").button(\"\uD83D\uDC4E\",\"coub_dislike\").send())))").build()).build());
        inlineQueryResults.add(InlineQueryResultArticle.builder().id("10").title("Инфо по сообщению").description("script().when(message.isReply().and(message.text.eq(\"/info\"))).then(message.send(reply.json))").inputMessageContent(InputTextMessageContent.builder().messageText("script().when(message.isReply().and(message.text.eq(\"/info\"))).then(message.send(reply.json))").build()).build());
        try {
          absSender.execute(AnswerInlineQuery.builder().inlineQueryId(update.getId()).results(inlineQueryResults).build());
        } catch (TelegramApiException e) {
          throw new RuntimeException(e);
        }
      } catch (InterruptedException e) {
        LOGGER.error("Interrupt Error occurred during execution main: ", e);
      } catch (Exception e) {
        LOGGER.error("Error occurred during execution main: ", e);
      }
    }
  }
}