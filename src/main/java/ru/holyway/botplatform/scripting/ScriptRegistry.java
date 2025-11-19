package ru.holyway.botplatform.scripting;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * Central repository for all runtime scripts. Manages sorting and lookup logic in a single place
 * to avoid duplication inside processors.
 */
public class ScriptRegistry {

  private final Map<String, List<Script>> scripts = new ConcurrentHashMap<>();

  public void register(@Nonnull String chatId, @Nonnull Script script) {
    scripts.computeIfAbsent(chatId, key -> new ArrayList<>());
    scripts.get(chatId).add(script);
    scripts.get(chatId).sort(Script::compareTo);
  }

  @Nonnull
  public List<Script> getScripts(@Nonnull String chatId) {
    return scripts.getOrDefault(chatId, new ArrayList<>());
  }

  public Optional<Script> findByContent(@Nonnull String chatId, @Nonnull String scriptText) {
    return scripts.getOrDefault(chatId, Collections.emptyList()).stream()
        .filter(script -> scriptText.equals(script.getStringScript()))
        .findFirst();
  }

  public Optional<Script> findByHash(@Nonnull String chatId, int hash) {
    return scripts.getOrDefault(chatId, Collections.emptyList()).stream()
        .filter(script -> script.hashCode() == hash)
        .findFirst();
  }

  public boolean removeByContent(@Nonnull String chatId, @Nonnull String scriptText) {
    Optional<Script> script = findByContent(chatId, scriptText);
    script.ifPresent(Script::cancel);
    return script.isPresent() && scripts.getOrDefault(chatId, Collections.emptyList()).remove(script.get());
  }

  public boolean removeByHash(@Nonnull String chatId, int hash) {
    Optional<Script> script = findByHash(chatId, hash);
    script.ifPresent(Script::cancel);
    return script.isPresent() && scripts.getOrDefault(chatId, Collections.emptyList()).remove(script.get());
  }

  public void clear(@Nonnull String chatId) {
    scripts.getOrDefault(chatId, Collections.emptyList()).forEach(Script::cancel);
    scripts.remove(chatId);
  }

  public void forEach(@Nonnull BiConsumer<String, List<Script>> consumer) {
    scripts.forEach(consumer);
  }
}
