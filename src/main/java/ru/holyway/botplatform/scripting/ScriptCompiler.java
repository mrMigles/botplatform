package ru.holyway.botplatform.scripting;

import javax.annotation.Nonnull;

public interface ScriptCompiler {

  @Nonnull
  Script compile(@Nonnull String scriptText);

}
