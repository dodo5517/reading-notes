package me.dodo.readingnotes.dto;

import me.dodo.readingnotes.domain.ReadingRecord;

import java.time.LocalDate;

public class ReadingRecordResponse {
    private Long id;
    private String title;
    private String author;
    private LocalDate date;
    private String sentence;
    private String comment;

    public ReadingRecordResponse(Long id, String title, String author, LocalDate date, String sentence, String comment) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.date = date;
        this.sentence = sentence;
        this.comment = comment;
    }

    // 새로운 생성자: ReadingRecord 객체를 받아서 처리 (update 처리할 때 ReadingRecord 객체를 받기 때문에 필요)
    public ReadingRecordResponse(ReadingRecord record) {
        this.id = record.getId();
        this.title = record.getTitle();
        this.author = record.getAuthor();
        this.date = record.getDate();
        this.sentence = record.getSentence();
        this.comment = record.getComment();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getSentence() {
        return sentence;
    }

    public String getComment() {
        return comment;
    }

}
