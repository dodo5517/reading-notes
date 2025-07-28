package me.dodo.readingnotes.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name= "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username; // 유저 이름

    @Column(nullable = false, unique = true, length = 100)
    private String email; // 이메일, 모두 있어야 함

    @Column(length = 255)
    private String password; // 비밀번호 (소셜 로그인은 필요X)

    @Column(length = 20)
    private String provider; // ex.카카오, 네이버, 구글, 일반

    @Column(name = "provider_id", length = 100)
    private String providerId; // 소셜 로그인 시 인증id (일반 로그인은 필요X)

    @Column(name = "api_key", nullable = false, unique = true, length = 100)
    private String apiKey; // api_key, 모두 있어야 함

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl; // 프로필 사진

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false; // 탈퇴 여부, 기본값 = false

    @CreationTimestamp // 엔티티 인스턴스가 생성될 때 자동으로 현재 시간 입력
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성된 시간

    @UpdateTimestamp // 엔티티 인스턴스가 수정할 때 자동으로 현재 시간 입력
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 수정된 시간


    // 기본 생성자(JPA 필수)
    public User(){
    }

    // Getter / Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getApiKey() {
        return apiKey;
    }
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
