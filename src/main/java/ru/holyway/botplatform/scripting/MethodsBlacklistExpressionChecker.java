package ru.holyway.botplatform.scripting;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;


public class MethodsBlacklistExpressionChecker implements SecureASTCustomizer.ExpressionChecker {

  private final Set<String> methodsBlacklist;

  public MethodsBlacklistExpressionChecker(@Nonnull Collection<String> methodsBlacklist) {
    this.methodsBlacklist = new HashSet<>(methodsBlacklist);
  }

  @Override
  public boolean isAuthorized(@Nonnull Expression expression) {
    if (expression instanceof MethodCallExpression) {
      MethodCallExpression mce = (MethodCallExpression) expression;
      String methodName = mce.getMethodAsString();
      if (methodsBlacklist.contains(methodName)) {
        return false;
      } else if (methodName == null && mce.getMethod() instanceof GStringExpression) {
        return false;
      }
    }
    return true;
  }
}
