package me.dodo.readingnotes.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY: 지연 로딩
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 512)
    private String token; // refresh_token

    @Column(name = "device_info", length = 255)
    private String deviceInfo; // 로그인한 장치 ex. PC, 모바일, 태블릿

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate; // 만료 날짜

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 만들어진 날짜시간

    // 생성자
    public RefreshToken() {}

    public RefreshToken(User user, String token, String deviceInfo, LocalDateTime expiryDate) {
        this.user = user;
        this.token = token;
        this.deviceInfo = deviceInfo;
        this.expiryDate = expiryDate;
    }

    // Getter/Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
