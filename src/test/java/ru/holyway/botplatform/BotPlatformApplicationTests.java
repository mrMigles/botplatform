package ru.holyway.botplatform;

import static ru.holyway.botplatform.scripting.Script.*;
import static ru.holyway.botplatform.scripting.util.Request.*;
import static ru.holyway.botplatform.scripting.util.TextJoiner.text;

import java.io.IOException;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import ru.holyway.botplatform.config.GroovyConfiguration;
import ru.holyway.botplatform.scripting.Script;
import ru.holyway.botplatform.scripting.ScriptCompiler;
import ru.holyway.botplatform.scripting.ScriptContext;

@RunWith(SpringRunner.class)
public class BotPlatformApplicationTests {

  private ScriptCompiler scriptCompiler = new GroovyConfiguration().scriptCompiler();

  @Test
  public void contextLoads() throws IOException, ParseException {
//    //Script script = scriptCompiler.compile(
//      //  "script().when(any()).then(sout(get(\"https://jsonplaceholder.typicode.com/todos/1\").asJson(\"title\")))");
//
//    ScriptContext ctx = new ScriptContext();
//
//    Script script = script().when(any()).then(sout(get(text("https://yandex.ru/search/?text=").add(encode("что такое человек?")).value()).asHtml("<span class=\"text-cut2 typo typo_text_m typo_line_m\">", "<a class=\"link")));
//
//    if (script.check(ctx)) {
//      script.execute(ctx);
//    }

  }

  @Test
  public void name() {
//    final String regex = "^(.|\\s)*(\"title\": )(.*)(,)(.|\\s)*$";
//    final String string = "{\n"
//        + "  \"userId\": 1,\n"
//        + "  \"id\": 1,\n"
//        + "  \"title\": \"delectus aut autem\",\n"
//        + "  \"completed\": false\n"
//        + "}";
//
//    final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
//    final Matcher matcher = pattern.matcher(string);
//
//
//    System.out.println(matcher.group(3));
  }
}
