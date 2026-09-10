package net.likelion.bebc25.sns.dto;

// 로그인 성공 후 발급된 JWT 토큰 정보를 응답하기 위한 DTO
// 클라이언트에게 Access Token과 Refresh Token 등의 정보를 전달한다.
public record TokenResponse(
        // API 요청을 인증할 때 사용하는 Access Token
        String accessToken,
        // Access Token이 만료되었을 때
        // 새로운 Access Token을 발급받기 위해 사용하는 Refresh Token
        String refreshToken,
        // Access Token을 사용할 때 앞에 붙이는 인증 방식
        // 현재 프로젝트에서는 "Bearer" 방식을 사용한다.
        String tokenType,
        // Access Token의 유효 시간
        // 단위: 초
        Long expiresIn
) {
    // Access Token과 Refresh Token을 이용해
    // TokenResponse 객체를 생성하는 정적 팩토리 메서드
    public static TokenResponse of(String accessToken, String refreshToken, Long expiresIn){
        // tokenType은 현재 프로젝트에서 사용하는
        // Bearer 인증 방식으로 고정한다.
        return  new TokenResponse(accessToken, refreshToken, "Bearer", expiresIn);
    }
}

// 토큰(Token) : 내가 인증받은 사용자라는 것을 증명하기 위해 서버가 발급해주는 문자열
// accessToken : 실제 API 요청을 인증할 때 사용하는 토큰
// refreshToken : Access Token이 만료되었을 때 새 Access Token을 받기 위한 토큰
// tokenType : 토큰 사용 방식. 현재 "Bearer"
// expiresIn : Access Token이 몇 초 동안 유효한지