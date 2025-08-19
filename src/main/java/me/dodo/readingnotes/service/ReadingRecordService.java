package me.dodo.readingnotes.service;

import me.dodo.readingnotes.domain.Book;
import me.dodo.readingnotes.domain.ReadingRecord;
import me.dodo.readingnotes.domain.User;
import me.dodo.readingnotes.dto.book.*;
import me.dodo.readingnotes.dto.reading.ReadingRecordItem;
import me.dodo.readingnotes.dto.reading.ReadingRecordRequest;
import me.dodo.readingnotes.external.KakaoBookClient;
import me.dodo.readingnotes.repository.BookRepository;
import me.dodo.readingnotes.repository.ReadingRecordRepository;
import me.dodo.readingnotes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReadingRecordService {

    private final ReadingRecordRepository readingRecordRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final KakaoBookClient kakaoBookClient;
    private final BookMatcherService bookMatcherService;
    private final BookLinkService bookLinkService;

    private static final int MAX_PAGE_SIZE = 30;
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    @Autowired
    public ReadingRecordService(ReadingRecordRepository readingRecordRepository,
                                BookRepository bookRepository,
                                UserRepository userRepository, KakaoBookClient kakaoBookClient,
                                BookMatcherService bookMatcherService, BookLinkService bookLinkService) {
        this.readingRecordRepository = readingRecordRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.kakaoBookClient = kakaoBookClient;
        this.bookMatcherService = bookMatcherService;
        this.bookLinkService = bookLinkService;
    }

    // 새로운 기록 생성
    @Transactional
    public ReadingRecord createByUserId(Long userId, ReadingRecordRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        ReadingRecord record = new ReadingRecord();
        record.setUser(user);
        record.setSentence(req.getSentence());
        record.setComment(req.getComment());
        record.setRawTitle(req.getRawTitle());
        record.setRawAuthor(req.getRawAuthor());
        record.setRecordedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());

        ReadingRecord saved = readingRecordRepository.save(record);


        // 제목+작가 모두 있을 경우
        if (present(saved.getRawTitle()) && present(saved.getRawAuthor())) {
            // Kakao 검색
            List<BookCandidate> candidates = kakaoBookClient.search(saved.getRawTitle(), saved.getRawAuthor(), 10);

            // BookMatcher로 베스트 선택
            BookMatcherService.MatchResult result =
                    bookMatcherService.pickBest(saved.getRawTitle(), saved.getRawAuthor(), candidates);

            // 자동 확정이면 저장 진입점으로 위임
            if (result.best != null && result.autoMatch) {
                //검색결과 DTO → 저장 명령 DTO 변환
                LinkBookRequest reqDto = LinkBookRequest.fromCandidate(result.best);

                // 스냅샷(근거 데이터) JSON 구성
                Map<String, Object> snapshot = Map.of(
                        "provider", reqDto.getSource(), // "KAKAO"
                        "score", result.score,
                        "query", Map.of("title", saved.getRawTitle(), "author", saved.getRawAuthor()),
                        "candidate", Map.of(
                                "title", result.best.getTitle(),
                                "author", result.best.getAuthor(),
                                "isbn10", result.best.getIsbn10(),
                                "isbn13", result.best.getIsbn13(),
                                "publisher", result.best.getPublisher(),
                                "publishedDate", result.best.getPublishedDate() == null ? null : result.best.getPublishedDate().toString(),
                                "thumbnailUrl", result.best.getThumbnailUrl(),
                                "externalId", result.best.getExternalId()
                        ),
                        "matcher", Map.of(
                                "threshold", 0.88,
                                "weights", Map.of("title", 0.7, "author", 0.3),
                                "version", "2025-08-18"
                        )
                );
                String snapshotJson = toJsonSafe(snapshot); // 아래 유틸 참고

                // Book/Link/Record 한 번에 처리
                bookLinkService.linkRecordAuto(saved.getId(), reqDto, result.score, snapshotJson);
            }
        }
        return saved;
    }
    private boolean present(String s) { return s != null && !s.isBlank(); }
    // 간단 버전: 필요시 Jackson 빈 주입으로 교체(아래 참고)
    private String toJsonSafe(Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    // 해당 유저의 최신 N개 기록 불러오기
    public List<ReadingRecord> getLatestRecords(Long userId, int size) {
        PageRequest pr = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "recordedAt"));
        return readingRecordRepository.findLatestByUser(userId, pr);
    }

    // 해당 유저의 모든 기록 불러오기
    public Page<ReadingRecord> getMyRecords(Long userId, Pageable pageable) {
        return readingRecordRepository.findByUser_IdOrderByRecordedAtDesc(userId, pageable);
    }

    // 해당 유저의 매칭 끝난 책 리스트 불러오기
    @Transactional(readOnly = true)
    public Page<BookWithLastRecordResponse> getConfirmedBooks(Long userId, String q, Pageable pageable, String sort) {
        if ("title".equalsIgnoreCase(sort)) {
            return readingRecordRepository.findConfirmedBooksByTitle(userId, q, pageable);
        }
        return readingRecordRepository.findConfirmedBooksByRecent(userId, q, pageable);
    }

    // 해당 유저가 기록한 책 한 권에 대한 기록 불러오기
    @Transactional(readOnly = true)
    public BookRecordsPageResponse getBookRecordsByCursor(Long userId, Long bookId, String cursor, int size) {
        // 책 찾기
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));

        // size 정규화
        int pageSize = normalizeSize(size);
        Cursor c = parseCursor(cursor);

        // 기록 시간 내림차순 → id 내림차순.
        Sort sort = Sort.by("recordedAt").descending().and(Sort.by("id").descending());
        // 커서가 있다면 커서보다 더 작은(과거) 레코드만 가져옴.
        List<ReadingRecord> fetched = readingRecordRepository.findSliceByUserAndBookWithCursor(
                userId, bookId, c.cursorAt, c.cursorId, PageRequest.of(0, pageSize + 1, sort)
        );

        // 기록이 더 남았는지 확인(남았으면=true, 안 남았으면=false)
        boolean hasMore = fetched.size() > pageSize;
        // 더 남았어도 pageSize만큼만 가져옴
        if (hasMore) fetched = new ArrayList<>(fetched.subList(0, pageSize));

        // 현재 페이지의 마지막 요소의 (recordedAt, id)를 커서 문자열(“epochMillis_id”)로 직렬화하여 반환
        String nextCursor = null;
        if (hasMore && !fetched.isEmpty()) {
            ReadingRecord last = fetched.get(fetched.size() - 1);
            nextCursor = buildCursor(last.getRecordedAt(), last.getId());
        }

        // 책 정보 구성
        BookMetaResponse bookMeta = new BookMetaResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublishedDate() != null ? book.getPublishedDate().toString() : null,
                book.getCoverUrl()
        );

        // 기록 정보 매핑
        List<ReadingRecordItem> items = fetched.stream()
                .map(r -> new ReadingRecordItem(r.getId(), r.getRecordedAt(), r.getSentence(), r.getComment()))
                .toList();

        return new BookRecordsPageResponse(bookMeta, items, nextCursor, hasMore);
    }
    // pageSize 최소/최대 규정
    private int normalizeSize(int size) {
        if (size <= 0) return 20;
        return Math.min(size, MAX_PAGE_SIZE);
    }
    // cursorAt,id를 저장
    private static class Cursor {
        final LocalDateTime cursorAt;
        final Long cursorId;
        Cursor(LocalDateTime at, Long id) { this.cursorAt = at; this.cursorId = id; }
    }
    // "epochMillis_id" -> (LocalDateTime, id)로 변환
    // null이면 첫 페이지라는 뜻임.
    private Cursor parseCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) return new Cursor(null, null);
        String[] parts = cursor.split("_");
        if (parts.length != 2) throw new IllegalArgumentException("커서 형식이 올바르지 않습니다.");
        long epochMillis = Long.parseLong(parts[0]);
        long id = Long.parseLong(parts[1]);
        LocalDateTime at = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZONE);
        return new Cursor(at, id);
    }
    // (recordedAt, id) -> "epochMillis_id"로 직렬화
    private String buildCursor(LocalDateTime recordedAt, Long id) {
        long epochMillis = recordedAt.atZone(ZONE).toInstant().toEpochMilli();
        return epochMillis + "_" + id;
    }

    // 기록 저장
    public ReadingRecord saveRecord(ReadingRecord record) {
        return readingRecordRepository.save(record);
    }

    // 전체 기록 조회
    public List<ReadingRecord> getAllRecords() {
        return readingRecordRepository.findAll();
    }

    // ID로 기록 조회
    public ReadingRecord getRecord(long id) {
        return readingRecordRepository.findById(id).orElse(null);
    }

    // 기록 삭제
    public String deleteRecordById(long id) {
        // 삭제하려는 행의 존재 여부 확인
        ReadingRecord record = readingRecordRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("해당 ID의 기록이 없습니다."));
        // 삭제
        readingRecordRepository.delete(record);
        // 삭제 완료 메시지
        return "삭제가 완료되었습니다.";
    }



