package ru.holyway.botplatform.web.entities;

/**
 * Created by seiv0814 on 10-04-17.
 */
public class SimpleResponse {

    private String speech;

    private String displayText;

    private Object data;

    private Object contextOut;

    private String source;

    private Object followupEvent;

    public SimpleResponse() {
    }

    public SimpleResponse(String speech, String displayText) {
        this.speech = speech;
        this.displayText = displayText;
    }

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getContextOut() {
        return contextOut;
    }

    public void setContextOut(Object contextOut) {
        this.contextOut = contextOut;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Object getFollowupEvent() {
        return followupEvent;
    }

    public void setFollowupEvent(Object followupEvent) {
        this.followupEvent = followupEvent;
    }
}
