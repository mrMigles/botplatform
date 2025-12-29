package ru.holyway.botplatform.scripting;

import groovy.lang.GroovyShell;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ScriptCompilerImplTest {

  @Test
  public void shouldCompileGroovyScript() {
    GroovyShell shell = new GroovyShell();
    ScriptCompilerImpl compiler = new ScriptCompilerImpl(shell);

    Script script = compiler.compile("ru.holyway.botplatform.scripting.Script.script('compiled')");

    assertNotNull(script);
    assertEquals("compiled", script.getName());
  }
}
