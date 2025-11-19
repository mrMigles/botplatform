package ru.holyway.botplatform.scripting;

import javax.annotation.Nonnull;

/**
 * Wraps compilation failures with additional context about the script text.
 */
public class ScriptCompilationException extends RuntimeException {

  private final String scriptText;

  public ScriptCompilationException(@Nonnull String message, @Nonnull String scriptText,
                                    @Nonnull Throwable cause) {
    super(message, cause);
    this.scriptText = scriptText;
  }

  @Nonnull
  public String getScriptText() {
    return scriptText;
  }
}
