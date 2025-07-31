package me.dodo.readingnotes.dto;

public class ApiKeyResponse {
    private String message;
    private String maskedApiKey;

    public ApiKeyResponse(String message, String maskedApiKey) {
        this.message = message;
        this.maskedApiKey = maskedApiKey;
    }

    public String getMessage() {
        return message;
    }
    public String getMaskedApiKey() {
        return maskedApiKey;
    }
}
