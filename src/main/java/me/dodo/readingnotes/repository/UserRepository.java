package me.dodo.readingnotes.repository;

import me.dodo.readingnotes.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // 필요하면 여기에 커스텀 쿼리 작성
}