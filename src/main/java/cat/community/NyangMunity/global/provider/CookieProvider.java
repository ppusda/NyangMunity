package cat.community.NyangMunity.global.provider;

import cat.community.NyangMunity.global.config.AppConfig;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieProvider {

    String domain;

    public CookieProvider(AppConfig appConfig) {
        this.domain = appConfig.getDomain();
    }

    public ResponseCookie createAccessTokenCookie(String accessToken) {

        return ResponseCookie.from("accessToken", accessToken)
                .domain(domain)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofMinutes(30))
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {

        return ResponseCookie.from("refreshToken", refreshToken)
                .domain(domain)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofDays(1))
                .build();
    }

    public ResponseCookie removeToken(String cookieName) {
        return ResponseCookie.from(cookieName, null)
                .domain(domain)
                .path("/")
                .maxAge(0)
                .build();
    }
}
