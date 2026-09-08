package net.likelion.bebc25.sns.exception;
import net.likelion.bebc25.sns.dto.ApiErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

// REST API에서 발생하는 예외를 전역적으로 처리하는 클래스
// 각 Controller에서 예외를 직접 처리하지 않고 이곳에서 한 번에 처리한다.
@RestControllerAdvice
public class GlobalRestExceptionHandler {

    // =========================================================
    // @Valid 유효성 검증 실패
    // HTTP 400 Bad Request
    // =========================================================

    // @Valid 검증에 실패하면 호출된다.
    // @NotNull, @NotBlank, @Size 등의 DTO 제약조건을 위반했을 때 발생한다.

    // @Valid 유효성 검증 실패할 경우 호출됨(400 Bad request 응답)
    // 클라이언트가 전송한 DTO의 제약조건(@NotNull, @NotBlank, @Size 등)을 위반할 경우 스프링이 발생시키는 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex){
        // 현재는 예외 메시지를 그대로 응답함
        return ResponseEntity.status(400).body(ex.getMessage());
    }

    // =========================================================
    // 비즈니스 규칙 위반
    // HTTP 400 Bad Request
    // =========================================================

    // IllegalArgumentException이 발생하면 호출된다.
    // 잘못된 인자나 비즈니스 규칙을 위반한 경우에 사용한다.

    // 비즈니스 업무 규칙 위반 시 호출됨(400 Bad Request 응답)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        // 비즈니스 규칙 위반에 해당하는 공통 에러 응답을 생성함
        ApiErrorResponse response = ApiErrorResponse.of(ErrorCode.BUSINESS_RULE_VIOLATION, ex.getMessage());
        // HTTP 400 Bad Request와 에러 응답 데이터를 반환함
        return ResponseEntity.status(ErrorCode.BUSINESS_RULE_VIOLATION.getHttpStatus()).body(response);
    }

    // =========================================================
    // 요청한 자원을 찾을 수 없음
    // HTTP 404 Not Found
    // =========================================================
    // NoSuchElementException이 발생하면 호출된다.
    // 예: 존재하지 않는 게시글을 조회하려는 경우

    // 요청한 자원이 없을 때(404 Not Found 응답)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
        // 자원을 찾을 수 없음에 해당하는 에러 응답을 생성함
        ApiErrorResponse response = ApiErrorResponse.of(ErrorCode.RESOURCE_NOT_FOUND, ex.getMessage());
        // HTTP 404 Not Found와 에러 응답 데이터를 반환함
        return ResponseEntity.status(ErrorCode.RESOURCE_NOT_FOUND.getHttpStatus()).body(response);
    }


    // =========================================================
    // 권한 부족
    // HTTP 403 Forbidden
    // =========================================================

    // IllegalStateException이 발생하면 호출된다.
    // 예: 다른 사용자의 게시글을 수정하거나 삭제하려는 경우

    // 권한이 부족할 때(403 Forbidden 응답)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        // 권한 부족에 해당하는 에러 응답을 생성함
        ApiErrorResponse response = ApiErrorResponse.of(ErrorCode.FORBIDDEN_OPERATION, ex.getMessage());
        // HTTP 403 Forbidden과 에러 응답 데이터를 반환함
        return ResponseEntity.status(ErrorCode.FORBIDDEN_OPERATION.getHttpStatus()).body(response);
    }


    // =========================================================
    // 그 외 서버 내부 오류
    // HTTP 500 Internal Server Error
    // =========================================================

    // 서버 내부 오류가 발생했을 때 (500 Internal Server Error 응답)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception ex) {
        // 서버 내부 오류에 해당하는 에러 응답을 생성함
        ApiErrorResponse response = ApiErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        // HTTP 500 Internal Server Error와 에러 응답 데이터를 반환함
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus()).body(response);
    }
}

