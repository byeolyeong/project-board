package net.likelion.bebc25.sns.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.sns.domain.Member;
import net.likelion.bebc25.sns.security.jwt.JwtProvider;
import net.likelion.bebc25.sns.security.principal.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;


// Google, Kakao 등 OAuth2 소셜 로그인에 성공했을 때 후속 처리를 담당하는 Handler
// 소셜 로그인 성공 → 자체 JWT 발급 → 콜백 페이지로 이동
@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // 자체 JWT를 생성하는 객체
    private final JwtProvider jwtProvider;

    public OAuth2SuccessHandler(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    // OAuth2 인증에 성공했을 때 실행되는 메서드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 1. 인증된 사용자 정보에서 Member 객체 추출
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.getMember();

        log.info("OAuth2 인증 성공 처리 시작: MemberId={}, Email={}", member.getId(), member.getEmail());

        // 2. 우리 서비스에서 사용할 JWT Access Token 생성
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(), member.getRole());

        // 3. 콜백 페이지 URL에 Access Token을 파라미터로 추가
        String targetUrl = UriComponentsBuilder.fromPath("/oauth/callback.html")
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        log.info("정적 콜백 페이지 리다이렉트 수행: {}", targetUrl);

        // 4. 브라우저를 콜백 페이지로 이동시킨다. (HTTP 302 Redirect)
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}