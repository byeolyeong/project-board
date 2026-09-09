package net.likelion.bebc25.sns.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// 비밀번호 암호화에 사용할 PasswordEncoder를 설정하는 Configuration 클래스
@Configuration
public class PasswordEncoderConfig {

    // PasswordEncoder 객체를 Spring Bean으로 등록한다.
    // 회원가입 시 비밀번호를 암호화하거나
    // 로그인 시 입력한 비밀번호와 DB의 암호화된 비밀번호를 비교할 때 사용한다.
    @Bean
    public PasswordEncoder passwordEncoder(){
        // BCrypt 방식의 비밀번호 해시 인코더를 생성한다.
        // BCrypt는 비밀번호를 복호화할 수 없는 단방향 해시 방식으로 저장한다.
        // 기본 strength는 10이며, 반복 횟수는 2^10 = 1,024회이다.
        return new BCryptPasswordEncoder(); // 기본 강도(10 =  2^10 = 1,024)의 BCrypt 해시 인코더 사용
    }
}