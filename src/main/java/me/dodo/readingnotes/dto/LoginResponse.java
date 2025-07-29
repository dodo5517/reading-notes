package me.dodo.readingnotes.dto;

public class LoginResponse {
    private String message;
    private UserResponse user;
    private String accessToken;
    private String refreshToken;

    public LoginResponse(String message, UserResponse user, String accessToken, String refreshToken) {
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
