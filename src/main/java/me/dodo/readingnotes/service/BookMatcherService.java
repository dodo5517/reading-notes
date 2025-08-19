package me.dodo.readingnotes.service;

import me.dodo.readingnotes.dto.book.BookCandidate;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class BookMatcherService {

    // 이 점수 이상이면 자동 매칭(1.0 = 동일문자)
    private static final double AUTO_MATCH_THRESHOLD = 0.88;
    // 제목, 작가의 가중치
    private static final double TITLE_WEIGHT = 0.7;
    private static final double AUTHOR_WEIGHT = 0.3;

    // 제목 부제 제거용 패턴: 괄호/대괄호 안 텍스트 제거
    private static final Pattern PAREN_OR_BRACKET = Pattern.compile("[\\(\\[].*?[\\)\\]]");
    // 제목 꼬리 제거: 콜론/대시 뒤의 부제 제거(예: "제목: 부제", "제목 - 부제")
    private static final Pattern TITLE_TRAILER = Pattern.compile("\\s*[:：\\-|–—]\\s*.*$");

    // 작가 토큰정리 시 불용어(끝에 붙는 역할어 등)
    private static final Set<String> AUTHOR_STOPWORDS =
            Set.of("외", "지음", "저", "역", "엮음", "옮김", "편");

    // 문자열 유사도 알고리즘
    private final JaroWinklerSimilarity sim = new JaroWinklerSimilarity();

    public MatchResult pickBest(String rawTitle, String rawAuthor, List<BookCandidate> candidates){
        // 제목, 작가를 정규화함.
        // NFC + 부제/꼬리 제거 + 공백/구두점 정리
        String nt = normTitle(rawTitle);
        String na = normAuthorField(rawAuthor);


        // 최고 점수의 후보와 그 점수
        BookCandidate best = null;
        double bestScore = -1;

        // 후보 하나씩 처리
        for (BookCandidate c : candidates) {
            // 후보 대상인 책의 제목,작가를 정규화함.
            String ct = normTitle(c.getTitle());
            String ca = normAuthorField(c.getAuthor());

            // 강매칭(제목=완전일치 + 작가=포함 일 때)
            if (ct.equals(nt) && authorOverlap(ca, na)) {
                // 만족하는 게 있으면 바로 채택
                return new MatchResult(c, 1.0, true);
            }

            // 강매칭 통과 대상이 없으면 유사도를 점수화함.
            double titleScore  = nonNull(sim.apply(nt, ct));
            double authorScore = nonNull(sim.apply(na, ca));
            // 제목의 가중치를 더 높게 함.
            double score = (titleScore * TITLE_WEIGHT) + (authorScore * AUTHOR_WEIGHT);

            // 최고 점수인 책을 저장
            if (score > bestScore) {
                bestScore = score;
                best = c;
            }
        }

        // 최저점 못 넘는 경우 매칭X
        if (best == null) return new MatchResult(null, 0.0, false);

        // 최고 점수의 후보가 임계점 넘었으면 자동 매칭 확정
        boolean auto = bestScore >= AUTO_MATCH_THRESHOLD;
        return new MatchResult(best, bestScore, auto);

    }

    // 정규화
    private static String normTitle(String s) {
        if (s == null) return "";
        String t = stripSubtitles(s);     // ()나 [] 안의 부제 제거
        t = stripTitleTrailer(t);         // ":"나 "-" 뒤 꼬리 제거
        return baseNorm(t);               // NFC + 소문자 + 구두점/공백 정리
    }
    private static String normAuthorField(String s) {
        if (s == null) return "";
        // 작가 필드는 부제 제거 없이 기본 정규화만
        return baseNorm(s);
    }
    private static String baseNorm(String s) {
        // 한국어 유지: NFC (NFKD는 자모 분해되어 유사도 저하 위험)
        String t = Normalizer.normalize(s, Normalizer.Form.NFC)
                .toLowerCase(Locale.ROOT);
        // 구두점→공백 치환 후, 다중 공백 축약
        t = t.replaceAll("\\p{Punct}+", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return t;
    }
    private static String stripSubtitles(String s) {
        String t = PAREN_OR_BRACKET.matcher(s).replaceAll(" ");
        return t.replaceAll("\\s+", " ").trim();
    }
    private static String stripTitleTrailer(String s) {
        return TITLE_TRAILER.matcher(s).replaceAll("").trim();
    }

    // 작가 토큰 교집합
    private static boolean authorOverlap(String a, String b) {
        if (a.isEmpty() || b.isEmpty()) return false;
        Set<String> as = authorTokens(a);
        Set<String> bs = authorTokens(b);
        as.retainAll(bs);
        return !as.isEmpty();
    }
    private static Set<String> authorTokens(String s) {
        // 기본 구분자: 콤마, 슬래시, 중점(·), &, 세미콜론
        String[] raw = s.split("\\s*[,/·&;]\\s*");
        Set<String> out = new HashSet<>();
        for (String token : raw) {
            token = token.trim();
            if (token.isEmpty()) continue;

            // 괄호 내 역할 표기 제거(예: "남궁성(지음)")
            token = PAREN_OR_BRACKET.matcher(token).replaceAll("").trim();

            // 끝에 붙는 역할어 제거
            for (String stop : AUTHOR_STOPWORDS) {
                token = token.replaceAll("\\s*" + stop + "$", "").trim();
            }
            if (token.isEmpty()) continue;

            out.add(token);
        }
        return out;
    }
    private static double nonNull(Double d) { return d == null ? 0.0 : d; }

    // 매칭 결과 객체
    public static class MatchResult {
        public final BookCandidate best;
        public final double score;
        public final boolean autoMatch;
        public MatchResult(BookCandidate best, double score, boolean autoMatch) {
            this.best = best;
            this.score = score;
            this.autoMatch = autoMatch;
        }
    }
}
