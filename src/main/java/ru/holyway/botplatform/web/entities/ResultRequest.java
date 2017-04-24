package ru.holyway.botplatform.web.entities;

import java.util.Map;

/**
 * Created by seiv0814 on 10-04-17.
 */
public class ResultRequest {
    private String action;
    private Map<String, String> parameters;

    public ResultRequest() {
    }

    public ResultRequest(String action, Map<String, String> parameters) {
        this.action = action;
        this.parameters = parameters;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
