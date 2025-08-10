package me.dodo.readingnotes.controller;

import jakarta.servlet.http.HttpServletRequest;
import me.dodo.readingnotes.config.ApiKeyFilter;
import me.dodo.readingnotes.domain.ReadingRecord;
import me.dodo.readingnotes.dto.ReadingRecordRequest;
import me.dodo.readingnotes.dto.ReadingRecordResponse;
import me.dodo.readingnotes.service.ReadingRecordService;
import me.dodo.readingnotes.util.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/records")
public class ReadingRecordController {
    private static final Logger log = LoggerFactory.getLogger(ReadingRecordController.class);

    private final ReadingRecordService service;
    private final JwtTokenProvider jwtTokenProvider;

    public ReadingRecordController(ReadingRecordService service, JwtTokenProvider jwtTokenProvider){
        this.service = service;
        this.jwtTokenProvider = jwtTokenProvider;
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
            @PageableDefault(size = 20, sort = "recordedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        String token = jwtTokenProvider.extractToken(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        Page<ReadingRecord> page = service.getMyRecords(userId, pageable);
        return page.map(ReadingRecordResponse::new);
    }

    // author도 넣어서 post하는 경우
//    @PostMapping
//    public ReadingRecordResponse saveRecord(@RequestBody ReadingRecordRequest request) {
//        ReadingRecord record = new ReadingRecord                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   (
//                request.getTitle(),
//                request.getAuthor(),
//                request.getDate(),
//                request.getContent()
//        );
//
//        ReadingRecord saved = service.saveRecord(record);
//        return new ReadingRecordResponse(
//                saved.getId(), // 엔티티.get~~ 여기서 엔티티가 saved라는 이름의 엔티티일뿐.
//                saved.getTitle(),
//                saved.getAuthor(),
//                saved.getDate(),
//                saved.getContent()
//        );
//    }
//    // author 없이 post하는 경우
//    @PostMapping
//    public ReadingRecordResponse saveRecord(@RequestBody String rawJson){
//        System.out.println("📩 원본 요청:\n" + rawJson);
//        try{
//
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.findAndRegisterModules(); // 기본 모듈 등록
//            mapper.registerModule(new JavaTimeModule()); // LocalDate 지원 추가
//
//            ReadingRecordRequest request = mapper.readValue(rawJson, ReadingRecordRequest.class);
//
//            ReadingRecord record = new ReadingRecord(
//                    request.getTitle(),
//                    request.getDate(),
//                    request.getSentence(),
//                    request.getComment()
//            );
//
//            ReadingRecord saved = service.saveRecord(record);
//            return new ReadingRecordResponse(saved);
//
//        } catch (Exception e) {
//            throw new RuntimeException("JSON 파싱 실패: "+ e.getMessage());
//        }
//    }
//
//    // 전부 조회
//    @GetMapping
//    public List<ReadingRecordResponse> getAllRecords() {
//        return service.getAllRecords().stream()
//                .map(r->new ReadingRecordResponse( r.getId(), r.getTitle(), r.getAuthor(), r.getDate(), r.getSentence(), r.getComment()))
//                .collect(Collectors.toList());
//    }
//
//    // ID로 조회
//    @GetMapping("/{id}")
//    public ReadingRecordResponse getRecordById(@PathVariable Long id) {
//        ReadingRecord r = service.getRecord(id);
//        return new ReadingRecordResponse(
//                r.getId(), r.getTitle(), r.getAuthor(),
//                r.getDate(), r.getSentence(), r.getComment()
//        );
//    }
//
//    // title, author, date로 조회
//    @GetMapping("/search")
//    public List<ReadingRecordResponse> searchRecords(
//            @RequestParam(required = false) String title,
//            @RequestParam(required = false) String author,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
//            @RequestParam(required = false, defaultValue = "date") String sort,
//            @RequestParam(required = false, defaultValue = "desc") String order
//    ){
//        return service.searchRecords(title, author, date, sort, order);
//    }
//
//    // 수정
//    @PutMapping("/{id}")
//    public ReadingRecordResponse update(@PathVariable Long id, @RequestBody ReadingRecordRequest request){
//        return service.update(id, request);
//    }
//
//    //삭제
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteRecord(@PathVariable Long id){
//        String message = service.deleteRecord(id);
//
//        return ResponseEntity.ok(message);
//    }
}
