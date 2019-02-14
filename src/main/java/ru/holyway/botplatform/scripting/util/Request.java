package ru.holyway.botplatform.scripting.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import ru.holyway.botplatform.scripting.ScriptContext;

public class Request {

  private Map<String, Object> params = new HashMap<>();

  private Object body = "";

  private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

  private Object url = "";

  private HttpMethod method;

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

  public Function<ScriptContext, String> asJson(String jsonPath) {
    return scriptContext -> {
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

      ResponseEntity<String> response = restTemplate
          .exchange(url, method, httpEntity, String.class, params);
      final JSONObject jsonObject = new JSONObject(response.getBody());
      return jsonObject.getString(jsonPath) != null ? jsonObject.getString(jsonPath) : "Not Found";
    };
  }

  public Function<ScriptContext, String> asHtml(String startTag, String endTag) {
    return scriptContext -> {
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

      ResponseEntity<String> response = restTemplate
          .exchange(url, method, httpEntity, String.class, params);
      final String res = response.getBody();

      Pattern pattern = Pattern
          .compile("^(.|\\s)*(" + startTag + ")(.*)(" + endTag + ")(.|\\s)*$", Pattern.MULTILINE);

      Matcher m = pattern.matcher(res);
      if (m.find()) {
        return m.group(3);
      } else {
        return "Not found";
      }
    };
  }

  public Function<ScriptContext, String> asString() {
    return scriptContext -> {
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

      ResponseEntity<String> response = restTemplate
          .exchange(url, method, httpEntity, String.class, params);
      return response.getBody();
    };
  }


  public static Request post(Object url) {
    return new Request().setMethod(HttpMethod.POST).url(url);
  }

  public static Request get(Object url) {
    return new Request().setMethod(HttpMethod.GET).url(url);
  }

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

}
