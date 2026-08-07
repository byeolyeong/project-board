package net.likelion.bebc25.springboard.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        HttpSession session = request.getSession(false);

        if(session == null || session.getAttribute("loginMember") == null){
            log.info("로그인 안된 사용자의 요청" + requestUri);
            // 미인증 사용자일 경우 로그인 페이지로 리다이렉스 시킴
            response.sendRedirect("/member/login");
            return false; // HandlerInterceptor가 false 리턴할 경우 컨트롤러 핸들러를 실행하지 않음
        }

        return true;    // HandlerInterceptor가 true 리턴할 경우 다음 HandlerInterceptor나 컨트롤러 핸들러를 실행함
    }
}
