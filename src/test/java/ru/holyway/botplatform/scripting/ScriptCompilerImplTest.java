package ru.holyway.botplatform.scripting;

import groovy.lang.GroovyShell;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ScriptCompilerImplTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

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
    expectedException.expect(ScriptCompilationException.class);
    expectedException.expectMessage(is("Unable to compile script"));
    expectedException.expectMessage(containsString("badMethod"));

    compiler.compile("badMethod(");
  }
}
