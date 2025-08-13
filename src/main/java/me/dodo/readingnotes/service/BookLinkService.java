package me.dodo.readingnotes.service;

import me.dodo.readingnotes.domain.Book;
import me.dodo.readingnotes.domain.BookSourceLink;
import me.dodo.readingnotes.domain.ReadingRecord;
import me.dodo.readingnotes.dto.LinkBookRequest;
import me.dodo.readingnotes.repository.BookRepository;
import me.dodo.readingnotes.repository.BookSourceLinkRepository;
import me.dodo.readingnotes.repository.ReadingRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
public class BookLinkService {
    private static final Logger log = LoggerFactory.getLogger(BookLinkService.class.getName());

    private final BookRepository bookRepo;
    private final BookSourceLinkRepository linkRepo;
    private final ReadingRecordRepository recordRepo;

    public BookLinkService(BookRepository bookRepo,
                           BookSourceLinkRepository linkRepo,
                           ReadingRecordRepository recordRepo) {
        this.bookRepo = bookRepo;
        this.linkRepo = linkRepo;
        this.recordRepo = recordRepo;
    }

    @Transactional
    public void linkRecord(Long recordId, LinkBookRequest req) {
        // Book upsert (ISBN13 우선)
        Book book = upsertBook(req);

        // Source link upsert
        upsertSourceLink(book, req);

        // 기록 연결
        ReadingRecord rec = recordRepo.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 recordId 입니다."));
        rec.setBook(book); // 기록 엔티티에 책 정보 저장
        rec.setMatchStatus(ReadingRecord.MatchStatus.RESOLVED_MANUAL); // 책 매칭 완료
    }

    // Book upsert
    private Book upsertBook(LinkBookRequest r) {
        if (r.getIsbn13() != null && !r.getIsbn13().isBlank()) {
            return bookRepo.findByIsbn13(r.getIsbn13())
                    .orElseGet(() -> bookRepo.save(toBook(r)));
        }
        // ISBN13이 없으면(기존 값이 없으면) 제목/저자 기준 신규 생성(나중에 중복 가능성 해결해야함)
        return bookRepo.save(toBook(r));
    }

    // 찾아온 책 정보 책 엔티티로 옮김
    private Book toBook(LinkBookRequest r) {
        Book b = new Book();
        b.setTitle(r.getTitle());
        b.setAuthor(r.getAuthor());
        b.setPublisher(r.getPublisher());
        b.setIsbn10(r.getIsbn10());
        b.setIsbn13(r.getIsbn13());
        b.setCoverUrl(r.getCoverUrl());
        // 날짜 파싱해서 저장
        b.setPublishedDate(parseFlexible(r.getPublishedDate()));

        log.debug("book: {}", b.toString());
        return b;
    }

    // SourceLink upsert
    private void upsertSourceLink(Book book, LinkBookRequest r) {
        if (r.getSource() == null) return;
        // 기존 값 없으면 새로 생성
        BookSourceLink link = linkRepo.findBySourceAndExternalId(r.getSource(), r.getExternalId())
                .orElseGet(BookSourceLink::new);
        link.setBook(book);
        link.setSource(r.getSource());
        link.setExternalId(r.getExternalId());
        link.setIsbn10(r.getIsbn10());
        link.setIsbn13(r.getIsbn13());
        link.setMetaJson(null);
        link.setSyncedAt(java.time.LocalDateTime.now());
        linkRepo.save(link);
    }
    
    // 날짜 파싱
    private LocalDate parseFlexible(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            if (s.matches("\\d{4}-\\d{2}-\\d{2}")) {
                // "연-월-일"일 때 그대로 파싱
                return LocalDate.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else if (s.matches("\\d{4}-\\d{2}")) {
                // "연-월"일 때 1일로 보정
                return LocalDate.parse(s + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else if (s.matches("\\d{4}")) {
                // "연"일 때 1월 1일로 보정
                return LocalDate.parse(s + "-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        } catch (Exception ignore) { }
        return null;
    }
}
