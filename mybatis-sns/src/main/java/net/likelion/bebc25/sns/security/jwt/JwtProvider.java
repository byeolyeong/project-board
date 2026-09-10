package net.likelion.bebc25.sns.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;


// JWT의 생성과 검증, 토큰 내부 정보(Claims) 추출을 담당하는 클래스
// 쉽게 말하면 JWT를 "만들고, 확인하고, 안에 들어있는 정보를 꺼내는" 역할
@Slf4j
@Component
public class JwtProvider {

    // JWT 서명에 사용하는 비밀키
    private final SecretKey secretKey;
    // Access Token의 유효 기간
    private final long accessTokenExpiration;
    // Refresh Token의 유효 기간
    private final long refreshTokenExpiration;

    // application.yml 등의 설정 파일에서 JWT 관련 설정값을 주입받는다.
    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}")long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}")long refreshTokenExpiration) {

        // Base64로 인코딩된 secret 문자열을 실제 바이트 배열로 디코딩한다.
        byte[] keyBytes = Decoders.BASE64.decode(secret);

        // 디코딩된 비밀키를 HMAC 방식의 JWT 서명용 SecretKey로 변환한다.
        System.out.println(new String(keyBytes));
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);

        // 설정 파일에서 전달받은 Access Token 만료 시간을 저장한다.
        this.accessTokenExpiration = accessTokenExpiration;

        // 설정 파일에서 전달받은 Refresh Token 만료 시간을 저장한다.
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // Access Token 생성 메서드
    // 로그인에 성공한 사용자가 API를 호출할 때 사용할 토큰을 생성한다.
    public String createAccessToken(Long memberId, String email, String role) {

        // 현재 시간을 기준으로 토큰 발급 시간을 기록한다.
        Date now = new Date();

        // 현재 시간 + 설정된 유효 기간으로 토큰 만료 시간을 계산한다.
        Date validity = new Date(now.getTime() + accessTokenExpiration);

        // JWT의 Header + Payload + Signature를 생성한다.
        return Jwts.builder()
                // 어떤 서비스에서 발급한 토큰인지 나타낸다.
                .issuer("mybatis-sns")
                // 토큰의 주체(Subject)에 회원 PK를 저장한다.
                .subject(String.valueOf(memberId))
                // Payload에 사용자의 이메일을 저장한다.
                .claim("email", email)
                // Payload에 사용자의 권한을 저장한다.
                .claim("roles", List.of(role))
                // 토큰이 발급된 시간을 저장한다.
                .issuedAt(now)
                // 토큰이 만료되는 시간을 저장한다.
                .expiration(validity)
                // 비밀키를 이용해 JWT에 서명한다.
                .signWith(secretKey)
                // 최종 JWT 문자열로 변환한다.
                .compact();
    }


    // Refresh Token 생성 메서드 (최소한의 식별 정보만 포함)
    // Access Token이 만료되었을 때 새로운 Access Token을 발급받기 위해 사용한다.
    public String createRefreshToken(Long memberId) {

        // Refresh Token 발급 시간을 기록한다.
        Date now = new Date();

        // 현재 시간 + Refresh Token 유효 기간으로 만료 시간을 계산한다.
        Date validity = new Date(now.getTime() + refreshTokenExpiration);

        // Refresh Token은 Access Token보다 적은 정보만 담아서 생성한다.
        return Jwts.builder()
                // 토큰을 발급한 서비스 이름
                .issuer("mybatis-sns")
                // 사용자를 식별하기 위한 회원 PK
                .subject(String.valueOf(memberId))
                // 토큰 발급 시간
                .issuedAt(now)
                // 토큰 만료 시간
                .expiration(validity)
                // 비밀키를 이용해 토큰에 서명
                .signWith(secretKey)
                // JWT 문자열 생성
                .compact();
    }


    // 토큰 서명 및 만료 유효성 검증
    // 전달받은 JWT가 정상적인 토큰인지 확인한다.
    public boolean validateToken(String token) {
        try {
            // 비밀키로 JWT의 서명을 검증하고 토큰 내용을 파싱한다.
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            // 예외가 발생하지 않았다면 유효한 토큰이다.
            return true;

        } catch (ExpiredJwtException e) {
            // 토큰의 만료 시간이 지난 경우
            log.warn("만료된 JWT 토큰입니다: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            // 서명이 잘못되었거나 JWT 형식이 잘못된 경우
            log.warn("유효하지 않은 JWT 서명 또는 형식입니다: {}", e.getMessage());
        }
        // 검증에 실패한 경우
        return false;
    }

    // 토큰에서 페이로드 Claims 추출
    // JWT 안에 저장된 사용자 정보 등을 꺼낸다.
    public Claims parseClaims(String token) {
        try {
            // JWT의 서명을 검증한 후 Payload(Claims)를 가져온다.
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰이라도 내부 Claims를 가져올 수 있다.
            // Refresh Token을 이용한 갱신 등의 작업에서 활용할 수 있다.
            return e.getClaims(); // 만료된 토큰이라도 클레임 정보는 반환하여 갱신에 활용
        }
    }

    // 토큰의 비공개 클레임에서 사용자 이메일 추출
    public String getEmail(String token) {
        // Claims에서 "email"이라는 이름으로 저장된 값을 String으로 가져온다.
        return parseClaims(token).get("email", String.class);
    }

    // 토큰의 주체(Subject)에서 회원 PK 추출
    public Long getMemberId(String token) {
        // Subject에 저장했던 회원 ID를 Long 타입으로 변환한다.
        return Long.valueOf(parseClaims(token).getSubject());
    }
}