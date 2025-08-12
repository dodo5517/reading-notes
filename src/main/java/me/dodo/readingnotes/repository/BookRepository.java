package me.dodo.readingnotes.repository;

import me.dodo.readingnotes.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    // 필요하면 여기에 커스텀 쿼리도 작성

    Optional<Book> findByIsbn13(String isbn13);
    boolean existsByIsbn13(String isbn13);

    // ISBN이 없을 때 임시 중복 방지용
    Optional<Book> findFirstByTitleAndAuthor(String title, String author);
}