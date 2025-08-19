package me.dodo.readingnotes.dto.common;

public class MaskedApiKeyResponse {
    private String message;
    private String maskedApiKey;

    public MaskedApiKeyResponse(String message, String maskedApiKey) {
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
