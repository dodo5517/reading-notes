package me.dodo.readingnotes.controller;

import jakarta.servlet.http.HttpServletRequest;
import me.dodo.readingnotes.config.ApiKeyFilter;
import me.dodo.readingnotes.domain.ReadingRecord;
import me.dodo.readingnotes.dto.book.BookRecordsPageResponse;
import me.dodo.readingnotes.dto.book.BookWithLastRecordResponse;
import me.dodo.readingnotes.dto.calendar.CalendarResponse;
import me.dodo.readingnotes.dto.reading.ReadingRecordRequest;
import me.dodo.readingnotes.dto.reading.ReadingRecordResponse;
import me.dodo.readingnotes.service.ReadingCalendarService;
import me.dodo.readingnotes.service.ReadingRecordService;
import me.dodo.readingnotes.util.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/records")
public class ReadingRecordController {
    private static final Logger log = LoggerFactory.getLogger(ReadingRecordController.class);

    private final ReadingRecordService service;
    private final JwtTokenProvider jwtTokenProvider;
    private final ReadingCalendarService calendarService;

    public ReadingRecordController(ReadingRecordService service, JwtTokenProvider jwtTokenProvider,
                                   ReadingCalendarService calendarService) {
        this.service = service;
        this.jwtTokenProvider = jwtTokenProvider;
        this.calendarService = calendarService;
    }

    // 아이폰 단축어로 메모 추가
    @PostMapping
    public ResponseEntity<Long> create(HttpServletRequest request,
                                       @RequestBody ReadingRecordRequest req) {
        Long userId = (Long) request.getAttribute(ApiKeyFilter.ATTR_API_USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).build(); // 필터가 보통 막지만 방어
        }
        ReadingRecord saved = service.createByUserId(userId, req);
        return ResponseEntity.ok(saved.getId());
    }

    // 해당 유저의 최근 N(default=3)개 기록 보기(메인 화면용)
    @GetMapping("/me/summary")
    public  List<ReadingRecordResponse> getMyLatestRecords(
            HttpServletRequest request,
            @RequestParam(name = "size", defaultValue = "3") int size
    ){
        log.info("getMySummaryRecords");

        // 헤더에서 Authorization 추출
        String token = jwtTokenProvider.extractToken(request);
        // 토큰에서 userId 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        List<ReadingRecord> list = service.getLatestRecords(userId, size);
        log.debug("list: {}", list.toString());
        return list.stream().map(ReadingRecordResponse::new).collect(Collectors.toList());
    }

    // 해당 유저의 모든 기록 보기
    @GetMapping("/me")
    // Page로 반환하므로 관련된 메타데이터도 따로 전달됨.
    public Page<ReadingRecordResponse> getMyRecords(
            HttpServletRequest request,
            @PageableDefault(size = 10, sort = "recordedAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "q", required = false) String q
    ) {
        String token = jwtTokenProvider.extractToken(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        Page<ReadingRecord> page = service.getMyRecords(userId, q, pageable);
        return page.map(ReadingRecordResponse::new);
    }

    // 해당 유저가 읽은 책 중 매핑이 끝난 N(default=20)개 책들 보기
    @GetMapping("/me/books")
    public Page<BookWithLastRecordResponse> getMyConfirmedBooks(
            HttpServletRequest request,
            @RequestParam(value = "q",required = false) String q,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "recent") String sort
    ) {
        String token = jwtTokenProvider.extractToken(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        Pageable pageable = PageRequest.of(page, size);

        return service.getConfirmedBooks(userId, q, pageable, sort);
    }

    // 해당 유저가 기록한 책 한 권에 대한 모든 기록 불러오기
    @GetMapping("/books/{bookId}")
    public BookRecordsPageResponse getBookRecords(
            @PathVariable Long bookId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request
    ) {
        String token = jwtTokenProvider.extractToken(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        return service.getBookRecordsByCursor(userId, bookId, cursor, size);
    }

    // 한 달 동안 기록한 날짜 불러오기
    @GetMapping("/calendar")
    public CalendarResponse getCalendar(@RequestParam int year,
                                        @RequestParam int month,
                                        HttpServletRequest request) {
        String accessToken = jwtTokenProvider.extractToken(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(accessToken);

        return calendarService.getMonthly(userId, year, month);
    }
}
