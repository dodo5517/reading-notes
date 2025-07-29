package me.dodo.readingnotes.dto;

public class LoginResponse {
    private String message;
    private UserResponse user;

    public LoginResponse(String message, UserResponse user) {
        this.message = message;
        this.user = user;

    }

    public String getMessage() { return message; }
    public UserResponse getUser() { return user; }
}
