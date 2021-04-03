package ru.holyway.botplatform;

import static ru.holyway.botplatform.scripting.Script.*;
import static ru.holyway.botplatform.scripting.util.Request.*;
import static ru.holyway.botplatform.scripting.util.TextJoiner.text;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import ru.holyway.botplatform.config.GroovyConfiguration;
import ru.holyway.botplatform.scripting.Script;
import ru.holyway.botplatform.scripting.ScriptCompiler;
import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.util.TextJoiner;

@RunWith(SpringRunner.class)
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
  }
}
