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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


// 인증은 되었지만 권한이 부족한 사용자의 요청을 처리하는 Handler
// 예: 일반 사용자가 관리자 전용 API에 접근 → 403 Forbidden
@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    // Java 객체(ApiErrorResponse)를 JSON 문자열로 변환하기 위한 객체
    // LocalDateTime도 JSON으로 변환할 수 있도록 JavaTimeModule을 추가한다.
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    // 권한 부족(403 Forbidden) 상황이 발생했을 때 호출되는 메서드
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        // 어떤 URI에서 권한 부족이 발생했는지 로그로 기록한다.
        log.warn("권한 부족 예외 발생: URI={}, 사유={}", request.getRequestURI(), accessDeniedException.getMessage());

        // 권한 부족에 해당하는 공통 에러 응답 객체 생성
        // ErrorCode.FORBIDDEN_OPERATION → HTTP 403
        ApiErrorResponse errorResponse = ApiErrorResponse.of(ErrorCode.FORBIDDEN_OPERATION);

        // HTTP 응답 상태 코드를 403으로 설정한다.
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