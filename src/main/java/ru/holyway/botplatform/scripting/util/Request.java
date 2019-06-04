package ru.holyway.botplatform.scripting.util;

import com.jayway.jsonpath.JsonPath;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import ru.holyway.botplatform.config.RequestFactory.HttpComponentsClientHttpRequestWithBodyFactory;
import ru.holyway.botplatform.scripting.ScriptContext;

public class Request {

  private static RestTemplate restTemplate = new RestTemplate();

  static {
    restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestWithBodyFactory());
  }

  private Map<String, Object> params = new HashMap<>();

  private boolean isLast = false;

  private Object body = "";

  private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

  private Object url = "";

  private HttpMethod method;

  public static Function<ScriptContext, String> encode(Object o) {
    return scriptContext -> {
      String url;
      if (o instanceof Function) {
        url = ((Function<ScriptContext, String>) o).apply(scriptContext);
      } else {
        url = String.valueOf(o);
      }
      try {
        return URLEncoder.encode(url, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        System.out.println(e);
        return url;
      }
    };
  }

  public static Function<ScriptContext, String> decode(Object o) {
    return scriptContext -> {
      String text;
      if (o instanceof Function) {
        text = ((Function<ScriptContext, String>) o).apply(scriptContext);
      } else {
        text = String.valueOf(o);
      }
      try {
        return URLDecoder.decode(text, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        System.out.println(e);
        return text;
      }
    };
  }

  public Request param(String key, Object value) {
    this.params.put(key, value);
    return this;
  }

  public Request body(Object body) {
    this.body = body;
    return this;
  }

  public Request header(String key, String value) {
    this.headers.add(key, value);
    return this;
  }

  public Request url(Object url) {
    this.url = url;
    return this;
  }

  private Request setMethod(HttpMethod method) {
    this.method = method;
    return this;
  }

  public TextJoiner asJson(Object jsonPath) {
    return TextJoiner.text(scriptContext -> {
      final String url = this.url instanceof Function ? ((Function<ScriptContext, String>) this.url)
          .apply(scriptContext) : String.valueOf(this.url);
      final String body =
          this.body instanceof Function ? ((Function<ScriptContext, String>) this.body)
              .apply(scriptContext) : String.valueOf(this.body);

      MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
      for (Map.Entry<String, Object> param : this.params.entrySet()) {
        if (param.getValue() instanceof Function) {
          params.add(param.getKey(), ((Function<ScriptContext, String>) param.getValue())
              .apply(scriptContext));
        } else {
          params.add(param.getKey(), String.valueOf(param.getValue()));
        }
      }

      HttpEntity httpEntity;

      if (!this.params.isEmpty()) {
        headers.add("Content-Type", "application/x-www-form-urlencoded");
      }
      if (StringUtils.isEmpty(body)) {
        httpEntity = new HttpEntity(params, headers);
      } else {
        httpEntity = new HttpEntity<>(body, headers);
      }

      final String response;

      if (isLast && scriptContext.getContextValue("request") != null) {
        response = scriptContext.getContextValue("request");
      } else {
        response = restTemplate
            .exchange(url, method, httpEntity, String.class).getBody();
        scriptContext.setContextValue("request", response);
      }

      String stringPath = jsonPath instanceof String ? String.valueOf(jsonPath)
          : ((Function<ScriptContext, String>) jsonPath).apply(scriptContext);
      Object res = JsonPath.read(response, stringPath);
      return String.valueOf(res);
    });
  }

  public TextJoiner asHtml(String startTag, String endTag) {
    return TextJoiner.text(scriptContext -> {
      RestTemplate restTemplate = new RestTemplate();
      final String url = this.url instanceof Function ? ((Function<ScriptContext, String>) this.url)
          .apply(scriptContext) : String.valueOf(this.url);
      final String body =
          this.body instanceof Function ? ((Function<ScriptContext, String>) this.body)
              .apply(scriptContext) : String.valueOf(this.body);

      Map<String, String> params = new HashMap<>();
      for (Map.Entry<String, Object> param : this.params.entrySet()) {
        if (param.getValue() instanceof Function) {
          params.put(param.getKey(), ((Function<ScriptContext, String>) param.getValue())
              .apply(scriptContext));
        } else {
          params.put(param.getKey(), String.valueOf(param.getValue()));
        }
      }

      HttpEntity httpEntity;

      if (StringUtils.isEmpty(body)) {
        httpEntity = new HttpEntity(headers);
      } else {
        httpEntity = new HttpEntity<>(body, headers);
      }

      final String response;

      if (isLast && scriptContext.getContextValue("request") != null) {
        response = scriptContext.getContextValue(url);
      } else {
        response = restTemplate
            .exchange(url, method, httpEntity, String.class, params).getBody();
        scriptContext.setContextValue("request", response);
      }

      Pattern pattern = Pattern
          .compile("^(.|\\s)*(" + startTag + ")(.*)(" + endTag + ")(.|\\s)*$", Pattern.MULTILINE);

      Matcher m = pattern.matcher(response);
      if (m.find()) {
        return m.group(3);
      } else {
        return "Not found";
      }
    });
  }

  public TextJoiner asString() {
    return TextJoiner.text(scriptContext -> {
      RestTemplate restTemplate = new RestTemplate();
      final String url = this.url instanceof Function ? ((Function<ScriptContext, String>) this.url)
          .apply(scriptContext) : String.valueOf(this.url);
      final String body =
          this.body instanceof Function ? ((Function<ScriptContext, String>) this.body)
              .apply(scriptContext) : String.valueOf(this.body);

      Map<String, String> params = new HashMap<>();
      for (Map.Entry<String, Object> param : this.params.entrySet()) {
        if (param.getValue() instanceof Function) {
          params.put(param.getKey(), ((Function<ScriptContext, String>) param.getValue())
              .apply(scriptContext));
        } else {
          params.put(param.getKey(), String.valueOf(param.getValue()));
        }
      }

      HttpEntity httpEntity;

      if (StringUtils.isEmpty(body)) {
        httpEntity = new HttpEntity(headers);
      } else {
        httpEntity = new HttpEntity<>(body, headers);
      }

      final String response;

      if (isLast && scriptContext.getContextValue("request") != null) {
        response = scriptContext.getContextValue(url);
      } else {
        response = restTemplate
            .exchange(url, method, httpEntity, String.class, params).getBody();
        scriptContext.setContextValue("request", response);
      }
      return response;
    });
  }

  public Request post(Object url) {
    return setMethod(HttpMethod.POST).url(url);
  }

  public Request get(Object url) {
    return setMethod(HttpMethod.GET).url(url);
  }

  public Request last() {
    this.isLast = true;
    return this;
  }

}
