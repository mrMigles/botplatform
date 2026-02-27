package ru.holyway.botplatform.config;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.net.URI;

@Component
public class RequestFactory {
  private RestTemplate restTemplate = new RestTemplate();

  @PostConstruct
  public void init() {
    this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestWithBodyFactory());
  }

  public static final class HttpComponentsClientHttpRequestWithBodyFactory extends
      HttpComponentsClientHttpRequestFactory {
    @Override
    protected ClassicHttpRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
      if (httpMethod == HttpMethod.GET) {
        return new HttpGetRequestWithEntity(uri);
      }
      return super.createHttpUriRequest(httpMethod, uri);
    }
  }

  private static final class HttpGetRequestWithEntity extends HttpUriRequestBase {
    public HttpGetRequestWithEntity(final URI uri) {
      super("GET", uri);
    }

    @Override
    public String getMethod() {
      return HttpMethod.GET.name();
    }
  }

  public RestTemplate getRestTemplate() {
    return restTemplate;
  }
}
