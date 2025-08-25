package me.dodo.readingnotes.dto.auth;

import me.dodo.readingnotes.domain.User;

public class AuthResult {
    private User user;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private Long serverTime;

    public AuthResult(User user, String accessToken, String refreshToken, Long expiresIn, Long serverTime) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.serverTime = serverTime;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) { this.user = user; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
    public Long getServerTime() { return serverTime; }
}
