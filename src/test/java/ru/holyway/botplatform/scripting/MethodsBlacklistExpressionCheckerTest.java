package ru.holyway.botplatform.scripting;

import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MethodsBlacklistExpressionCheckerTest {

  @Test
  public void shouldRejectBlacklistedMethod() {
    MethodsBlacklistExpressionChecker checker =
        new MethodsBlacklistExpressionChecker(Arrays.asList("getClass", "exit"));
    MethodCallExpression expression = new MethodCallExpression(
        new ConstantExpression("obj"), "getClass", new ArgumentListExpression());

    assertFalse(checker.isAuthorized(expression));
  }

  @Test
  public void shouldRejectGStringMethodName() {
    MethodsBlacklistExpressionChecker checker =
        new MethodsBlacklistExpressionChecker(Arrays.asList("smth"));
    MethodCallExpression expression = new MethodCallExpression(
        new ConstantExpression("obj"), (String) null, new ArgumentListExpression());
    expression.setMethod(new GStringExpression("${dynamic}"));

    assertFalse(checker.isAuthorized(expression));
  }

  @Test
  public void shouldAllowNonBlacklistedMethod() {
    MethodsBlacklistExpressionChecker checker =
        new MethodsBlacklistExpressionChecker(Arrays.asList("getClass"));
    MethodCallExpression expression = new MethodCallExpression(
        new ConstantExpression("obj"), "otherMethod", new ArgumentListExpression());

    assertTrue(checker.isAuthorized(expression));
  }
}
