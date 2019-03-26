package ru.holyway.botplatform.config;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.holyway.botplatform.scripting.DefaultShellRules;
import ru.holyway.botplatform.scripting.MethodsBlacklistExpressionChecker;
import ru.holyway.botplatform.scripting.Script;
import ru.holyway.botplatform.scripting.ScriptCompiler;
import ru.holyway.botplatform.scripting.ScriptCompilerImpl;
import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.TelegramScriptEntity;
import ru.holyway.botplatform.scripting.entity.ChatTelegramEntity;
import ru.holyway.botplatform.scripting.entity.ForwardScriptEntity;
import ru.holyway.botplatform.scripting.entity.MessageScriptEntity;
import ru.holyway.botplatform.scripting.entity.ReplyScriptEntity;
import ru.holyway.botplatform.scripting.entity.TextScriptEntity;
import ru.holyway.botplatform.scripting.entity.UserScriptEntity;
import ru.holyway.botplatform.scripting.util.ContextChatStorage;
import ru.holyway.botplatform.scripting.util.NumberOperations;
import ru.holyway.botplatform.scripting.util.Request;
import ru.holyway.botplatform.scripting.util.TextJoiner;
import ru.holyway.botplatform.scripting.util.Time;

@Configuration
public class GroovyConfiguration {

  @Bean
  public ScriptCompiler scriptCompiler() {
    List<String> allowedImports = new ArrayList<>(DefaultShellRules.importsWhitelist);
    CompilerConfiguration configuration = new CompilerConfiguration()
        .addCompilationCustomizers(customAst(allowedImports),
            customImports(allowedImports.toArray(new String[0])));

    Map<String, Object> mapping = new HashMap<>();
    mapping.put("message", new MessageScriptEntity());
    mapping.put("forward", new ForwardScriptEntity());
    mapping.put("reply", new ReplyScriptEntity());
    mapping.put("ctx", new ScriptContext());
    mapping.put("telegram", new TelegramScriptEntity());
    mapping.put("map", new ContextChatStorage());
    mapping.put("text", new TextScriptEntity());
    mapping.put("user", new UserScriptEntity());
    mapping.put("request", new Request());
    mapping.put("chat", new ChatTelegramEntity(null));

    Binding binding = new Binding(mapping);
    GroovyShell groovyShell = new GroovyShell(binding, configuration);
    return new ScriptCompilerImpl(groovyShell);
  }

  @Nonnull
  private ImportCustomizer customImports(@Nonnull String[] allowedImports) {
    return new ImportCustomizer()
        .addStaticStars(
            Script.class.getName(),
            Time.class.getName(),
            TextJoiner.class.getName(),
            Request.class.getName(),
            ChatTelegramEntity.class.getName(),
            NumberOperations.class.getName())
        .addImports(allowedImports)
        .addStarImports(DefaultShellRules.starImportsWhiteArray);
  }

  @Nonnull
  private SecureASTCustomizer customAst(@Nonnull List<String> allowedImports) {
    SecureASTCustomizer secureAstCustomizer = new SecureASTCustomizer();

    secureAstCustomizer.setClosuresAllowed(false);
    secureAstCustomizer.setMethodDefinitionAllowed(false);

    secureAstCustomizer.setPackageAllowed(false);

    secureAstCustomizer.setIndirectImportCheckEnabled(true);
    secureAstCustomizer.setImportsWhitelist(allowedImports);
    secureAstCustomizer.setStarImportsWhitelist(DefaultShellRules.starImportsWhitelist);
    secureAstCustomizer.setStatementsBlacklist(DefaultShellRules.statementsBlacklist);
    secureAstCustomizer.setReceiversBlackList(DefaultShellRules.receiversBlackList);

    SecureASTCustomizer.ExpressionChecker expressionChecker =
        new MethodsBlacklistExpressionChecker(DefaultShellRules.methodsBlacklist);
    secureAstCustomizer.addExpressionCheckers(expressionChecker);
    return secureAstCustomizer;
  }
}
