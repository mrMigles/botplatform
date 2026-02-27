package ru.holyway.botplatform.scripting;

import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("null")
public class MethodsBlacklistExpressionCheckerTest {

  @Test
  public void shouldRejectBlacklistedMethod() {
    MethodsBlacklistExpressionChecker checker =
        new MethodsBlacklistExpressionChecker(List.of("getClass", "exit"));
    MethodCallExpression expression = new MethodCallExpression(
        new ConstantExpression("obj"), "getClass", new ArgumentListExpression());

    assertFalse(checker.isAuthorized(expression));
  }

  @Test
  public void shouldRejectGStringMethodName() {
    MethodsBlacklistExpressionChecker checker =
        new MethodsBlacklistExpressionChecker(List.of("smth"));
    MethodCallExpression expression = new MethodCallExpression(
        new ConstantExpression("obj"), (String) null, new ArgumentListExpression());
    expression.setMethod(new GStringExpression("${dynamic}"));

    assertFalse(checker.isAuthorized(expression));
  }

  @Test
  public void shouldAllowNonBlacklistedMethod() {
    MethodsBlacklistExpressionChecker checker =
        new MethodsBlacklistExpressionChecker(List.of("getClass"));
    MethodCallExpression expression = new MethodCallExpression(
        new ConstantExpression("obj"), "otherMethod", new ArgumentListExpression());

    assertTrue(checker.isAuthorized(expression));
  }
}
