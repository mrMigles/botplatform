package ru.holyway.botplatform.scripting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.classgen.BytecodeSequence;
import ru.holyway.botplatform.scripting.entity.MessageScriptEntity;
import ru.holyway.botplatform.scripting.entity.TextScriptEntity;
import ru.holyway.botplatform.scripting.entity.UserScriptEntity;
import ru.holyway.botplatform.scripting.util.ContextChatStorage;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultShellRules {

  @SuppressWarnings("unchecked")
  public static final List<Class<? extends Statement>> statementsBlacklist =
      Arrays.asList(
          WhileStatement.class, DoWhileStatement.class, ForStatement.class,
          BytecodeSequence.class);

  public static final String[] starImportsWhiteArray =
      new String[]{"java.util", "java.lang", "ru.holyway"};

  public static final List<String> starImportsWhitelist = Arrays.asList(starImportsWhiteArray);

  public static final List<String> importsWhitelist = Arrays.asList(
      java.math.BigDecimal.class.getName(),
      java.math.BigInteger.class.getName(),
      Script.class.getName(),
      MessageScriptEntity.class.getName(),
      ScriptContext.class.getName(),
      TelegramScriptEntity.class.getName(),
      ContextChatStorage.class.getName(),
      TextScriptEntity.class.getName(),
      UserScriptEntity.class.getName());

  public static final List<String> receiversBlackList =
      Collections.singletonList(Thread.class.getName());

  public static List<String> methodsBlacklist = Arrays.asList("getClass", "class", "forName",
      "wait", "notify", "notifyAll", "invokeMethod", "finalize", "sleep", "exit");

}
