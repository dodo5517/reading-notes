package me.dodo.readingnotes.dto.book;

import me.dodo.readingnotes.domain.Book;

public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn10;
    private String isbn13;
    private String coverUrl;

    public BookResponse(Book book) {
    }

    public BookResponse(Long bookId, String title, String author, String isbn10, String isbn13, String coverUrl) {
        this.id = bookId;
        this.title = title;
        this.author = author;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.coverUrl = coverUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn10() { return isbn10; }
    public void setIsbn10(String isbn10) { this.isbn10 = isbn10; }

    public String getIsbn13() { return isbn13; }
    public void setIsbn13(String isbn13) { this.isbn13 = isbn13; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
}
