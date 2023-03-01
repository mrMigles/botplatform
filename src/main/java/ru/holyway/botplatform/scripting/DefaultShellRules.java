package ru.holyway.botplatform.scripting;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.classgen.BytecodeSequence;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import ru.holyway.botplatform.scripting.entity.*;
import ru.holyway.botplatform.scripting.util.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultShellRules {

  @SuppressWarnings("unchecked")
  public static final List<Class<? extends Statement>> statementsBlacklist =
      Arrays.asList(
          WhileStatement.class, DoWhileStatement.class, ForStatement.class,
          BytecodeSequence.class);

  public static final String[] starImportsWhiteArray =
      new String[]{"ru.holyway"};

  public static final List<String> starImportsWhitelist = Arrays.asList(starImportsWhiteArray);

  public static final List<String> importsWhitelist = Arrays.asList(
      java.math.BigDecimal.class.getName(),
      java.math.BigInteger.class.getName(),
      String.class.getName(),
      Long.class.getName(),
      Double.class.getName(),
      Number.class.getName(),
      Object.class.getName(),
      Boolean.class.getName(),
      Byte.class.getName(),
      Enum.class.getName(),
      Float.class.getName(),
      Integer.class.getName(),
      Math.class.getName(),
      UUID.class.getName(),
      Script.class.getName(),
      MessageScriptEntity.class.getName(),
      ScriptContext.class.getName(),
      TelegramScriptEntity.class.getName(),
      ContextChatStorage.class.getName(),
      TextScriptEntity.class.getName(),
      UserScriptEntity.class.getName(),
      StickerEntity.class.getName(),
      TextJoiner.class.getName(),
      Time.class.getName(),
      Request.class.getName(),
      ArrayEntity.class.getName(),
      ConditionHandler.class.getName(),
      TernaryHandler.class.getName(),
      ChatTelegramEntity.class.getName(),
      TimePredicate.class.getName(),
      InTimePredicate.class.getName(),
      HistoryMessageEntity.class.getName(),
      LoopHandler.class.getName(),
      VariableEntity.class.getName(),
      MessageBuilder.class.getName(),
      NumberOperations.class.getName(),
      ParseMode.class.getName());

  public static final List<String> receiversBlackList =
      Collections.singletonList(Thread.class.getName());

  public static List<String> methodsBlacklist = Arrays.asList("getClass", "class", "forName",
      "wait", "notify", "notifyAll", "invokeMethod", "finalize", "sleep", "exit");

}
