package me.dodo.readingnotes.dto;

import me.dodo.readingnotes.domain.User;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String profileImageUrl;
    private String role;
    private String maskedApiKey;

    // UserResponse 객체
    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.profileImageUrl = user.getProfileImageUrl();
        this.role = user.getRole();

        String apiKey = user.getApiKey();
        this.maskedApiKey = maskApiKey(apiKey);
    }

    // api_key 마스킹 하기
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 4) return "****";
        int visibleCount = 4;
        int maskCount = apiKey.length() - visibleCount;
        return "*".repeat(maskCount) + apiKey.substring(maskCount);
    }

    // Getter
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public String getRole() { return role; }
    public String getMaskedApiKey() { return maskedApiKey; }
}
