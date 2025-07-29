package me.dodo.readingnotes.dto;

import me.dodo.readingnotes.domain.User;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String profileImageUrl;
    private String role;

    // UserResponse 객체
    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.profileImageUrl = user.getProfileImageUrl();
        this.role = user.getRole();
    }

    // Getter
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public String getRole() { return role; }
}
