package ru.holyway.botplatform.scripting;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.*;

public class ScriptRegistryTest {

  @Test
  public void registerShouldKeepScriptsSorted() {
    ScriptRegistry registry = new ScriptRegistry();
    Script slow = Script.script("slow").order(10);
    slow.setStringScript("slow-script");
    Script fast = Script.script("fast").order(1);
    fast.setStringScript("fast-script");

    registry.register("chat", slow);
    registry.register("chat", fast);

    List<Script> scripts = registry.getScripts("chat");

    assertThat(scripts, contains(fast, slow));
  }

  @Test
  public void removeByContentShouldDropScriptAndTriggerCancellation() {
    ScriptRegistry registry = new ScriptRegistry();
    Script script = Script.script("to-remove");
    script.setStringScript("body");

    registry.register("chat", script);

    assertTrue(registry.removeByContent("chat", "body"));
    assertThat(registry.getScripts("chat"), empty());
  }

  @Test
  public void removeByHashShouldWorkForMultipleScripts() {
    ScriptRegistry registry = new ScriptRegistry();
    Script first = Script.script("first");
    first.setStringScript("first-body");
    Script second = Script.script("second");
    second.setStringScript("second-body");

    registry.register("chat", first);
    registry.register("chat", second);

    assertTrue(registry.removeByHash("chat", second.hashCode()));
    assertEquals(1, registry.getScripts("chat").size());
    assertEquals(first, registry.getScripts("chat").get(0));
  }
}
