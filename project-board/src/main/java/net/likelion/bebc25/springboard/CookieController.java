package net.likelion.bebc25.springboard;

import jakarta.servlet.http.HttpServletResponse;
import net.likelion.bebc25.springboard.member.dto.MemberDto;
import net.likelion.bebc25.springboard.member.service.MemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Controller
@RequestMapping("/cookie")
public class CookieController {

    private MemberService memberService;
    public CookieController(MemberService memberService){
        this.memberService = memberService;
    }

    @GetMapping("/create") // http:loaclhost:8080/cookie/create?memberId=3 응답
    private String createCookie(@RequestParam String memberId, HttpServletResponse response){
        ResponseCookie memberIDdCookie = ResponseCookie.from("memberId", memberId)
                .maxAge(Duration.ofHours(1)) //.maxAge(Duration.ofHours(1) 1시간 동안 유효하게 해줌
                .path("/") // 경로 설정
                .httpOnly(true) // 자바스크립트 접근 불가
                .build();

        ResponseCookie usernameCookie = ResponseCookie.from("userName", "haru")
                .maxAge(Duration.ofHours(1)) //.maxAge(Duration.ofHours(1) 1시간 동안 유효하게 해줌
                .path("/") // 경로 설정
                .httpOnly(true) // 자바스크립트 접근 불가
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, memberIDdCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, usernameCookie.toString());

        return "redirect:/";
    }

    @GetMapping("/view") // http:loaclhost:8080/cookie/view 응답
    @ResponseBody
    private String viewCookie(@CookieValue(name = "memberId",  required = false) Integer memberId,
                              @CookieValue(name = "username",  required = false) String username){
        if(memberId == null){
            return  "<p>memberId 쿠키가 필요없습니다.</p>";
        }

        MemberDto memberInfo = memberService.getMember(memberId);
        return """
                <ul>
                    <li>번호: %s</li>
                    <li>이름: %s</li>
                    <li>권한: %s</li>
                <ul>
                """.formatted(memberInfo.getId(), memberInfo.getUsername(), memberInfo.getRole());
    }

    @GetMapping("/delete") // http:loaclhost:8080/cookie/delete 응답
    private String deleteCookie(){
        return "redirect:/";
    }
}
