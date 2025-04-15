package me.dodo.readingnotes.service;

import me.dodo.readingnotes.domain.ReadingRecord;
import me.dodo.readingnotes.dto.ReadingRecordRequest;
import me.dodo.readingnotes.dto.ReadingRecordResponse;
import me.dodo.readingnotes.repository.ReadingRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReadingRecordService {

    private final ReadingRecordRepository repository;

    @Autowired
    public ReadingRecordService(ReadingRecordRepository repository) {
        this.repository = repository;
    }

    //기록 저장
    public ReadingRecord saveRecord(ReadingRecord record) {
        return repository.save(record);
    }

    //ID로 조회
    public ReadingRecord getRecord(long id) {
        return repository.findById(id).orElse(null);
    }

    //title, author, date로 조회
    public List<ReadingRecordResponse> searchRecords(String title, String author, LocalDate date){
        List<ReadingRecord> results = repository.findAll().stream()
                .filter(r -> title == null || r.getTitle().contains(title))
                .filter(r -> author == null || r.getAuthor().contains(author))
                .filter(r -> date == null || r.getDate().isEqual(date))
                .collect(Collectors.toList());

        return results.stream()
                .map(ReadingRecordResponse::new)
                .collect(Collectors.toList());
    }


    //전체 조회
    public List<ReadingRecord> getAllRecords() {
        return repository.findAll();
    }

    //수정
    public ReadingRecordResponse update(Long id, ReadingRecordRequest request) {
        ReadingRecord record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 기록이 없습니다: "+ id));

        //수정할 필드만 바꾸기
        record.setTitle(request.getTitle());
        record.setAuthor(request.getAuthor());
        record.setDate(request.getDate());
        record.setSentence(request.getSentence());
        record.setComment(request.getComment());

        //저장 후 응답 DTO로 변환
        ReadingRecord updated = repository.save(record);
        return new ReadingRecordResponse(updated);
    }

    //삭제
    public void deleteRecord(long id) {
        repository.deleteById(id);
    }
}
