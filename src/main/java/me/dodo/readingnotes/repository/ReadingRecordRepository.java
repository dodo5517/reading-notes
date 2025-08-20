package me.dodo.readingnotes.repository;

import me.dodo.readingnotes.domain.ReadingRecord;
import me.dodo.readingnotes.dto.book.BookWithLastRecordResponse;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {
    // 필요하면 여기에 커스텀 쿼리도 작성

    // 해당 유저의 모든 기록을 페이지 단위로 가져옴
    // Page라서 전체 개수(totalElements), 전체 페이지 수(totalPages), 현재 페이지 번호(pageNumber) 포함함.
    // N+1 줄이기 위해서 book을 EntityGraph로 로딩함.
    @EntityGraph(attributePaths = "book")
    Page<ReadingRecord> findByUser_IdOrderByRecordedAtDesc(Long userId, Pageable pageable);

    // 해당 유저의 특정 책 찾기
    @Query("""
        select r
          from ReadingRecord r
         where r.user.id = :userId
           and r.book.id = :bookId
           and ( :cursorAt is null
                 or r.recordedAt < :cursorAt
                 or (r.recordedAt = :cursorAt and r.id < :cursorId) )
         order by r.recordedAt desc, r.id desc
    """)
    List<ReadingRecord> findSliceByUserAndBookWithCursor(
            Long userId,
            Long bookId,
            LocalDateTime cursorAt,
            Long cursorId,
            Pageable pageable
    );
    // 기간 계산(해당 유저의 해당 책 기록 중 가장 과거/가장 최근)
    @Query("""
        select min(r.recordedAt)
          from ReadingRecord r
         where r.user.id = :userId
           and r.book.id = :bookId
    """)
    LocalDateTime findMinRecordedAtByUserAndBook(Long userId, Long bookId);
    @Query("""
        select max(r.recordedAt)
          from ReadingRecord r
         where r.user.id = :userId
           and r.book.id = :bookId
    """)
    LocalDateTime findMaxRecordedAtByUserAndBook(Long userId, Long bookId);

    // 해당 유저의 기록 중 최신 N개만 가져옴,  count 쿼리 없음.
    // 페이지네이션 필요 없으니 굳이 Page 안 쓰고 List로 반환
    @Query("""
            select rr from ReadingRecord rr
            left join fetch rr.book b
            where rr.user.id = :userId
            order by rr.recordedAt desc
            """)
    List<ReadingRecord> findLatestByUser(@Param("userId") Long userId, Pageable pageable);

    // 해당 유저가 기록한 책 중에서 매칭이 끝난 책만 가져옴.
    // 최근 기록순
    @Query("""
        select new me.dodo.readingnotes.dto.book.BookWithLastRecordResponse(
            b.id, b.title, b.author, b.isbn10, b.isbn13, b.coverUrl, max(r.recordedAt)
        )
        from ReadingRecord r join r.book b
        where r.user.id = :userId
          and r.matchStatus in (me.dodo.readingnotes.domain.ReadingRecord.MatchStatus.RESOLVED_AUTO,
                                me.dodo.readingnotes.domain.ReadingRecord.MatchStatus.RESOLVED_MANUAL)
          and (:q is null
               or lower(b.title) like lower(concat('%', :q, '%'))
               or lower(b.author) like lower(concat('%', :q, '%')))
        group by b.id, b.title, b.author, b.isbn10, b.isbn13, b.coverUrl
        order by max(r.recordedAt) desc
        """)
    Page<BookWithLastRecordResponse> findConfirmedBooksByRecent(Long userId, String q, Pageable pageable);
    // 제목순
    @Query("""
select new me.dodo.readingnotes.dto.book.BookWithLastRecordResponse(
    b.id, b.title, b.author, b.isbn10, b.isbn13, b.coverUrl, max(r.recordedAt)
)
from ReadingRecord r join r.book b
where r.user.id = :userId
  and r.matchStatus in (me.dodo.readingnotes.domain.ReadingRecord.MatchStatus.RESOLVED_AUTO,
                        me.dodo.readingnotes.domain.ReadingRecord.MatchStatus.RESOLVED_MANUAL)
  and (:q is null
       or lower(b.title) like lower(concat('%', :q, '%'))
       or lower(b.author) like lower(concat('%', :q, '%')))
group by b.id, b.title, b.author, b.isbn10, b.isbn13, b.coverUrl
order by b.title asc
""")
    Page<BookWithLastRecordResponse> findConfirmedBooksByTitle(Long userId, String q, Pageable pageable);
}


