package me.dodo.readingnotes.service;

import me.dodo.readingnotes.domain.Book;
import me.dodo.readingnotes.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // 책 저장
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    // 전체 책 조회
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    // ID로 책 조회
    public Book findBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    // 책 삭제
    public String deleteBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("해당 ID의 책이 없습니다."));
        // 삭제
        bookRepository.delete(book);
        //삭제 완료 메시지
        return "삭제가 완료되었습니다.";
    }

}
