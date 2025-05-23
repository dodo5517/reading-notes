package me.dodo.readingnotes.repository;

import me.dodo.readingnotes.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    // 필요하면 여기에 커스텀 쿼리도 작성
}