//    //title, author, date로 조회
//    public List<ReadingRecordResponse> searchRecords(String title, String author, LocalDate date, String sort, String order) {
//        return repository.findAll().stream()
//                .filter(r -> (title == null || r.getTitle().contains(title)))
//                .filter(r -> (author == null || r.getAuthor().contains(author)))
//                .filter(r -> (date == null || r.getDate().equals(date)))
//                .sorted((r1, r2) -> {
//                    int compareResult = 0;
//                    if ("title".equalsIgnoreCase(sort)) {
//                        compareResult = r1.getTitle().compareToIgnoreCase(r2.getTitle());
//                    } else if ("author".equalsIgnoreCase(sort)) {
//                        compareResult = r1.getAuthor().compareToIgnoreCase(r2.getAuthor());
//                    } else if ("date".equalsIgnoreCase(sort)) {
//                        compareResult = r1.getDate().compareTo(r2.getDate());
//                    }
//
//                    return "desc".equalsIgnoreCase(order) ? -compareResult : compareResult;
//                })
//                .map(ReadingRecordResponse::new) // ReadingRecord를 ReadingRecordResponse로 변환
//                .collect(Collectors.toList());
//    }




//    //수정
//    public ReadingRecordResponse update(Long id, ReadingRecordRequest request) {
//        ReadingRecord record = repository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 기록이 없습니다: "+ id));
//
//        //수정할 필드만 바꾸기
//        record.setTitle(request.getTitle());
//        record.setAuthor(request.getAuthor());
//        record.setDate(request.getDate());
//        record.setSentence(request.getSentence());
//        record.setComment(request.getComment());
//
//        //저장 후 응답 DTO로 변환
//        ReadingRecord updated = repository.save(record);
//        return new ReadingRecordResponse(updated);
//    }


}
