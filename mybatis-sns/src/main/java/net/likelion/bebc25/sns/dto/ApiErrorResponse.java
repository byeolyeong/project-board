package net.likelion.bebc25.sns.dto;

import net.likelion.bebc25.sns.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;

// REST API 공통 에러 응답 Record
public record ApiErrorResponse(
        String code,                  // 시스템 내부 비즈니스 예외 코드
        String message,               // 클라이언트 및 사용자용 에러 설명 메시지
        int status,                   // HTTP 응답 상태 코드 (예: 400, 403, 404, 500)
        LocalDateTime timestamp,      // 예외 발생 일시
        List<FieldErrorDetail> errors // Bean Validation 유효성 검증 실패 상세 목록
) {
    // 필드별 유효성 검증 실패 정보 저장 Record
    public record FieldErrorDetail(
            String field,             // 검증 실패 대상 필드명
            String rejectedValue,     // 클라이언트가 전송하여 거부된 입력값
            String reason             // 검증 실패 사유
    ) {}


    // ErrorCode를 이용해 기본적인 에러 응답을 생성하는 정적 팩토리 메서드
    // ErrorCode에 정의된 코드, 메시지, HTTP 상태 코드를 그대로 사용한다.
    // ErrorCode 기반 정적 팩토리 메서드
    public static ApiErrorResponse of(ErrorCode errorCode) {
        return new ApiErrorResponse(
                errorCode.getCode(),                // 에러 코드
                errorCode.getMessage(),             // 기본 에러 메세지
                errorCode.getHttpStatus().value(),  // HTTP 상태 코드 (예: 400, 404, 500)
                LocalDateTime.now(),                // 현재 시간
                List.of()                           // 상세 에러 목록 (없음)
        );
    }


    // ErrorCode와 별도의 에러 메시지를 이용해 에러 응답을 생성하는 정적 팩토리 메서드
    // ErrorCode의 기본 메시지 대신 전달받은 message를 사용한다.
    // ErrorCode 및 예외 세부 메시지 기반 정적 팩토리 메서드
    public static ApiErrorResponse of(ErrorCode errorCode, String message) {
        return new ApiErrorResponse(
                errorCode.getCode(),                // 에러 코드
                message,                            // 전달받은 상세 에러 메시지
                errorCode.getHttpStatus().value(),  // HTTP 상태 코드
                LocalDateTime.now(),                // 현재 시간
                List.of()                           // 상세 에러 목록 (없음)
        );
    }


    // ErrorCode와 필드 유효성 검증 실패 목록을 이용해 에러 응답을 생성하는 정적 팩토리 메서드
    // 여러 입력 필드에서 발생한 validation 오류를 errors에 담아 반환한다.
    // ErrorCode 및 필드 유효성 검증 실패 목록 기반 정적 팩토리 메서드
    public static ApiErrorResponse of(ErrorCode errorCode, List<FieldErrorDetail> errors) {
        return new ApiErrorResponse(
                errorCode.getCode(),                // 에러 코드
                errorCode.getMessage(),             // 기본 에러 메시지
                errorCode.getHttpStatus().value(),  // HTTP 상태 코드
                LocalDateTime.now(),                // 현재 시간
                errors                              // 필드별 validation 오류 목록
        );
    }
}