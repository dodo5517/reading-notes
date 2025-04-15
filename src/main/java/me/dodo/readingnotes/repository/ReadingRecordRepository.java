package me.dodo.readingnotes.repository;

import me.dodo.readingnotes.domain.ReadingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {
    // 필요하면 여기에 커스텀 쿼리도 작성

    List<ReadingRecord> findByTitleContainingAndAuthorContainingAndDate(
        String title, String author, LocalDate date
    );
}

