package me.dodo.readingnotes.repository;

import me.dodo.readingnotes.domain.BookSourceLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookSourceLinkRepository extends JpaRepository<BookSourceLink, Long> {
    // 중복 확인
    Optional<BookSourceLink> findBySourceAndExternalId(String source, String externalId);
    boolean existsBySourceAndExternalId(String source, String externalId);
}
