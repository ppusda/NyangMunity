package cat.community.nyangmunity.global.provider;

import java.time.Duration;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import cat.community.nyangmunity.global.config.AppConfig;

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
			.httpOnly(false)
			.secure(false)
			.maxAge(Duration.ofMinutes(30))
			.build();
	}

	public ResponseCookie createRefreshTokenCookie(String refreshToken) {

		return ResponseCookie.from("refreshToken", refreshToken)
			.domain(domain)
			.path("/")
			.httpOnly(false)
			.secure(false)
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
