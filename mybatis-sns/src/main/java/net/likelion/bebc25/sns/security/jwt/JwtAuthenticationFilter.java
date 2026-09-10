package net.likelion.bebc25.sns.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.likelion.bebc25.sns.security.service.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


// HTTP 요청이 들어올 때마다 JWT를 확인하여
// 현재 사용자의 인증 정보를 SecurityContext에 저장하는 Filter
//
// 쉽게 말하면:
// "요청에 JWT가 있는지 확인 → JWT가 유효하면 로그인한 사용자로 인증"
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JWT가 들어있는 HTTP Header 이름
    private static final String AUTHORIZATION_HEADER = "Authorization";

    // Authorization Header에서 사용하는 Bearer 접두사
    private static final String BEARER_PREFIX = "Bearer ";

    // JWT 생성 및 검증을 담당하는 객체
    private final JwtProvider jwtProvider;

    // JWT의 회원 ID를 이용해 사용자 정보를 조회하는 Service
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtProvider jwtProvider,
            CustomUserDetailsService userDetailsService) {

        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    // HTTP 요청이 들어올 때마다 실행되는 필터
    // OncePerRequestFilter이므로 하나의 요청당 한 번만 실행된다.
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청 헤더에서 JWT 토큰 추출
        // Authorization: Bearer <Token> 형식으로 전달된 토큰을 가져온다.
        String token = resolveToken(request);

        // 토큰이 존재하고 JWT의 유효성 검증까지 통과한 경우에만 인증을 진행한다.
        if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {

            // JWT에 저장된 회원 ID(PK)를 가져온다.
            Long memberId = jwtProvider.getMemberId(token);

            // 3. 토큰 주체(PK) 기반 사용자 정보 조회 및 UserDetails 생성
            // 회원 ID를 이용해 DB에서 회원 정보를 조회하고
            // Spring Security가 사용하는 UserDetails 객체로 변환한다.
            UserDetails userDetails =
                    userDetailsService.loadUserById(memberId);

            // 4. 인증 완료 토큰(UsernamePasswordAuthenticationToken) 생성
            // - principal: 인증 주체 (UserDetails)
            // - credentials: 자격 증명 (비밀번호는 이미 토큰으로 검증되었으므로 보안상 null 지정)
            // - authorities: 인가 심사에 사용할 사용자 권한 목록 (ROLE_USER 등)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            // 현재 요청에 대한 추가적인 HTTP 정보를 인증 객체에 저장한다.
            // 예: 요청한 IP, 세션 ID 등의 정보
            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            // 5. SecurityContextHolder에 인증 정보 저장 (이후 컨트롤러에서 @AuthenticationPrincipal로 사용 가능)
            // 여기까지 저장되면 Spring Security는
            // 현재 요청을 인증된 사용자로 인식한다.
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
        }

        // 6. 체인의 다음 필터로 위임
        // 현재 필터의 작업이 끝났으므로 다음 Security Filter로 요청을 전달한다.
        filterChain.doFilter(request, response);
    }


    // Authorization: Bearer <Token> 형식에서 실제 토큰 값만 분리 추출
    private String resolveToken(HttpServletRequest request) {

        // Authorization Header에서 토큰을 가져온다.
        String bearerToken =
                request.getHeader(AUTHORIZATION_HEADER);

        // null, 빈 문자열(""), 공백(" ") 여부를 일괄 검증하고 "Bearer " 접두사 일치 확인
        if (StringUtils.hasText(bearerToken)
                && bearerToken.startsWith(BEARER_PREFIX)) {

            // "Bearer " 부분을 제거하고 실제 JWT 값만 반환한다.
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        // JWT가 없거나 Bearer 형식이 아니면 null을 반환한다.
        return null;
    }
}