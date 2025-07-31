package me.dodo.readingnotes.repository;

import me.dodo.readingnotes.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserIdAndDeviceInfo(Long userId, String deviceInfo);

    void deleteAllByUserId(Long userId);
}
