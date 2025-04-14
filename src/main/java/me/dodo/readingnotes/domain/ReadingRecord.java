package me.dodo.readingnotes.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity //이 클래스가 JPA 엔티티임을 선언. DB 테이블과 매핑됨
@Table(name = "reading_record") //DB에서 이 엔티티가 매핑될 테이블 이름을 지정함
public class ReadingRecord {

    @Id //이 필드(id)가 **기본 키(PK)**임을 나타냄
    //기본 키의 값을 DB가 **자동 증가(Auto Increment)**로 생성하도록 지정함
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private LocalDate date;
    private String sentence;
    private String comment;

    // 기본 생성자 (JPA 필수)
    public ReadingRecord() {
    }

    // author 없이 만들 때 사용 (POST)
    public ReadingRecord(String title, LocalDate date, String sentence, String comment) {
        this.title = title;
        this.date = date;
        this.sentence = sentence;
        this.comment = comment;
    }

    // 전체 필드 생성자 author 포함해서 만들거나 수정할 때 사용 (PUT)
    public ReadingRecord(String title, String author, LocalDate date, String sentence, String comment) {
        this.title = title;
        this.author = "Unknown";
        this.date = date;
        this.sentence = sentence;
        this.comment = comment;
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

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
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