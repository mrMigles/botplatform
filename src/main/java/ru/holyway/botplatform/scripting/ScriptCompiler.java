package ru.holyway.botplatform.scripting;

import jakarta.annotation.Nonnull;

public interface ScriptCompiler {

  @Nonnull
  Script compile(@Nonnull String scriptText);

}
