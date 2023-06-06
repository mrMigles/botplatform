package ru.holyway.botplatform.scripting.util;

import com.jayway.jsonpath.JsonPath;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.holyway.botplatform.config.RequestFactory.HttpComponentsClientHttpRequestWithBodyFactory;
import ru.holyway.botplatform.scripting.ScriptContext;
import us.codecraft.xsoup.Xsoup;

import javax.net.ssl.SSLContext;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {

  private static RestTemplate restTemplate = new RestTemplateBuilder().setReadTimeout(5 * 60 * 1000).setConnectTimeout(5 * 60 * 1000).build();

  private static final Logger LOGGER = LoggerFactory.getLogger(Request.class);

  static {
    SSLContext sslContext = SSLContexts.createDefault();

    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"}, null, new NoopHostnameVerifier());

    HttpClient httpClient = HttpClientBuilder.create().disableCookieManagement().useSystemProperties().setSSLSocketFactory(sslsf).build();
    HttpComponentsClientHttpRequestWithBodyFactory factory = new HttpComponentsClientHttpRequestWithBodyFactory();
    factory.setHttpClient(httpClient);
    restTemplate.setRequestFactory(factory);

    restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

  }

  private Map<String, Object> params = new HashMap<>();
  private Map<String, Object> headers = new HashMap<>();

  private boolean isLast = false;

  private Object body = "";


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
        LOGGER.error("Unsupported encoding", e);
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
        LOGGER.error("Unsupported encoding", e);
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

  public Request header(String key, Object value) {
    this.headers.put(key, value);
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

  private Function<ScriptContext, String> performRequest() {
    return scriptContext -> {
      final String url = this.url instanceof Function ? ((Function<ScriptContext, String>) this.url).apply(scriptContext) : String.valueOf(this.url);
      final String body = this.body instanceof Function ? ((Function<ScriptContext, String>) this.body).apply(scriptContext) : String.valueOf(this.body);

      MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
      for (Map.Entry<String, Object> param : this.params.entrySet()) {
        if (param.getValue() instanceof Function) {
          params.add(param.getKey(), ((Function<ScriptContext, String>) param.getValue()).apply(scriptContext));
        } else {
          params.add(param.getKey(), String.valueOf(param.getValue()));
        }
      }

      MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
      for (Map.Entry<String, Object> header : this.headers.entrySet()) {
        if (header.getValue() instanceof Function) {
          headers.add(header.getKey(), ((Function<ScriptContext, String>) header.getValue()).apply(scriptContext));
        } else {
          headers.add(header.getKey(), String.valueOf(header.getValue()));
        }
      }

      HttpEntity httpEntity;

      if (!this.params.isEmpty()) {
        headers.add("Content-Type", "application/x-www-form-urlencoded");
      }
      if (StringUtils.isEmpty(body)) {
        if (params == null || params.isEmpty()) {
          httpEntity = new HttpEntity(headers);
        } else {
          httpEntity = new HttpEntity(params, headers);
        }
      } else {
        httpEntity = new HttpEntity<>(body, headers);
      }

      String response = "";

      if (isLast && scriptContext.getContextValue("request") != null) {
        response = scriptContext.getContextValue("request");
      } else {
        try {
          response = restTemplate.exchange(url, method, httpEntity, String.class).getBody();
        } catch (RestClientException e) {
          LOGGER.error("failed response to {} with {} code", url, e.getMessage(), e);
          throw new RuntimeException(e);
        } catch (Exception e) {
          LOGGER.error("failed response to {} with:", url, e);
          throw new RuntimeException(e);
        }
        scriptContext.setContextValue("request", response);
      }
      return response;
    };
  }

  public TextJoiner asJson(Object jsonPath) {
    return TextJoiner.text(scriptContext -> {
      final String response = performRequest().apply(scriptContext);
      String stringPath = jsonPath instanceof String ? String.valueOf(jsonPath) : ((Function<ScriptContext, String>) jsonPath).apply(scriptContext);
      Object res = JsonPath.read(response, stringPath);
      return String.valueOf(res);
    });
  }

  public TextJoiner asHtml(String startTag, String endTag) {
    return TextJoiner.text(scriptContext -> {

      final String response = performRequest().apply(scriptContext);

      Pattern pattern = Pattern.compile("^(.)*(" + startTag + ")(.*)(" + endTag + ")(.)*$", Pattern.MULTILINE);

      Matcher m = pattern.matcher(response.replaceAll(" ", "_"));
      if (m.find()) {
        return m.group(3).replaceAll("_", " ");
      } else {
        return "Not found";
      }
    });
  }

  public TextJoiner asXpath(String xpath) {
    return asXPath(xpath);
  }

  public TextJoiner asXPath(String xpath) {
    return TextJoiner.text(scriptContext -> {

      final String response = performRequest().apply(scriptContext);

      Document document = Jsoup.parse(response);
      List<Element> nodes = Xsoup.compile(xpath).evaluate(document).getElements();
      if (nodes != null && !nodes.isEmpty()) {
        return nodes.get(0).text();
      }
      return null;
    });
  }

  public TextJoiner asString() {
    return TextJoiner.text(scriptContext -> performRequest().apply(scriptContext));
  }

  public Request post(Object url) {
    return setMethod(HttpMethod.POST).url(url);
  }

  public Request get(Object url) {
    return setMethod(HttpMethod.GET).url(url);
  }

  public static Request request() {
    return new Request();
  }

  public Request last() {
    this.isLast = true;
    return this;
  }

}
