package me.dodo.readingnotes.dto;

import me.dodo.readingnotes.domain.Book;
import me.dodo.readingnotes.domain.ReadingRecord;

import java.time.LocalDateTime;

public class ReadingRecordResponse {
    private Long id;
    private String title;
    private String author;
    private String sentence;
    private String comment;
    private Boolean matched;
    private Long bookId;
    private LocalDateTime recordedAt;

    public ReadingRecordResponse(ReadingRecord r) {
        this.id = r.getId();
        this.recordedAt = r.getRecordedAt();
        
        // 책 매칭된 상태인지 확인
        boolean isResolved = r.getMatchStatus() != null &&
                (r.getMatchStatus() == ReadingRecord.MatchStatus.RESOLVED_AUTO
                || r. getMatchStatus() == ReadingRecord.MatchStatus.RESOLVED_MANUAL);

        Book book = r.getBook();
        // 책이 매칭 완료된 상태라면 연결된 책 정보 사용.
        if (isResolved && book != null) {
            this.title = book.getTitle();
            this.author = book.getAuthor();
            this.matched = true;
            this.bookId = book.getId();
        } else {
            // 매칭되지 않은 상태라면 raw 사용
            this.title = r.getRawTitle();
            this.author = r.getRawAuthor();
            this.matched = false;
            this.bookId = null;
        }
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getSentence() { return sentence; }
    public String getComment() { return comment; }
    public Boolean getMatched() { return matched; }
    public Long getBookId() { return bookId; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
}
