package ru.holyway.botplatform.web.entities;

/**
 * Created by voyo on 10/14/2017.
 */
public class UserInfo {
    private String userName;
    private String chatId;

    public UserInfo(String userName, String chatId) {
        this.userName = userName;
        this.chatId = chatId;
    }

    public String getUserName() {
        return userName;
    }

    public String getChatId() {
        return chatId;
    }
}
