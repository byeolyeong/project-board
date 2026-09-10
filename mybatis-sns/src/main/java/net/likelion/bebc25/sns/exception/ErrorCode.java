package net.likelion.bebc25.sns.exception;

import org.springframework.http.HttpStatus;

// 비즈니스 로직에서 발생할 수 있는 에러 종류를 정의하는 Enum
// 각 에러마다 에러 코드, 메시지, HTTP 상태 코드를 함께 관리함
// 비즈니스 에러 코드 열거형 Enum
public enum ErrorCode {

    INVALID_INPUT_VALUE("INVALID_INPUT_VALUE", "입력값 검증에 실패했습니다.", HttpStatus.BAD_REQUEST),
    BUSINESS_RULE_VIOLATION("BUSINESS_RULE_VIOLATION", "비즈니스 업무 규칙을 위반했습니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS", "인증이 필요하거나 유효하지 않은 자격 증명입니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_OPERATION("FORBIDDEN_OPERATION", "해당 작업을 수행할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "요청한 자원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    // 에러를 구분하기 위한 문자열 코드
    private final String code;
    // 사용자에게 보여줄 에러 메시지
    private final String message;
    // 해당 에러에 대응하는 HTTP 상태 코드
    private final HttpStatus httpStatus;

    // Enum의 각 에러 항목을 생성할 때
    // 에러 코드, 메시지, HTTP 상태 코드를 저장함
    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    // 에러 코드 반환
    public String getCode() {
        return code;
    }

    // 에러 메시지 반환
    public String getMessage() {
        return message;
    }

    // HTTP 상태 코드 반환
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}