package me.dodo.readingnotes.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import me.dodo.readingnotes.domain.User;
import me.dodo.readingnotes.repository.UserRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

public class ApiKeyFilter extends OncePerRequestFilter {

    public static final String ATTR_API_USER_ID = "apiUserId";
    public static final String HEADER_API_KEY = "X-Api-Key";

    private final UserRepository userRepository;
    // 적용할 URL prefix(들). 예: /api/records 로 들어오는 요청에만 API Key를 요구
    private final String[] protectedPrefixes;

    public ApiKeyFilter(UserRepository userRepository, String... protectedPrefixes) {
        this.userRepository = userRepository;
        this.protectedPrefixes = protectedPrefixes != null ? protectedPrefixes : new String[0];
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (protectedPrefixes.length == 0) return true; // 보호 경로가 없으면 필터 비활성화
        for (String prefix : protectedPrefixes) {
            if (path.startsWith(prefix)) {
                return false; // 보호 경로면 필터 동작
            }
        }
        return true; // 그 외 경로는 필터 패스
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String apiKey = request.getHeader(HEADER_API_KEY);

        if (apiKey == null || apiKey.isBlank()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "X-Api-Key가 누락되었습니다.");
            return;
        }

        Optional<User> userOpt = userRepository.findByApiKey(apiKey);
        if (userOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "잘못된 API Key 입니다.");
            return;
        }

        request.setAttribute(ATTR_API_USER_ID, userOpt.get().getId());
        chain.doFilter(request, response);
    }
}