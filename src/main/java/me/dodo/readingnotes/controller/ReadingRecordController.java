package me.dodo.readingnotes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.dodo.readingnotes.domain.ReadingRecord;
import me.dodo.readingnotes.dto.ReadingRecordRequest;
import me.dodo.readingnotes.dto.ReadingRecordResponse;
import me.dodo.readingnotes.service.ReadingRecordService;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/records")
public class ReadingRecordController {

    private final ReadingRecordService service;

    public ReadingRecordController(ReadingRecordService service){
        this.service = service;
    }

    // json 형식에 맞게 잘 와서 자동으로 파싱 경우
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
    // 불완전하거나 escape되지 않은 JSON 문자열이 와서 직접 파싱하는 경우
    @PostMapping
    public ReadingRecordResponse saveRecord(@RequestBody String rawJson){
        System.out.println("📩 원본 요청:\n" + rawJson);
        try{

           ObjectMapper mapper = new ObjectMapper();
           mapper.findAndRegisterModules(); // 기본 모듈 등록
           mapper.registerModule(new JavaTimeModule()); // LocalDate 지원 추가

           ReadingRecordRequest request = mapper.readValue(rawJson, ReadingRecordRequest.class);

           ReadingRecord record = new ReadingRecord(
                   request.getTitle(),
                   request.getDate(),
                   request.getSentence(),
                   request.getComment()
           );

           ReadingRecord saved = service.saveRecord(record);
           return new ReadingRecordResponse(saved);

        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 실패: "+ e.getMessage());
        }
    }

    @GetMapping
    public List<ReadingRecordResponse> getAllRecords() {
        return service.getAllRecords().stream()
                .map(r->new ReadingRecordResponse( r.getId(), r.getTitle(), r.getAuthor(), r.getDate(), r.getSentence(), r.getComment()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ReadingRecordResponse getRecordById(@PathVariable Long id) {
        ReadingRecord r = service.getRecord(id);
        return new ReadingRecordResponse(
                r.getId(), r.getTitle(), r.getAuthor(),
                r.getDate(), r.getSentence(), r.getComment()
        );
    }

    @PutMapping("/{id}")
    public ReadingRecordResponse update(@PathVariable Long id, @RequestBody ReadingRecordRequest request){
        return service.update(id, request);
    }
}
