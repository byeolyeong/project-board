package net.likelion.bebc25.sns.controller;

import lombok.RequiredArgsConstructor;
import net.likelion.bebc25.sns.dto.LoginRequest;
import net.likelion.bebc25.sns.dto.TokenResponse;
import net.likelion.bebc25.sns.security.jwt.JwtProvider;
import net.likelion.bebc25.sns.security.principal.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // =========================================================
    // 로그인
    // POST /api/v1/auth/login
    // =========================================================
    // 로그인 요청 처리
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        // 클라이언트가 입력한 이메일과 비밀번호를 이용해
        // 아직 인증되지 않은 Authentication 객체를 생성한다.
        //
        // 여기서는 "이 사람이 누구인지" 아직 확인하지 않았다.
        // 1. 클라이언트가 입력한 이메일과 비밀번호로 미인증 토큰 생성
        Authentication unauthenticatedToken =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());

        // =========================================================
        // 2. 실제 인증 수행
        // =========================================================

        // AuthenticationManager에게 인증을 요청한다.
        //
        // AuthenticationManager는 전달받은 이메일과 비밀번호를
        // UserDetailsService 등을 이용하여 DB의 사용자 정보와 비교한다.
        //
        // 인증에 성공하면 인증된 Authentication 객체를 반환하고,
        // 실패하면 인증 예외가 발생한다.
        // 2. AuthenticationManager를 통한 인증 검증 위임
        Authentication authentication = authenticationManager.authenticate(unauthenticatedToken);

        // =========================================================
        // 3. 인증된 사용자 정보 가져오기
        // =========================================================

        // 인증에 성공한 Authentication에서
        // 실제 사용자 정보인 Principal을 가져온다.
        //
        // 현재 프로젝트에서는 Principal로
        // CustomUserDetails를 사용한다.
        // 3. 인증된 Principal로부터 회원 상세 정보 추출
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        // 인증된 회원의 ID를 가져온다.
        Long memberId = userDetails.getMember().getId();
        // 로그인 식별자인 이메일을 가져온다.
        String email = userDetails.getUsername();
        // 회원의 권한을 가져온다.
        // 예: ROLE_USER, ROLE_ADMIN
        String role = userDetails.getMember().getRole();

        // =========================================================
        // 4. JWT 토큰 생성
        // =========================================================

        // 회원 ID, 이메일, 권한 정보를 이용해
        // API 요청에 사용할 Access Token을 생성한다.
        // 4. JWT 토큰 생성
        String accessToken = jwtProvider.createAccessToken(memberId, email, role);
        // 회원 ID를 이용해 Refresh Token을 생성한다.
        // Access Token이 만료되었을 때 새로운 Access Token을
        // 발급받는 데 사용할 수 있다.
        String refreshToken = jwtProvider.createRefreshToken(memberId);

        // =========================================================
        // 5. 토큰 응답 생성 및 반환
        // =========================================================

        // 발급한 Access Token과 Refresh Token을
        // TokenResponse DTO에 담는다.
        //
        // Access Token의 유효기간은 3600초(1시간)로 전달한다.
        // 5. 발급된 토큰 응답 반환 (Access Token 유효기간 1시간 = 3600초)
        TokenResponse response = TokenResponse.of(accessToken, refreshToken, 3600L);
        // 로그인 성공 → HTTP 200 OK
        // 생성된 토큰 정보를 JSON 형태로 반환한다.
        return ResponseEntity.ok(response);
    }
}