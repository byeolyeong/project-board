package net.likelion.bebc25.springboard.config;

import net.likelion.bebc25.springboard.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/member/*", "/post/write")
                .excludePathPatterns("/member/login", "/member/register", "/css/**", "/js/**", "/*/icon", "/error");

    }
}
