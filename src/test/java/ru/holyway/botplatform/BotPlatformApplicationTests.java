package ru.holyway.botplatform;

import java.io.IOException;
import java.text.ParseException;
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
    Script script = scriptCompiler.compile(
        "script().when(ctx.entity.text.eq(\"123\").and(ctx.entity.user.eq(\"mrMigles\")))\n"
            + "        .then(ctx.entity.send(\"text\").andThen(ctx.entity.reply(ctx.entity.text.get())))");

    ScriptContext ctx = new ScriptContext();

//    Script script = script().when(ctx.entity.text.eq("123").and(ctx.entity.user.eq("mrMigles")))
//        .then(ctx.entity.send("text").andThen(ctx.entity.reply(ctx.entity.text.get())));

    if (script.check(ctx)) {
      script.execute(ctx);
    }

  }
}
