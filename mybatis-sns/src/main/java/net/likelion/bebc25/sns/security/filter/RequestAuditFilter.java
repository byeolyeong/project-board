package net.likelion.bebc25.sns.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


// 모든 HTTP 요청을 기록하는 감사(Audit) Filter
// 요청 방식, URI, 상태 코드, 처리 시간, 접속 IP 등을 로그로 남긴다.
public class RequestAuditFilter extends OncePerRequestFilter {

    // 요청 정보를 로그로 출력하기 위한 Logger
    private static final Logger log = LoggerFactory.getLogger(RequestAuditFilter.class);

    // HTTP 요청이 들어올 때마다 한 번씩 실행된다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 요청 처리 시작 시간 기록
        long startTime = System.currentTimeMillis();
        // 요청한 클라이언트의 IP 주소
        String clientIp = request.getRemoteAddr();
        // HTTP 메서드 확인 (GET, POST, PUT, DELETE 등)
        String method = request.getMethod();
        // 요청 URL 경로 확인
        String uri = request.getRequestURI();
        try {
            // 다음 Filter 또는 Controller로 요청 전달
            filterChain.doFilter(request, response);
        } finally {
            // 요청 처리에 걸린 시간 계산
            long duration = System.currentTimeMillis() - startTime;
            // 최종 HTTP 응답 상태 코드 확인
            int status = response.getStatus();
            // 요청 정보를 로그로 기록
            log.info("[HTTP AUDIT] {} {} | 상태코드: {} | 소요시간: {}ms | 접속IP: {}",
                    method, uri, status, duration, clientIp);
        }
    }
}