package me.dodo.readingnotes.dto.book;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.dodo.readingnotes.dto.common.AuthorsFlexibleDeserializer;

import java.time.LocalDate;

public class BookCandidate {
    private String source; // KAKAO, NAVER
    private String externalId; // 공급자별 고유 ID(카카오는 url 또는 isbn13 사용 권장)
    private String title;

    // 배열과 문자열 모두 수용함.
    @JsonDeserialize(using = AuthorsFlexibleDeserializer.class)
    private String author;
    private String isbn10;
    private String isbn13;
    private String publisher;
    private LocalDate publishedDate;
    private String thumbnailUrl;
    private double score;  // 점수화는 상위 서비스에서 계산

    @Override
    public String toString() {
        return "BookCandidate{" +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                '}';
    }

    // Getter / Setter
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn10() { return isbn10; }
    public void setIsbn10(String isbn10) { this.isbn10 = isbn10; }
    public String getIsbn13() { return isbn13; }
    public void setIsbn13(String isbn13) { this.isbn13 = isbn13; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public LocalDate getPublishedDate() { return publishedDate; }
    public void setPublishedDate(LocalDate publishedDate) { this.publishedDate = publishedDate; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
}
