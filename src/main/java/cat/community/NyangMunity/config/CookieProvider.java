package cat.community.NyangMunity.config;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieProvider {

    public ResponseCookie createCookie(String accessToken) {
        ResponseCookie cookie = ResponseCookie.from("SESSION", accessToken)
                .domain("localhost") // todo 향후 서버 환경에 따른 분리 필요
                .path("/")
                .httpOnly(false) // javascript가 cookie 값에 접근하지 못하게 하는 설정.
                .secure(false)
                .maxAge(Duration.ofHours(3))
                .sameSite("Strict")
                .build();

        return cookie;
    }
}
