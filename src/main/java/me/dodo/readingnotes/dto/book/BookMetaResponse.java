package me.dodo.readingnotes.dto.book;

public class BookMetaResponse {
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String publishedDate;
    private String coverUrl;

    public BookMetaResponse(Long id, String title, String author, String publisher, String publishedDate, String coverUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.coverUrl = coverUrl;
    }

    // Getter
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getPublishedDate() { return publishedDate; }
    public String getCoverUrl() { return coverUrl; }
}
