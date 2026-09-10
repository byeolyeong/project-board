package net.likelion.bebc25.sns.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.sns.dto.ApiErrorResponse;
import net.likelion.bebc25.sns.exception.ErrorCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


// 인증되지 않은 사용자가 보호된 API에 접근했을 때 처리하는 Handler
// 예: 로그인하지 않은 사용자가 게시글 작성 API에 접근 → 401 Unauthorized
@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Java 객체(ApiErrorResponse)를 JSON 문자열로 변환하기 위한 객체
    // LocalDateTime을 JSON으로 변환할 수 있도록 JavaTimeModule을 추가한다.
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    // 인증되지 않은 사용자가 보호된 API에 접근했을 때 호출되는 메서드
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 어떤 URI에서 인증 실패가 발생했는지 로그로 기록한다.
        log.warn("인증 실패 예외 발생: URI={}, 사유={}", request.getRequestURI(), authException.getMessage());

        // 인증 실패에 해당하는 공통 에러 응답 객체 생성
        // ErrorCode.UNAUTHORIZED_ACCESS → HTTP 401
        ApiErrorResponse errorResponse = ApiErrorResponse.of(ErrorCode.UNAUTHORIZED_ACCESS);

        // HTTP 응답 상태 코드를 401로 설정한다.
        response.setStatus(errorResponse.status());

        // 응답 데이터가 JSON 형식임을 설정한다.
        // UTF-8을 사용하여 한글이 깨지지 않도록 한다.
        response.setContentType("application/json;charset=UTF-8");

        // ApiErrorResponse 객체를 JSON 문자열로 변환하여 응답 Body에 작성한다.
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

        // 작성한 응답 내용을 클라이언트로 전송한다.
        response.getWriter().flush();
    }
}