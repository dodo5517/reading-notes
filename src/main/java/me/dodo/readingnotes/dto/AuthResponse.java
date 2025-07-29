package me.dodo.readingnotes.dto;

public class AuthResponse {
    private final String message;
    private final UserResponse user;
    private final String accessToken;
    private final String refreshToken;

    public AuthResponse(String message, UserResponse user, String accessToken, String refreshToken) {
        this.message = message;
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getMessage() { return message; }
    public UserResponse getUser() { return user; }
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
}
