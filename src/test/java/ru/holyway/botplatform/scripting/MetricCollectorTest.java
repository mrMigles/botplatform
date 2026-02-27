package ru.holyway.botplatform.scripting;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetricCollectorTest {

  @Test
  public void shouldSaveLogWithFullCause() {
    MetricCollector collector = MetricCollector.getInstance();
    Script script = Script.script("logTest");
    script.setStringScript("Script.script('logTest').owner(1)");

    RuntimeException inner = new RuntimeException("inner");
    RuntimeException outer = new RuntimeException("outer", inner);

    collector.saveLog("chat1", script, outer);
    String log = collector.getLog("chat1");

    assertThat(log, containsString("logTest"));
    assertThat(log, containsString("outer"));
    assertThat(log, containsString("inner"));
  }

  @Test
  public void shouldCollectExecutionStatistics() {
    MetricCollector collector = MetricCollector.getInstance();
    collector.trackCall("chat2", 10);
    collector.trackCall("chat2", 30);

    String info = collector.getExecutionInfo();

    assertThat(info, containsString("chat2"));
    assertThat(info, containsString("count: 2"));
    assertThat(info, containsString("max: 30"));
    assertTrue(info.contains("avg: 20"));
  }
}
