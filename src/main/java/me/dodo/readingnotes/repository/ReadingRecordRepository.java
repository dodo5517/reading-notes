package me.dodo.readingnotes.repository;

import me.dodo.readingnotes.domain.ReadingRecord;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {
    // 필요하면 여기에 커스텀 쿼리도 작성

    // 해당 유저의 모든 기록을 페이지 단위로 가져옴
    // Page라서 전체 개수(totalElements), 전체 페이지 수(totalPages), 현재 페이지 번호(pageNumber) 포함함.
    // N+1 줄이기 위해서 book을 EntityGraph로 로딩함.
    @EntityGraph(attributePaths = "book")
    Page<ReadingRecord> findByUser_IdOrderByRecordedAtDesc(Long userId, Pageable pageable);

    // 해당 유저의 기록 중 최신 N개만 가져옴,  count 쿼리 없음.
    // 페이지네이션 필요 없으니 굳이 Page 안 쓰고 List로 반환
    @Query("""
            select rr from ReadingRecord rr
            left join fetch rr.book b
            where rr.user.id = :userId
            order by rr.recordedAt desc
            """)
    List<ReadingRecord> findLatestByUser(@Param("userId") Long userId, Pageable pageable);
}

