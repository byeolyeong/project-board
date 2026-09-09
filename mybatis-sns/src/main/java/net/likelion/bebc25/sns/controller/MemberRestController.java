package net.likelion.bebc25.sns.controller;

import net.likelion.bebc25.sns.domain.Member;
import net.likelion.bebc25.sns.dto.MemberProfileResponse;
import net.likelion.bebc25.sns.security.principal.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 회원 관련 REST API 요청을 처리하는 Controller
@RestController
// 회원 API의 공통 URL
// 실제 요청 주소: /api/v1/members
@RequestMapping("/api/v1/members")
public class MemberRestController {

    // =========================================================
    // 내 프로필 조회
    // GET /api/v1/members/me
    // =========================================================

    // GET 요청을 처리한다.
    // 현재 로그인한 사용자의 프로필 정보를 조회한다.
    @GetMapping("/me")
    public ResponseEntity<MemberProfileResponse> getMyProfile(
            // SecurityContext에 저장된 인증된 사용자 정보를 가져온다.
            // 로그인 과정에서 인증이 완료되면 Spring Security가
            // 현재 사용자의 Authentication 정보를 SecurityContext에 저장한다.
            @AuthenticationPrincipal CustomUserDetails userDetails // SecurityContext에서 인증된 사용자 정보를 반환
    ){
        // 인증된 사용자의 Member 정보를 가져온다.
        // CustomUserDetails가 가지고 있는 실제 회원 정보이다.
        Member member = userDetails.getMember();
        // Member 객체를 MemberProfileResponse DTO로 변환한다.
        // 회원의 프로필 조회에 필요한 정보만 응답으로 전달한다.
        return ResponseEntity.ok(MemberProfileResponse.from(member));
    }

}
