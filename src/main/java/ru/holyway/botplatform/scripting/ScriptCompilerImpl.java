package ru.holyway.botplatform.scripting;

import groovy.lang.GroovyShell;
import lombok.RequiredArgsConstructor;

import jakarta.annotation.Nonnull;

@RequiredArgsConstructor
public class ScriptCompilerImpl implements ScriptCompiler {

  @Nonnull
  private final GroovyShell groovyShell;

  @Nonnull
  @Override
  public Script compile(@Nonnull String scriptText) {
    return (Script) groovyShell.parse("return " + scriptText).run();
  }
}
