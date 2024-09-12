package kr.co.ureca.sockettest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/* CORS = 다른 출처의 리소스를 요청할 수 있도록 하는 메커니즘 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final String DEVELOP_FRONT_ADDRESS = "http://localhost:3000";

    @Override
    public void addCorsMappings(CorsRegistry registry) { // cors 설정
        registry.addMapping("/**") // CORS를 적용할 URL 패턴을 정의
                .allowedOrigins(DEVELOP_FRONT_ADDRESS) // 자원 공유를 허락할 Origin을 지정 -> 프론트(3000)에서 오는 요청 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP method를 지정
                .exposedHeaders("location") // 클라이언트 측 응답에서 노출되는 헤더를 지정
                .allowedHeaders("*") // 클라이언트 측의 CORS 요청에 허용되는 헤더를 지정
                .allowCredentials(true); // 라이언트 측에 대한 응답에 credentials(쿠키, 인증 헤더)를 포함할 수 있는지 여부를 지정
    }
}
