package net.likelion.bebc25.sns.security.principal;

import net.likelion.bebc25.sns.domain.Member;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Spring Security가 사용하는 사용자 인증 정보를 표현하는 클래스
// DB의 Member 객체를 Spring Security가 이해할 수 있는 UserDetails 형태로 변환한다.
public class CustomUserDetails implements UserDetails {

    // 실제 회원 정보를 가지고 있는 Member 객체
    private final Member member;

    // Member 객체를 전달받아 CustomUserDetails를 생성한다.
    public CustomUserDetails(Member member){
        this.member = member;
    }

    // ID 반환
    // Spring Security의 기본 UserDetails에는 ID가 없기 때문에
    // 애플리케이션에서 회원 ID가 필요할 때 사용할 수 있도록 추가한 메서드이다.
    public Long getId(){
        return this.member.getId();
    }

    // 사용자 정보가 전부 들어 있는 Member 객체를 반환
    // 애플리케이션에서 회원의 다른 정보가 필요할 때 사용할 수 있다.
    public Member getMember(){
        return this.member;
    }

    // 권한 목록 반환
    // Spring Security는 GrantedAuthority 타입으로 사용자의 권한을 관리한다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 문자열을 Spring Security의 SimpleGrantedAuthority 타입으로 변환
        return List.of(new SimpleGrantedAuthority(member.getRole()));
    }

    // 비밀번호를 반환한다.
    // Spring Security가 로그인 인증 과정에서 사용한다.
    @Override
    public @Nullable String getPassword() {
        return member.getPassword();
    }

    // 사용자의 식별자를 반환한다.
    // 여기서는 회원의 이메일을 로그인 ID로 사용한다.
    @Override
    public String getUsername() {
        return member.getEmail();
    }

    // 계정 만료 여부
    // true이면 계정이 만료되지 않은 상태이다.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠김 여부 (비밀번호 5회 연속 틀렸을 경우)
    // 현재는 별도의 계정 잠금 기능을 구현하지 않았기 때문에
    // 기본 UserDetails의 값을 사용한다.
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    // 인증 정보 만료 여부
    // 현재는 별도의 인증 정보 만료 기능을 구현하지 않았기 때문에
    // 기본 UserDetails의 값을 사용한다.
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    // 계정 활성화 여부 (휴면 계정 관리)
    // 현재는 별도의 휴면/비활성 계정 관리 기능을 구현하지 않았기 때문에
    // 기본 UserDetails의 값을 사용한다.
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}