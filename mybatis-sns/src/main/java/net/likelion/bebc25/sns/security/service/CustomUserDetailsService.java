package net.likelion.bebc25.sns.security.service;

import net.likelion.bebc25.sns.domain.Member;
import net.likelion.bebc25.sns.mapper.MemberMapper;
import net.likelion.bebc25.sns.security.principal.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security의 사용자 인증 정보를 조회하는 Service
// 로그인 시 사용자의 이메일을 기준으로 DB에서 회원 정보를 조회한다.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // DB에서 회원 정보를 조회하는 Mapper
    private final MemberMapper memberMapper;

    // MemberMapper를 생성자를 통해 주입받는다.
    // → 생성자 의존성 주입(DI)
    public CustomUserDetailsService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    // Spring Security가 로그인 과정에서 사용자 정보를 조회할 때 호출한다.
    // 여기서는 username 대신 이메일을 사용자 식별자로 사용한다.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 전달받은 이메일을 기준으로 DB에서 회원 정보를 조회한다.
        Member member = memberMapper.findByEmail(email);
        // 해당 이메일의 회원이 존재하지 않는 경우
        if(member == null){
            // Spring Security에 사용자를 찾을 수 없음을 알린다.
            throw new UsernameNotFoundException("사용자가 없습니다.");
        }
        // 조회한 Member 객체를 Spring Security가 사용할 수 있도록
        // CustomUserDetails 객체로 변환하여 반환한다.
        return new CustomUserDetails(member);
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {

        // 전달받은 회원 ID(PK)를 기준으로 DB에서 회원 정보를 조회한다.
        Member member = memberMapper.findById(id);

        // 해당 ID의 회원이 존재하지 않는 경우
        if(member == null){
            // Spring Security에 사용자를 찾을 수 없음을 알린다.
            throw new UsernameNotFoundException("사용자가 없습니다.");
        }
        // 조회한 Member 객체를 Spring Security가 사용할 수 있도록
        // CustomUserDetails 객체로 변환하여 반환한다.
        return new CustomUserDetails(member);
    }
}