package net.likelion.bebc25.jpasns.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
// 기본 생성자는 Protected 이상
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    // PK 역할을 알려주는 어노테이션 @Id
    @Id
    // MySQL auto increment 속성으로 값을 부여하는 속성
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String nickname;

    @Column(nullable = false, length = 20)
    private String role;

    private String profileImage;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Member(String email, String password, String nickname) {
        this(email, password, nickname, "USER");
    }

    public Member(String email, String nickname, String password, String role) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    public void changeNickname(String nickname){
        this.nickname = nickname;
    }
}
