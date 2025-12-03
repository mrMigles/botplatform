package ru.holyway.botplatform.scripting;

import groovy.lang.GroovyShell;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ScriptCompilerImplTest {

  private final ScriptCompilerImpl compiler = new ScriptCompilerImpl(new GroovyShell());

  @Test
  public void compileShouldReturnConfiguredScript() {
    Script script = compiler.compile("ru.holyway.botplatform.scripting.Script.script('test')" +
        ".when(ru.holyway.botplatform.scripting.Script.any())" +
        ".then(ru.holyway.botplatform.scripting.Script.nothing())");

    assertNotNull(script);
    assertEquals("test", script.getName());
    assertNotNull(script.getPredicates());
    assertNotNull(script);
  }

  @Test
  public void compileShouldWrapGroovyErrors() {
    try {
      compiler.compile("badMethod(");
      fail("Script compilation failure should be wrapped");
    } catch (ScriptCompilationException ex) {
      assertEquals("Unable to compile script", ex.getMessage());
      assertNotNull(ex.getCause());
      assertThat(ex.getCause().getMessage(), containsString("badMethod"));
    }
  }
}
