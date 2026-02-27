package ru.holyway.botplatform.scripting;

import org.junit.jupiter.api.Test;
import ru.holyway.botplatform.scripting.entity.ArrayEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayEntityTest {

  @Test
  public void shouldIterateAllItems() {
    ArrayEntity array = ArrayEntity.array("a,b,c", ",");
    List<String> collected = new ArrayList<>();

    array.forEach(ctx -> collected.add(ctx.getContextValue("array.item")))
        .accept(new ScriptContext());

    assertEquals(Arrays.asList("a", "b", "c"), collected);
  }

  @Test
  public void shouldIterateFromOffset() {
    ArrayEntity array = ArrayEntity.array("a,b,c,d", ",");
    List<String> collected = new ArrayList<>();

    array.forEachFrom(ctx -> collected.add(ctx.getContextValue("array.item")), 2)
        .accept(new ScriptContext());

    assertEquals(Arrays.asList("c", "d"), collected);
  }

  @Test
  public void shouldIterateLastNItems() {
    ArrayEntity array = ArrayEntity.array("a,b,c,d,e", ",");
    List<String> collected = new ArrayList<>();

    array.forEachLast(ctx -> collected.add(ctx.getContextValue("array.item")), 2)
        .accept(new ScriptContext());

    assertEquals(Arrays.asList("d", "e"), collected);
  }

  @Test
  public void shouldIterateFromFunctionOffset() {
    ArrayEntity array = ArrayEntity.array("a,b,c,d", ",");
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("offset", "1");
    List<String> collected = new ArrayList<>();

    array.forEachFrom(c -> collected.add(c.getContextValue("array.item")),
        c -> Integer.parseInt(c.getContextValue("offset"))).accept(ctx);

    assertEquals(Arrays.asList("b", "c", "d"), collected);
  }

  @Test
  public void shouldIterateLastNWithFunctionCount() {
    ArrayEntity array = ArrayEntity.array("a,b,c,d,e", ",");
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("last", "3");
    List<String> collected = new ArrayList<>();

    array.forEachLast(c -> collected.add(c.getContextValue("array.item")),
        c -> Integer.parseInt(c.getContextValue("last"))).accept(ctx);

    assertEquals(Arrays.asList("c", "d", "e"), collected);
  }

  @Test
  public void shouldReturnCorrectSize() {
    ArrayEntity array = ArrayEntity.array("x,y,z", ",");

    Number size = array.size().value().apply(new ScriptContext());

    assertEquals(3, size.intValue());
  }

  @Test
  public void shouldReturnItemFromContext() {
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("array.item", "hello");

    assertEquals("hello", ArrayEntity.item().apply(ctx));
  }

  @Test
  public void shouldStoreFullArrayInContext() {
    ArrayEntity array = ArrayEntity.array("x,y,z", ",");
    ScriptContext ctx = new ScriptContext();

    array.forEach(c -> {}).accept(ctx);

    assertEquals("x&_&y&_&z", ctx.getContextValue("array"));
  }

  @Test
  public void shouldCreateArrayFromContextFunction() {
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("data", "1-2-3");

    ArrayEntity array = ArrayEntity.array(c -> c.getContextValue("data"), "-");
    List<String> collected = new ArrayList<>();

    array.forEach(c -> collected.add(c.getContextValue("array.item"))).accept(ctx);

    assertEquals(Arrays.asList("1", "2", "3"), collected);
  }

  @Test
  public void shouldRestoreArrayFromContext() {
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("array", "a&_&b&_&c");

    ArrayEntity array = ArrayEntity.array();
    List<String> collected = new ArrayList<>();

    array.forEach(c -> collected.add(c.getContextValue("array.item"))).accept(ctx);

    assertEquals(Arrays.asList("a", "b", "c"), collected);
  }

  @Test
  public void shouldCapForEachFromAt100ItemsMax() {
    StringBuilder csv = new StringBuilder();
    for (int i = 0; i < 150; i++) {
      if (i > 0) csv.append(",");
      csv.append(i);
    }
    ArrayEntity array = ArrayEntity.array(csv.toString(), ",");
    List<String> collected = new ArrayList<>();

    array.forEachFrom(ctx -> collected.add(ctx.getContextValue("array.item")), 0)
        .accept(new ScriptContext());

    assertEquals(100, collected.size());
  }
}
