package ru.holyway.botplatform.config;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.scripting.*;
import ru.holyway.botplatform.scripting.entity.*;
import ru.holyway.botplatform.scripting.util.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class GroovyConfiguration {

  @Bean
  public ScriptCompiler scriptCompiler(final DataHelper dataHelper) {
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
    mapping.put("map", new ContextChatStorage(dataHelper));
    mapping.put("text", new TextScriptEntity());
    mapping.put("user", new UserScriptEntity());
    mapping.put("sticker", new StickerEntity());
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
            ArrayEntity.class.getName(),
            ConditionHandler.class.getName(),
            TernaryHandler.class.getName(),
            ChatTelegramEntity.class.getName(),
            TimePredicate.class.getName(),
            InTimePredicate.class.getName(),
            HistoryMessageEntity.class.getName(),
            LoopHandler.class.getName(),
            MessageBuilder.class.getName(),
            VariableEntity.class.getName(),
            NumberOperations.class.getName(),
            ParseMode.class.getName())
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
