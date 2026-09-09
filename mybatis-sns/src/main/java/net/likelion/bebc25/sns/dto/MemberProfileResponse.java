package net.likelion.bebc25.sns.dto;

import net.likelion.bebc25.sns.domain.Member;

import java.time.LocalDateTime;

// 회원 프로필 조회 API의 응답 데이터를 표현하는 DTO
// Member 객체 전체가 아니라 클라이언트에게 필요한 정보만 전달한다.
public record MemberProfileResponse(
        // 회원 고유 ID
        Long id,
        // 회원 이메일
        String email,
        // 회원 닉네임
        String nickname,
        // 프로필 이미지 경로 또는 URL
        String profileImage,
        // 회원 권한
        String role,
        // 회원 가입 날짜와 시간
        LocalDateTime createAt
) {
    // Member 객체를 MemberProfileResponse DTO로 변환하는 메서드
    // Controller에서 Member 정보를 API 응답 형태로 변환할 때 사용한다.
    public static MemberProfileResponse from(Member member){
        // Member의 각 필드 값을 꺼내
        // MemberProfileResponse 객체를 생성한다.
        return new MemberProfileResponse(
                // 회원 ID
                member.getId(),
                // 회원 이메일
                member.getEmail(),
                // 회원 닉네임
                member.getNickname(),
                // 프로필 이미지
                member.getProfileImage(),
                // 회원 권한
                member.getRole(),
                // 회원 생성 날짜와 시간
                member.getCreatedAt()
        );
    }
}
