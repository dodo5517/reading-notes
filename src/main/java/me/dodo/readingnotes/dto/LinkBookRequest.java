package me.dodo.readingnotes.dto;

public class LinkBookRequest {
    private String source;       // "KAKAO"
    private String externalId;   // 공급자별 ID (카카오는 url/ISBN13 등)
    private String isbn13;       // 가능하면 제공
    private String isbn10;
    private String title;        // 표준화해 저장할 내부 기준값
    private String author;
    private String publisher;
    private String coverUrl;
    private String publishedDate; // yyyy-MM-dd / yyyy-MM / yyyy 중 하나

    // Getter / Setter
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
    public String getIsbn13() { return isbn13; }
    public void setIsbn13(String isbn13) { this.isbn13 = isbn13; }
    public String getIsbn10() { return isbn10; }
    public void setIsbn10(String isbn10) { this.isbn10 = isbn10; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public String getPublishedDate() { return publishedDate; }
    public void setPublishedDate(String publishedDate) { this.publishedDate = publishedDate; }
}
