package ru.holyway.botplatform.core.entity;

import java.util.concurrent.TimeUnit;

/**
 * Created by seiv0814 on 10-11-17.
 */
public class UserAccessInfo {
    private String userName;
    private String userLogin;
    private String chatId;
    private Long expirationTime;

    public String getUserName() {
        return userName;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public String getChatId() {
        return chatId;
    }

    public UserAccessInfo(String userName, String userLogin, String chatId) {
        this.userName = userName;
        this.userLogin = userLogin;
        this.chatId = chatId;
        this.expirationTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
    }

    public Long getExpirationTime() {
        return expirationTime;
    }
}
