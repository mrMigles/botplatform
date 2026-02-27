package ru.holyway.botplatform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.text.ParseException;

@ExtendWith(SpringExtension.class)
public class BotPlatformApplicationTests {

  //private ScriptCompiler scriptCompiler = new GroovyConfiguration().scriptCompiler();

  @Test
  public void contextLoads() throws IOException, ParseException {
//    //Script script = scriptCompiler.compile(
//      //  "script().when(any()).then(sout(get(\"https://jsonplaceholder.typicode.com/todos/1\").asJson(\"title\")))");
//
    //ScriptContext ctx = new ScriptContext();
//
//    Script script = script().when(any()).then(sout(get(text("https://yandex.ru/search/?text=").add(encode("что такое человек?")).value()).asHtml("<span class=\"text-cut2 typo typo_text_m typo_line_m\">", "<a class=\"link")));
//
//    if (script.check(ctx)) {
//      script.execute(ctx);
//    }
    //script().when(ctx.message.text.eqic(new String("стопаньки"))).then(sout(new TextJoiner(){}.value()));

  }

  @Test
  public void name() {
//    System.getenv("BOT_TOKEN");
//    String a = Arrays.stream("1601668040877:5038&+&ну+&&+-&&фиксед".split(":")).filter(new Predicate<String>() {public boolean test(String s) {System.exit(0);return true;}}).findFirst().toString();
//    System.out.println(a.replace("&+", " "));
//
//    System.out.println(new RestTemplate().exchange("https://ssyoutube.com/api/ig/story?url=https://instagram.com/stories/avtoinstruktorsaratov/3027039242002599367?utm_source=ig_story_item_share&igshid=MDJmNzVkMjY=", HttpMethod.GET, null, String.class).getBody());

  }
}
