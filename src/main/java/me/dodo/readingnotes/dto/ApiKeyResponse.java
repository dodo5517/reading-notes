package me.dodo.readingnotes.dto;

public class ApiKeyResponse {
    private String message;
    private String apiKey;

    public ApiKeyResponse(String message, String apiKey) {
        this.message = message;
        this.apiKey = apiKey;
    }

    public String getMessage() {
        return message;
    }
    public String getapiKey() {
        return apiKey;
    }
}
