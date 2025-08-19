package me.dodo.readingnotes.external;

import me.dodo.readingnotes.dto.book.BookCandidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class KakaoBookClient implements BookSearchClient {
    private static final Logger log = LoggerFactory.getLogger(KakaoBookClient.class);
    private final WebClient webClient;

    @Value("${external.kakao.book.search-path:/v3/search/book}")
    private String searchPath;

    public KakaoBookClient(@Qualifier("kakaoBookWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<BookCandidate> search(String rawTitle, String rawAuthor, int limit) {
        String query = (rawAuthor == null || rawAuthor.isBlank())
                ? "\"" + rawTitle + "\""
                : "\"" + rawTitle + "\" \"" + rawAuthor + "\"";

        // 카카오 API size 허용 범위를 1~50개로.
        int size = Math.min(Math.max(limit, 1), 50);

        // 책 검색
        KakaoResponse resp = webClient.get()
                .uri(uri -> uri.path(searchPath)
                        .queryParam("query", query)
                        .queryParam("size", size)
                        .queryParam("sort", "accuracy") // 정확도순으로 정렬
                        .build())
                .retrieve()
                // 4xx/5xx 응답을 에러로 변환함
                .onStatus(status -> status.isError(), r ->
                        r.bodyToMono(String.class).map(body ->
                                new RuntimeException("Kakao API error: " +  r.statusCode()  + " - " + body)))
                .bodyToMono(KakaoResponse.class)
                .block();

        if (resp == null || resp.documents == null) return List.of();

        log.debug("Kakao API response: {}", resp.documents.toString());

        // BookCandidate로 변환
        return resp.documents.stream().map(this::toCandidate).collect(Collectors.toList());
    }

    private BookCandidate toCandidate(Document d) {
        BookCandidate c = new BookCandidate();
        c.setSource(getSource());
        c.setTitle(d.title);
        c.setAuthor(joinAuthor(d.authors));
        // externalId: 카카오는 별도 ID가 없어서 url 또는 isbn13 활용
        c.setExternalId(d.url != null ? d.url : d.isbn);

        // 길이에 따라 ISBN10/ISBN13을 분리 저장
        if (d.isbn != null && !d.isbn.isBlank()) {
            String[] parts = d.isbn.trim().split("\\s+");
            if (parts.length == 2) {
                if (parts[0].length() == 10) c.setIsbn10(parts[0]);
                if (parts[1].length() == 13) c.setIsbn13(parts[1]);
            } else if (parts.length == 1) {
                if (parts[0].length() == 13) c.setIsbn13(parts[0]);
                else if (parts[0].length() == 10) c.setIsbn10(parts[0]);
            }
        }
        c.setPublisher(d.publisher);
        // datetime은 날짜만 잘라 LocalDate로 파싱함
        c.setPublishedDate(parseDate(d.datetime));
        c.setThumbnailUrl(d.thumbnail);
        c.setScore(0.0);

        log.debug("kakaoBook BookCandidate: {}", c.toString());
        return c;
    }

    // author 리스트를 "A, B"로 합치기 (null/빈 처리 포함)
    private static String joinAuthor(List<String> author) {
        if (author == null || author.isEmpty()) return "";
        return author.stream()
                .filter(a -> a != null && !a.trim().isEmpty())
                .collect(Collectors.joining(", "));
    }

    // 날짜 파싱("2020-01-02T10:20:30.000+09:00" 같은 문자열을 LocalDate(앞 10자리)로 파싱함.)
    private LocalDate parseDate(String iso) {
        try { return (iso != null && iso.length() >= 10) ? LocalDate.parse(iso.substring(0, 10)) : null; }
        catch (Exception e) { return null; }
    }

    @Override public String getSource() { return "KAKAO"; }

    // 내부 응답 DTO
    static class KakaoResponse {
        public Meta meta;
        public List<Document> documents;
    }
    static class Meta {
        public boolean is_end;
        public int pageable_count;
        public int total_count;
    }
    static class Document {
        public String title;
        public String contents;
        public String url;
        public String isbn;
        public String datetime;
        // 카카오는 authors로 줌.
        public List<String> authors;
        public String publisher;
        public String[] translators;
        public Integer price;
        public Integer sale_price;
        public String thumbnail;
        public String status;

        @Override
        public String toString() {
            return "Document{title='" + title + "', authors=" + authors + ", isbn='" + isbn + "'}";
        }
    }
}
