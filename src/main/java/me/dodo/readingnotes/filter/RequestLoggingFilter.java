package me.dodo.readingnotes.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import me.dodo.readingnotes.dto.CachedBodyHttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if ("POST".equalsIgnoreCase(httpRequest.getMethod())
                && httpRequest.getContentType() != null
                && httpRequest.getContentType().contains("application/json")) {

            // 요청 바디를 여러 번 읽을 수 있도록 래핑
            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(httpRequest);

            String body = new BufferedReader(cachedRequest.getReader())
                    .lines()
                    .collect(Collectors.joining("\n"));

            System.out.println("📩 Raw JSON 요청 바디:\n" + body);

            chain.doFilter(cachedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}

