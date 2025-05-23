package me.dodo.readingnotes.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity //이 클래스가 JPA 엔티티임을 선언. DB 테이블과 매핑됨
@Table(name = "reading_record") //DB에서 이 엔티티가 매핑될 테이블 이름을 지정함
public class ReadingRecord {

    @Id //이 필드(id)가 기본 키임을 나타냄
    //기본 키의 값을 DB가 자동 증가(Auto Increment)로 생성하도록 지정함
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 관계
    @ManyToOne(fetch = FetchType.LAZY) // 다(기록):1(책) 설정
    @JoinColumn(name= "book_id") // 외래 키로 book 테이블의 id를 참조함.
    private Book book;

    private LocalDateTime date;
    private String sentence;
    private String comment;

    // 기본 생성자 (JPA 필수)
    public ReadingRecord() {
    }

    // Getter / Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }
    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
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