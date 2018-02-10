package ru.holyway.botplatform.security;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import ru.holyway.botplatform.core.data.DataService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

public class AnonymousChatTokenSecurityFilter extends AnonymousAuthenticationFilter {

    @Autowired
    private DataService dataService;

    private final String key;

    public AnonymousChatTokenSecurityFilter(String key) {
        super(key);
        this.key = key;
    }

    @Override
    protected Authentication createAuthentication(HttpServletRequest request) {
        final String token = request.getHeader("Authorization");
        final String chatId = request.getParameter("chatId");
        if (StringUtils.isNotEmpty(token) && StringUtils.isNotEmpty(chatId) && dataService.getSettings().getToken(chatId).equals(token)) {
            return new AnonymousAuthenticationToken(key, getPrincipal(), Collections.singletonList(new SimpleGrantedAuthority("USER")));
        }
        return super.createAuthentication(request);
    }
}
