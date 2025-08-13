package me.dodo.readingnotes.service;

import me.dodo.readingnotes.dto.BookCandidate;
import me.dodo.readingnotes.external.BookSearchClient;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookCandidateService {
    private static final Logger log = LoggerFactory.getLogger(BookCandidateService.class);

    private final List<BookSearchClient> clients;

    public BookCandidateService(List<BookSearchClient> clients) {
        this.clients = clients;
    }

    public List<BookCandidate> findCandidates(String rawTitle, String rawAuthor, int limit) {
        // 호출 결과들을 하나의 리스트로 합침 + 평탄화 작업
        List<BookCandidate> merged = clients.stream()
                .flatMap(c -> c.search(rawTitle, rawAuthor, limit).stream())
                .collect(Collectors.toList());

        // 점수화(제목 0.7, 저자 0.3)
        for (BookCandidate c : merged) {
            // 정규화(소문자로 통일, 공백과 문장부호는 제거)
            // 레벤슈타인 거리 알고리즘 사용
            double t = similarity(norm(rawTitle), norm(c.getTitle()));
            double a = similarity(norm(rawAuthor), norm(c.getAuthor()));
            // 제목을 0.7로 더 중요하게 반영함.
            c.setScore(0.7 * t + 0.3 * a);
        }

        log.debug(merged.toString());

        // 점수가 높은 순으로 정렬
        return merged.stream()
                .sorted(Comparator.comparingDouble(BookCandidate::getScore).reversed())
                // 최대 반환 개수는 20개로 제한
                .limit(Math.min(limit, 20))
                .collect(Collectors.toList());
    }

    // 소문자화, 문자부호/공백 제거
    private String norm(String s) {
        if (s == null) return "";
        return s.toLowerCase().replaceAll("[\\p{Punct}\\s]+", "");
    }

    // 레벤슈타인 거리 알고리즘 사용
    private double similarity(String a, String b) {
        if (a.isEmpty() || b.isEmpty()) return 0d;
        int dist = LevenshteinDistance.getDefaultInstance().apply(a, b);
        int max = Math.max(a.length(), b.length());
        return 1.0 - ((double) dist / (double) max);
    }
}
