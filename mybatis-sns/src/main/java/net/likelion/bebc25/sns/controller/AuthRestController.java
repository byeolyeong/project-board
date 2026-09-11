package net.likelion.bebc25.sns.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.sns.domain.Member;
import net.likelion.bebc25.sns.dto.LoginRequest;
import net.likelion.bebc25.sns.dto.RefreshTokenRequest;
import net.likelion.bebc25.sns.dto.TokenResponse;
import net.likelion.bebc25.sns.mapper.MemberMapper;
import net.likelion.bebc25.sns.security.jwt.JwtProvider;
import net.likelion.bebc25.sns.security.principal.CustomUserDetails;
import net.likelion.bebc25.sns.security.service.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

// 로그인 관련 REST API 요청을 처리하는 Controller
@RestController
// 인증 API의 공통 URL
// 실제 요청 주소: /api/v1/auth/login
@RequestMapping("/api/v1/auth")
// final 필드에 대한 생성자를 Lombok이 자동으로 생성한다.
// → 생성자 의존성 주입(DI)
@RequiredArgsConstructor
public class AuthRestController {

    // 사용자의 로그인 정보를 실제로 인증하는 객체
    private final AuthenticationManager authenticationManager;
    // JWT Access Token과 Refresh Token을 생성하는 객체
    private final JwtProvider jwtProvider;

    // 회원 ID를 이용해 DB에서 회원 정보를 조회하는 Service
    private final CustomUserDetailsService userDetailsService;

    // =========================================================
// 로그인
// POST /api/v1/auth/login
// =========================================================
// 로그인 요청 처리
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {

        // 1. 이메일과 비밀번호로 미인증 Authentication 객체 생성
        Authentication unauthenticatedToken =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());

        // =========================================================
        // 2. 실제 인증 수행
        // =========================================================

        // AuthenticationManager가 DB의 사용자 정보와 비교하여 인증 수행
        // 성공하면 인증된 Authentication 반환, 실패하면 예외 발생
        Authentication authentication = authenticationManager.authenticate(unauthenticatedToken);

        // =========================================================
        // 3. 인증된 사용자 정보 가져오기
        // =========================================================

        // 인증된 Authentication에서 CustomUserDetails 추출
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 인증된 회원의 ID, 이메일, 권한 정보 추출
        Long memberId = userDetails.getMember().getId();
        String email = userDetails.getUsername();
        String role = userDetails.getMember().getRole();

        // =========================================================
        // 4. JWT 토큰 생성
        // =========================================================

        // 회원 정보를 이용해 Access Token 생성
        String accessToken = jwtProvider.createAccessToken(memberId, email, role);

        // 회원 ID를 이용해 Refresh Token 생성
        String refreshToken = jwtProvider.createRefreshToken(memberId);

        // =========================================================
        // 5. 토큰 응답 생성 및 반환
        // =========================================================

        // 발급된 토큰을 TokenResponse DTO에 담는다.
        // Access Token 유효기간: 3600초(1시간)
        TokenResponse response = TokenResponse.of(accessToken, refreshToken, 3600L);

        // 로그인 성공 → HTTP 200 OK
        return ResponseEntity.ok(response);
    }

    // Refresh Token 기반 Access Token 갱신 엔드포인트
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {

        // 요청으로 전달받은 Refresh Token 추출
        String refreshToken = request.refreshToken();

        // 1. Refresh Token의 서명과 만료 여부 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("유효하지 않거나 만료된 Refresh Token입니다.");
        }

        // 2. Refresh Token에서 회원 PK 추출
        Long memberId = jwtProvider.getMemberId(refreshToken);

        // 3. 회원 ID를 이용해 DB에서 회원 정보 조회
        CustomUserDetails userDetails = (CustomUserDetails)userDetailsService.loadUserById(memberId);

        // UserDetails에서 실제 Member 객체 추출
        Member member = userDetails.getMember();

        // 4. 새로운 Access Token 및 Refresh Token 발급 (RTR 전략 적용)
        String newAccessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(), member.getRole());
        String newRefreshToken = jwtProvider.createRefreshToken(member.getId());

        // 새로 발급한 토큰을 응답 DTO에 담는다.
        TokenResponse response = TokenResponse.of(newAccessToken, newRefreshToken, 3600L);

        // 토큰 갱신 성공 → HTTP 200 OK
        return ResponseEntity.ok(response);
    }
}