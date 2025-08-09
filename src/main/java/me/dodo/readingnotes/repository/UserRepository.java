package me.dodo.readingnotes.repository;

import me.dodo.readingnotes.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 필요하면 여기에 커스텀 쿼리 작성

    // JPA가 알아서 쿼리 생성해줌.
    boolean existsByEmail(String email); // SELECT COUNT(*) FROM user WHERE email = ?
    boolean existsByUsername(String username); // SELECT COUNT(*) FROM user WHERE username = ?

    Optional<User> findByEmail(String email);

    Optional<User> findByApiKey(String apiKey);
}