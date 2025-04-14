package me.dodo.readingnotes.repository;

import me.dodo.readingnotes.domain.ReadingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {
    // 필요하면 여기에 커스텀 쿼리도 작성
}
