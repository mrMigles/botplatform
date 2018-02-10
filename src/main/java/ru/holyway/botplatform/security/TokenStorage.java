package ru.holyway.botplatform.security;

import org.springframework.stereotype.Component;
import ru.holyway.botplatform.web.entities.UserInfo;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by voyo on 10/14/2017.
 */
@Component
public class TokenStorage {
    private Map<String, UserInfo> codes = new ConcurrentHashMap<>();

    public UserInfo getUserInfo(final String code) {
        if (codes.get(code) != null) {
            return codes.get(code);
        }
        return null;
    }

    public String proceedUser(final UserInfo userInfo) {
        final String code = UUID.randomUUID().toString();
        codes.put(code, userInfo);
        return code;
    }

    public void clearCode(final String code) {
        codes.remove(code);
    }
}
