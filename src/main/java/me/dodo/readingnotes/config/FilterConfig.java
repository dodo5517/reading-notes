package me.dodo.readingnotes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Value("${app.api-key}")
    private String expectedApiKey;

    @Bean
    public FilterRegistrationBean<ApiKeyFilter> apiKeyFilter() {
        // 직접 생성자 호출해서 객체 만듦
        ApiKeyFilter filter = new ApiKeyFilter(expectedApiKey);

        FilterRegistrationBean<ApiKeyFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/records/*"); // 보호할 URL 패턴
        registration.setOrder(1); // 필터 순서
        return registration;
    }
}