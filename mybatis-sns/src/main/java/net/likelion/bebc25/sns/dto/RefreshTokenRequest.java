package net.likelion.bebc25.sns.dto;

import jakarta.validation.constraints.NotBlank;

// Refresh Token 갱신 요청에 사용하는 DTO
public record RefreshTokenRequest(

        // Refresh Token이 비어 있으면 요청을 거부한다.
        @NotBlank(message = "Refresh Token은 필수입니다.")
        String refreshToken

) {}