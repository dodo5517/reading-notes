package me.dodo.readingnotes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // H2나 Postman 테스트용
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // H2 콘솔 띄우기 위해
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/**").permitAll()
                ); // 모든 요청 허용
        return http.build();
    }
}
