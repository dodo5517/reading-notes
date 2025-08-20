package me.dodo.readingnotes.dto.book;

public class BookMetaResponse {
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String publishedDate;
    private String coverUrl;
    private String periodStart; // ISO 문자열 (예: "2025-08-10T14:22:31")
    private String periodEnd;

    public BookMetaResponse(Long id, String title, String author, String publisher,
                            String publishedDate, String coverUrl, String periodStart, String periodEnd) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.coverUrl = coverUrl;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }

    // Getter
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getPublishedDate() { return publishedDate; }
    public String getCoverUrl() { return coverUrl; }
    public String getPeriodStart() { return periodStart; }
    public String getPeriodEnd() { return periodEnd; }
}
