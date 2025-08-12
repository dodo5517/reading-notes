package me.dodo.readingnotes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class KakaoBookConfig {

    @Value("${external.kakao.book.base-url}")
    private String baseUrl;

    @Value("${external.kakao.book.rest-api-key}")
    private String apiKey;

    // 스프링 컨테이너에 WebClient 빈을 등록함
    // 나중에 @Qualifier("kakaoBookWebClient") 사용하여 이 빈을 주입할 수 있음.
    @Bean(name = "kakaoBookWebClient")
    public WebClient kakaoBookWebClient() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Kakao Book REST API 키가 비어있습니다.");
        }
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + apiKey)
                .build();
    }
}
