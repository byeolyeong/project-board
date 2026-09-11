package net.likelion.bebc25.sns.security.oauth;

import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.sns.domain.Member;
import net.likelion.bebc25.sns.mapper.MemberMapper;
import net.likelion.bebc25.sns.security.principal.CustomUserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

// 소셜 로그인(Google, Kakao) 사용자를 처리하는 Service
// 소셜에서 받은 사용자 정보를 DB 회원 정보로 연결한다.
@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    // DB에서 회원 정보를 조회하고 저장하는 Mapper
    private final MemberMapper memberMapper;

    public CustomOAuth2UserService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    // 소셜 로그인 성공 후 사용자 정보를 가져오는 메서드
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 1. 소셜 플랫폼에서 사용자 프로필 정보 조회
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 어떤 소셜 로그인인지 확인 (google, kakao)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 3. 소셜에서 받은 사용자 정보를 가져와 DB 회원으로 저장하거나 조회
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Member member = saveOrUpdate(registrationId, attributes);

        // 4. DB 회원 정보와 소셜 사용자 정보를 함께 담아 반환
        return new CustomUserDetails(member, attributes);
    }

    // 소셜 플랫폼의 사용자 정보를 Member 객체로 변환하고 DB에 반영한다.
    private Member saveOrUpdate(String registrationId, Map<String, Object> attributes) {
        String email;
        String nickname;

        // 소셜 플랫폼마다 사용자 정보의 JSON 구조가 다르므로 각각 처리한다.
        switch (registrationId.toLowerCase()) {

            // Google 로그인 사용자 정보 처리
            case "google" -> {
                email = (String) attributes.get("email");
                nickname = (String) attributes.get("name");
            }

            // Kakao 로그인 사용자 정보 처리
            case "kakao" -> {
                // Kakao는 이메일과 프로필 정보가 kakao_account 안에 들어있다.
                Map<String, Object> kakaoAccount =
                        (Map<String, Object>) attributes.get("kakao_account");

                Map<String, Object> profile =
                        (kakaoAccount != null)
                                ? (Map<String, Object>) kakaoAccount.get("profile")
                                : null;

                email = (kakaoAccount != null)
                        ? (String) kakaoAccount.get("email")
                        : null;

                nickname = (profile != null)
                        ? (String) profile.get("nickname")
                        : "KakaoUser";

                // 카카오에서 이메일을 제공하지 않는 경우
                // 카카오 고유 ID를 이용해 임시 이메일을 만든다.
                if (email == null || email.isBlank()) {
                    Object kakaoId = attributes.get("id");
                    email = "kakao_" + kakaoId + "@kakao.social";

                    log.info("카카오 이메일 미제공 계정: 대체 가상 이메일 [{}] 생성", email);
                }
            }
            // 지원하지 않는 소셜 로그인인 경우 예외 발생
            default -> throw new OAuth2AuthenticationException(
                    "지원하지 않는 소셜 로그인 공급자입니다: " + registrationId
            );
        }

        // DB에 같은 이메일의 회원이 있는지 확인한다.
        Member existingMember = memberMapper.findByEmail(email);
        if (existingMember == null) {
            // 처음 로그인한 사용자라면 자동으로 회원가입한다.
            Member newMember = Member.builder()
                    .email(email)
                    .password("") // 소셜 로그인 회원은 자체 비밀번호가 없으므로 빈 문자열 저장
                    .nickname(nickname)
                    .role("ROLE_USER")
                    .build();

            // 새 회원 정보를 DB에 저장한다.
            memberMapper.save(newMember);
            log.info(
                    "신규 소셜 회원 DB 자동 가입 완료: ID={}, Email={}",
                    newMember.getId(),
                    newMember.getEmail()
            );
            return newMember;
        }

        // 이미 가입한 회원이면 기존 회원 정보를 반환한다.
        return existingMember;
    }
}