package net.likelion.bebc25.sns.dto;

// 로그인 요청 데이터를 전달하기 위한 DTO
// 클라이언트가 로그인할 때 이메일과 비밀번호를 전달한다.
public record LoginRequest(
        String email,
        String password
) {
}
