package me.dodo.readingnotes.service;

import me.dodo.readingnotes.domain.ReadingRecord;
import me.dodo.readingnotes.domain.User;
import me.dodo.readingnotes.dto.ReadingRecordRequest;
import me.dodo.readingnotes.repository.ReadingRecordRepository;
import me.dodo.readingnotes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReadingRecordService {

    private final ReadingRecordRepository readingRecordRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReadingRecordService(ReadingRecordRepository readingRecordRepository,
                                UserRepository userRepository) {
        this.readingRecordRepository = readingRecordRepository;
        this.userRepository = userRepository;
    }

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

        return readingRecordRepository.save(record);
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
