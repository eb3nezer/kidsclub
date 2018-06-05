package kc.ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect
public class ErrorMessage {
    private String error;

    public ErrorMessage() {
    }

    public ErrorMessage(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
