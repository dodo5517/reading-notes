package me.dodo.readingnotes.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name= "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private String imageUrl;
    private LocalDate publishedDate; // 출판일
    private LocalDate addedDate; // 책이 시스템에 추가된 날짜

    // 기본 생성자(JPA 필수)
    public Book() {
    }

    // Getter / Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }
    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

    public LocalDate getAddedDate() {
        return addedDate;
    }
    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }
}
