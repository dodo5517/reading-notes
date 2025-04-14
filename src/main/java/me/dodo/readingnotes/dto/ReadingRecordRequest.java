package me.dodo.readingnotes.dto;

import java.time.LocalDate;

public class ReadingRecordRequest {
    private String title;
    private LocalDate date;
    private String author;
    private String sentence;
    private String comment;

    // 기본 생성자
    // 없으면 JPA와 동일하게 Jackson이 리플렉션을 못해서 자동으로 body에 있는 값을 객체 형태로 넣을 수 없게 됨.
    // 스프링은 내부적으로 Jackson이라는 JSON 변환기를 씀.
    public ReadingRecordRequest(){}


    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSentence() {
        return sentence;
    }
    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}
