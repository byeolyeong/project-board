package net.likelion.bebc25.sns.security.principal;

import net.likelion.bebc25.sns.domain.Member;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Member member;

    public CustomUserDetails(Member member){
        this.member = member;
    }

    // 사용자 정보가 전부 들어 있는 Member 객체를 반환
    public Member getMember(){
        return this.member;
    }

    // 회원 id 반환
    public Long getId(){
        return this.member.getId();
    }

    // 권한 목록 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 문자열을 Spring Security의 SimpleGrantedAuthority 타입으로 변환
        return List.of(new SimpleGrantedAuthority(member.getRole()));
    }

    // 비밀번호를 반환한다.
    @Override
    public @Nullable String getPassword() {
        return member.getPassword();
    }

    // 사용자의 식별자를 반환한다.
    @Override
    public String getUsername() {
        return member.getEmail();
    }

    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠김 여부 (비밀번호 5회 연속 틀렸을 경우)
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    // 인증 정보 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    // 계정 활성화 여부 (휴면 계정 관리)
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}