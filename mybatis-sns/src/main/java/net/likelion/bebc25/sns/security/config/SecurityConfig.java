package net.likelion.bebc25.sns.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

// Spring Security의 전체 보안 설정을 담당하는 Configuration 클래스
@Configuration
// Spring Security의 웹 보안 기능을 활성화함
@EnableWebSecurity
public class SecurityConfig {
    // Spring Security의 보안 필터 체인을 Bean으로 등록함
    // HTTP 요청이 들어오면 SecurityFilterChain을 거쳐 인증/인가 여부를 검사함
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // =========================================================
                // CSRF 설정
                // =========================================================

                // CSRF(Cross-Site Request Forgery) 공격 방어 기능을 비활성화함
                // 현재 API는 Stateless 방식으로 동작하며,
                // 학습 단계에서는 CSRF를 사용하지 않도록 설정함
                // CSRF 공격 방어 기능 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // =========================================================
                // 인증 방식 설정
                // =========================================================

                // HTTP Basic 인증 방식을 활성화함
                // 요청 Header의 Authorization에 인증 정보를 전달하는 방식
                // HTTP 기본 인증 활성화
                .httpBasic(Customizer.withDefaults())

                // 기본 로그인 페이지(Form Login)는 사용하지 않음
                // REST API에서는 HTML 로그인 페이지 대신 API 방식으로 인증을 처리함
                // 기본 폼 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable)

                // =========================================================
                // 세션 설정
                // =========================================================

                // 서버에서 로그인 상태를 Session에 저장하지 않음
                // 각 요청마다 인증 정보를 전달하는 Stateless 방식으로 동작함
                // 세션 생성 및 보관 비활성화
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // =========================================================
                // URL별 접근 권한 설정
                // =========================================================

                // HTTP 요청 URL과 사용자의 권한에 따라 접근을 허용하거나 차단함
                // URL 엔드포인트별 기본 접근 인가 설정
                .authorizeHttpRequests(auth -> auth
                        // 로그인/회원가입 API와 Swagger 문서는 인증 없이 접근할 수 있다.
                        // 예: /api/v1/auth/login
                        //     /api/v1/auth/signup
                        //     /swagger-ui/index.html
                        .requestMatchers("/api/v1/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 게시글 목록 조회는 인증 없이 허용한다.
                        // GET /api/v1/posts
                        .requestMatchers(HttpMethod.GET,"/api/v1/posts").permitAll()
                        // 게시글 상세 조회도 인증 없이 허용한다.
                        // GET /api/v1/posts/{id}
                        .requestMatchers(HttpMethod.GET,"/api/v1/posts/**").permitAll()
                        // 관리자 API는 ADMIN 권한을 가진 사용자만 접근할 수 있다.
                        // hasRole("ADMIN")은 내부적으로 ROLE_ADMIN 권한을 확인한다.
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // ROLE_ADMIN 체크
                        // 위에서 명시적으로 허용하지 않은 모든 요청은
                        // 인증된 사용자만 접근할 수 있도록 설정한다.
                        .anyRequest().authenticated()
                );

        // 지금까지 설정한 내용을 기반으로 SecurityFilterChain을 생성하여 반환한다.
        return http.build();
    }
}