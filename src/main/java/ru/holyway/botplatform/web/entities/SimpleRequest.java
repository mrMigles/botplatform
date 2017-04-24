package ru.holyway.botplatform.web.entities;

/**
 * Created by seiv0814 on 10-04-17.
 */
public class SimpleRequest {
    private ResultRequest result;

    public SimpleRequest() {
    }

    public SimpleRequest(ResultRequest result) {
        this.result = result;
    }

    public ResultRequest getResult() {
        return result;
    }

    public void setResult(ResultRequest result) {
        this.result = result;
    }
}
