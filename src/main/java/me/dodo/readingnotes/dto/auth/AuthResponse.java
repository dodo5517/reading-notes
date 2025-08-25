package me.dodo.readingnotes.dto.auth;
import me.dodo.readingnotes.dto.user.UserResponse;

public class AuthResponse {
    private final String message;
    private final UserResponse user;
    private final String accessToken;
    private final String refreshToken;
    private Long expiresIn;
    private Long serverTime;

    public AuthResponse(String message, UserResponse user, String accessToken, String refreshToken, Long expiresIn, Long serverTime) {
        this.message = message;
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.serverTime = serverTime;
    }

    public String getMessage() { return message; }
    public UserResponse getUser() { return user; }
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public Long getExpiresIn() { return expiresIn; }
    public Long getServerTime() { return serverTime; }
}
