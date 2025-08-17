package me.dodo.readingnotes.dto;

import me.dodo.readingnotes.domain.Book;

import java.time.LocalDateTime;

public class BookWithLastRecordResponse extends BookResponse {
    private LocalDateTime lastRecordAt;

    public BookWithLastRecordResponse(
            Long bookId, String title, String author, String isbn10,
            String isbn13, String coverUrl, LocalDateTime lastRecordAt) {
        super(bookId, title, author, isbn10, isbn13, coverUrl);
        this.lastRecordAt = lastRecordAt;

    }

    public LocalDateTime getLastRecordAt() { return lastRecordAt; }
}
