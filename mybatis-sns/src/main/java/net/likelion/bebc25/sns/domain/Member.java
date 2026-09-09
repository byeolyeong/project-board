package net.likelion.bebc25.sns.domain;

import lombok.*;

import java.time.LocalDateTime;

// 회원 정보를 표현하는 Domain 클래스
// DB의 회원 정보를 Java 객체로 표현할 때 사용함
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Member {
    private Long id;
    private String email;
    private String nickname;
    private String password;
    private String profileImage;
    // 별도로 지정하지 않으면 기본적으로 일반 사용자 권한을 부여한다.
    @Builder.Default
    private String role = "ROLE_USER";
    private LocalDateTime createdAt;
}
