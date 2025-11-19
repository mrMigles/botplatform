package ru.holyway.botplatform.scripting;

import groovy.lang.GroovyShell;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
@Slf4j
public class ScriptCompilerImpl implements ScriptCompiler {

  @Nonnull
  private final GroovyShell groovyShell;

  @Nonnull
  @Override
  public Script compile(@Nonnull String scriptText) {
    try {
      return (Script) groovyShell.parse("return " + scriptText).run();
    } catch (Exception ex) {
      log.error("Failed to compile script: {}", scriptText, ex);
      throw new ScriptCompilationException("Unable to compile script", scriptText, ex);
    }
  }
}
