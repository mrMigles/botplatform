package ru.holyway.botplatform.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import ru.holyway.botplatform.core.data.DataHelper;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;

public class AnonymousChatTokenSecurityFilter extends AnonymousAuthenticationFilter {

  @Autowired
  private DataHelper dataHelper;

  private final String key;

  public AnonymousChatTokenSecurityFilter(String key) {
    super(key);
    this.key = key;
  }

  @Override
  protected Authentication createAuthentication(HttpServletRequest request) {
    final String token = request.getHeader("Authorization");
    final String chatId = request.getParameter("chatId");
    if (StringUtils.isNotEmpty(token) && StringUtils.isNotEmpty(chatId)
        && constantTimeEquals(dataHelper.getSettings().getToken(chatId), token)) {
      return new AnonymousAuthenticationToken(key, getPrincipal(),
          Collections.singletonList(new SimpleGrantedAuthority("USER")));
    }
    return super.createAuthentication(request);
  }

  private static boolean constantTimeEquals(String expected, String actual) {
    return MessageDigest.isEqual(
        expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
  }
}
