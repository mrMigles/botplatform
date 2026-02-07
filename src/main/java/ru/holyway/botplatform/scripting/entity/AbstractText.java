package ru.holyway.botplatform.scripting.entity;

import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;
import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.util.NumberOperations;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractText implements Function<ScriptContext, String> {

  public abstract Function<ScriptContext, String> value();

  public static class Text extends AbstractText {

    private Function<ScriptContext, String> value;

    public Text(Function<ScriptContext, String> value) {
      this.value = value;
    }

    @Override
    public Function<ScriptContext, String> value() {
      return value;
    }
  }

  @Override
  public String apply(ScriptContext scriptContext) {
    return value().apply(scriptContext);
  }

  public Predicate<ScriptContext> eq(String text) {
    return ctx -> {
      String value = value().apply(ctx);
      if (value == null) {
        return false;
      }
      return value.equals(text);
    };
  }

  public Predicate<ScriptContext> eq(Function<ScriptContext, String> text) {
    return ctx -> {
      String value = value().apply(ctx);
      if (value == null) {
        return false;
      }
      return value.equals(text.apply(ctx));
    };
  }

  public Predicate<ScriptContext> eqic(String text) {
    return ctx -> {
      String value = value().apply(ctx);
      if (value == null) {
        return false;
      }
      return value.equalsIgnoreCase(text);
    };
  }

  public Predicate<ScriptContext> eqic(Function<ScriptContext, String> text) {
    return ctx -> {
      String value = value().apply(ctx);
      if (value == null) {
        return false;
      }
      return value.equalsIgnoreCase(text.apply(ctx));
    };
  }

  public Predicate<ScriptContext> contains(String text) {
    return ctx -> {
      String value = value().apply(ctx);
      if (value == null) {
        return false;
      }
      return value.contains(text);
    };
  }

  public Predicate<ScriptContext> cic(String text) {
    return ctx -> {
      String value = value().apply(ctx);
      if (value == null) {
        return false;
      }
      return StringUtils.containsIgnoreCase(value, text);
    };
  }

  public Predicate<ScriptContext> cic(Function<ScriptContext, String> text) {
    return ctx -> {
      String value = value().apply(ctx);
      if (value == null) {
        return false;
      }
      return StringUtils.containsIgnoreCase(value, text.apply(ctx));
    };
  }

  public Predicate<ScriptContext> startWith(String text) {
    return ctx -> {
      String value = value().apply(ctx);
      if (value == null) {
        return false;
      }
      return value.startsWith(text);
    };
  }

  public Predicate<ScriptContext> startWith(String text, boolean ignoreCase) {
    return ctx -> {
      String value = value().apply(ctx);
      if (value == null) {
        return false;
      }
      if (ignoreCase) {
        return StringUtils.startsWithIgnoreCase(value, text);
      }
      return value.startsWith(text);
    };
  }

  public Predicate<ScriptContext> matches(String text) {
    return ctx -> {
      String value = value().apply(ctx);
      if (value == null) {
        return false;
      }
      ctx.setContextValue("regexp", text);
      return value.matches(text);
    };
  }

  public NumberOperations asNumber() {
    return new NumberOperations().add(value());
  }

  public AbstractText regexp(final String regexp, final Integer group) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      scriptContext.setContextValue("regexp", regexp);
      Pattern pattern = Pattern.compile(regexp);
      Matcher m = pattern.matcher(value);
      m.find();
      return m.group(group);
    });
  }

  public AbstractText group(final Integer group) {
    return new Text(scriptContext -> {
      String regex = scriptContext.getContextValue("regexp");
      if (regex == null) {
        return null;
      }
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      Pattern pattern = Pattern.compile(regex);
      Matcher m = pattern.matcher(value);
      m.find();
      return m.group(group);
    });
  }

  public AbstractText split(final String deliniter, final Integer group) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      String[] splits = value.split(deliniter);
      if (group < 0 || group >= splits.length) {
        return null;
      }
      return splits[group];
    });
  }

  public AbstractText replace(String from, String to) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      return value.replace(from, to);
    });
  }

  public AbstractText replace(String from, String to, boolean ignoreCase) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      return replaceLiteral(value, from, to, ignoreCase);
    });
  }

  public AbstractText replaceAll(String from, String to) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      return value.replaceAll(from, to);
    });
  }

  public AbstractText replaceAll(String from, String to, boolean ignoreCase) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      return replaceAllRegex(value, from, to, ignoreCase);
    });
  }

  public AbstractText path(final String path) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      Object res = JsonPath.read(value, path);
      return String.valueOf(res);
    });
  }

  public AbstractText trim() {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      return value.trim();
    });
  }

  public AbstractText replace(Function<ScriptContext, String> from, Function<ScriptContext, String> to) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      return value.replace(from.apply(scriptContext), to.apply(scriptContext));
    });
  }

  public AbstractText replace(Function<ScriptContext, String> from, Function<ScriptContext, String> to, boolean ignoreCase) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      return replaceLiteral(value, from.apply(scriptContext), to.apply(scriptContext), ignoreCase);
    });
  }

  public AbstractText replaceAll(Function<ScriptContext, String> from, Function<ScriptContext, String> to) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      return value.replaceAll(from.apply(scriptContext), to.apply(scriptContext));
    });
  }

  public AbstractText replaceAll(Function<ScriptContext, String> from, Function<ScriptContext, String> to, boolean ignoreCase) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      return replaceAllRegex(value, from.apply(scriptContext), to.apply(scriptContext), ignoreCase);
    });
  }

  public AbstractText replace(String from, Function<ScriptContext, String> to) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      return value.replace(from, to.apply(scriptContext));
    });
  }

  public AbstractText replace(String from, Function<ScriptContext, String> to, boolean ignoreCase) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      return replaceLiteral(value, from, to.apply(scriptContext), ignoreCase);
    });
  }

  public AbstractText replaceAll(String from, Function<ScriptContext, String> to) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      return value.replaceAll(from, to.apply(scriptContext));
    });
  }

  public AbstractText replaceAll(String from, Function<ScriptContext, String> to, boolean ignoreCase) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      return replaceAllRegex(value, from, to.apply(scriptContext), ignoreCase);
    });
  }

  public AbstractText replace(Function<ScriptContext, String> from, String to) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      return value.replace(from.apply(scriptContext), to);
    });
  }

  public AbstractText replace(Function<ScriptContext, String> from, String to, boolean ignoreCase) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      return replaceLiteral(value, from.apply(scriptContext), to, ignoreCase);
    });
  }

  public AbstractText replaceAll(Function<ScriptContext, String> from, String to) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      return value.replaceAll(from.apply(scriptContext), to);
    });
  }

  public AbstractText replaceAll(Function<ScriptContext, String> from, String to, boolean ignoreCase) {
    return new Text(scriptContext -> {
      String value = value().apply(scriptContext);
      if (value == null) {
        return null;
      }
      return replaceAllRegex(value, from.apply(scriptContext), to, ignoreCase);
    });
  }

  private static String replaceLiteral(String value, String from, String to, boolean ignoreCase) {
    if (!ignoreCase) {
      return value.replace(from, to);
    }
    Pattern pattern = Pattern.compile(Pattern.quote(from), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    Matcher matcher = pattern.matcher(value);
    return matcher.replaceAll(Matcher.quoteReplacement(to));
  }

  private static String replaceAllRegex(String value, String from, String to, boolean ignoreCase) {
    if (!ignoreCase) {
      return value.replaceAll(from, to);
    }
    Pattern pattern = Pattern.compile(from, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    Matcher matcher = pattern.matcher(value);
    return matcher.replaceAll(to);
  }
}
