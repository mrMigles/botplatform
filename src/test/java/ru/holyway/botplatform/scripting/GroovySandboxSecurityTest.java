package ru.holyway.botplatform.scripting;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import ru.holyway.botplatform.core.data.MemoryDataHelper;
import ru.holyway.botplatform.scripting.entity.ArrayEntity;
import ru.holyway.botplatform.scripting.entity.ChatTelegramEntity;
import ru.holyway.botplatform.scripting.entity.ConditionHandler;
import ru.holyway.botplatform.scripting.entity.HistoryMessageEntity;
import ru.holyway.botplatform.scripting.entity.InTimePredicate;
import ru.holyway.botplatform.scripting.entity.LoopHandler;
import ru.holyway.botplatform.scripting.entity.MessageBuilder;
import ru.holyway.botplatform.scripting.entity.MessageScriptEntity;
import ru.holyway.botplatform.scripting.entity.ReplyScriptEntity;
import ru.holyway.botplatform.scripting.entity.StickerEntity;
import ru.holyway.botplatform.scripting.entity.TernaryHandler;
import ru.holyway.botplatform.scripting.entity.TextScriptEntity;
import ru.holyway.botplatform.scripting.entity.TimePredicate;
import ru.holyway.botplatform.scripting.entity.UserScriptEntity;
import ru.holyway.botplatform.scripting.entity.VariableEntity;
import ru.holyway.botplatform.scripting.util.ContextChatStorage;
import ru.holyway.botplatform.scripting.util.NumberOperations;
import ru.holyway.botplatform.scripting.util.Request;
import ru.holyway.botplatform.scripting.util.SecretChatStorage;
import ru.holyway.botplatform.scripting.util.TextJoiner;
import ru.holyway.botplatform.scripting.util.Time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Exercises the real sandboxed GroovyShell built the same way as
 * {@link ru.holyway.botplatform.config.GroovyConfiguration}, instead of a plain GroovyShell,
 * so the SecureASTCustomizer/blacklist rules are actually under test.
 */
public class GroovySandboxSecurityTest {

  private ScriptCompiler compiler;

  @BeforeEach
  public void setUp() {
    List<String> allowedImports = new ArrayList<>(DefaultShellRules.importsWhitelist);
    CompilerConfiguration configuration = new CompilerConfiguration()
        .addCompilationCustomizers(customAst(allowedImports), customImports(allowedImports));

    Map<String, Object> mapping = new HashMap<>();
    mapping.put("message", new MessageScriptEntity());
    mapping.put("reply", new ReplyScriptEntity());
    mapping.put("map", new ContextChatStorage(new MemoryDataHelper()));
    mapping.put("secret", new SecretChatStorage(new MemoryDataHelper()));
    mapping.put("text", new TextScriptEntity());
    mapping.put("user", new UserScriptEntity());
    mapping.put("sticker", new StickerEntity());
    mapping.put("request", new Request());
    mapping.put("chat", new ChatTelegramEntity(null));

    Binding binding = new Binding(mapping);
    GroovyShell shell = new GroovyShell(binding, configuration);
    compiler = new ScriptCompilerImpl(shell);
  }

  private ImportCustomizer customImports(List<String> allowedImports) {
    return new ImportCustomizer()
        .addStaticStars(
            Script.class.getName(), Time.class.getName(), TextJoiner.class.getName(),
            Request.class.getName(), ArrayEntity.class.getName(), ConditionHandler.class.getName(),
            TernaryHandler.class.getName(), ChatTelegramEntity.class.getName(),
            TimePredicate.class.getName(), InTimePredicate.class.getName(),
            HistoryMessageEntity.class.getName(), LoopHandler.class.getName(),
            MessageBuilder.class.getName(), VariableEntity.class.getName(),
            NumberOperations.class.getName(), ParseMode.class.getName())
        .addImports(allowedImports.toArray(new String[0]))
        .addStarImports(DefaultShellRules.starImportsWhiteArray);
  }

  private SecureASTCustomizer customAst(List<String> allowedImports) {
    SecureASTCustomizer secureAstCustomizer = new SecureASTCustomizer();
    secureAstCustomizer.setClosuresAllowed(false);
    secureAstCustomizer.setMethodDefinitionAllowed(false);
    secureAstCustomizer.setPackageAllowed(false);
    secureAstCustomizer.setIndirectImportCheckEnabled(true);
    secureAstCustomizer.setImportsWhitelist(allowedImports);
    secureAstCustomizer.setStarImportsWhitelist(DefaultShellRules.starImportsWhitelist);
    secureAstCustomizer.setStatementsBlacklist(DefaultShellRules.statementsBlacklist);
    secureAstCustomizer.setReceiversBlackList(DefaultShellRules.receiversBlackList);
    secureAstCustomizer.addExpressionCheckers(
        new MethodsBlacklistExpressionChecker(DefaultShellRules.methodsBlacklist));
    return secureAstCustomizer;
  }

  @Test
  public void legitimateScriptStillCompiles() {
    Script script = compiler.compile(
        "script().when(text.cic(\"hello\")).then(message.reply(\"hi\"))");

    assertNotNull(script);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "System.exit(1)",
      "Runtime.getRuntime().exec('id')",
      "Runtime.getRuntime()",
      "new ProcessBuilder('id').start()",
      "Thread.sleep(1000)",
      "new Thread(){}.start()",
      "this.getClass().forName('java.lang.Runtime')",
      "message.getClass()",
      "message.getClass().forName('java.lang.Runtime').getMethod('exec', String.class)",
      "Class.forName('java.lang.Runtime')",
      "this.getClass().getClassLoader()",
      "import java.io.File; new File('/').listFiles()",
      "import ru.holyway.botplatform.core.entity.JSettings; new JSettings()",
      "import ru.holyway.botplatform.security.AnonymousChatTokenSecurityFilter",
      "for (int i = 0; i < 10; i++) { }",
      "while (true) { }",
  })
  public void maliciousOrDisallowedScriptIsRejected(String maliciousScript) {
    assertThrows(Exception.class, () -> compiler.compile(maliciousScript));
  }
}
