package cat.community.nyangmunity.global.provider;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import cat.community.nyangmunity.global.config.AppConfig;

@Component
public class CookieProvider {

	public static final String ACCESS_TOKEN_COOKIE = "accessToken";
	public static final String REFRESH_TOKEN_COOKIE = "refreshToken";

	private static final String SAME_SITE = "Lax";
	private static final String COOKIE_PATH = "/";

	private final String domain;
	private final boolean secure;
	private final long accessTokenExpirationMs;
	private final long refreshTokenExpirationMs;

	public CookieProvider(
		AppConfig appConfig,
		@Value("${jwt.access-token-expiration}") long accessTokenExpirationMs,
		@Value("${jwt.refresh-token-expiration}") long refreshTokenExpirationMs
	) {
		this.domain = appConfig.getDomain();
		this.secure = appConfig.isCookieSecure();
		this.accessTokenExpirationMs = accessTokenExpirationMs;
		this.refreshTokenExpirationMs = refreshTokenExpirationMs;
	}

	/**
	 * Access 토큰 쿠키. 프론트 axios 인터셉터가 JS 로 읽어 Authorization Bearer 헤더에 첨부해야 하므로
	 * HttpOnly=false 로 둔다. (XSS 노출 위험 vs 프론트 구조 변경 비용을 저울질해서 후자로 결정 — PRD §8.3 D4)
	 */
	public ResponseCookie createAccessTokenCookie(String accessToken) {
		return baseCookie(ACCESS_TOKEN_COOKIE, accessToken, accessTokenExpirationMs)
			.httpOnly(false)
			.build();
	}

	/**
	 * Refresh 토큰 쿠키. /tokens 재발급 요청 시에만 자동 첨부되도록 HttpOnly + SameSite=Lax.
	 */
	public ResponseCookie createRefreshTokenCookie(String refreshToken) {
		return baseCookie(REFRESH_TOKEN_COOKIE, refreshToken, refreshTokenExpirationMs)
			.httpOnly(true)
			.build();
	}

	public ResponseCookie removeAccessTokenCookie() {
		return removeCookie(ACCESS_TOKEN_COOKIE, false);
	}

	public ResponseCookie removeRefreshTokenCookie() {
		return removeCookie(REFRESH_TOKEN_COOKIE, true);
	}

	private ResponseCookie.ResponseCookieBuilder baseCookie(String name, String value, long maxAgeMs) {
		return ResponseCookie.from(name, value)
			.domain(domain)
			.path(COOKIE_PATH)
			.secure(secure)
			.sameSite(SAME_SITE)
			.maxAge(Duration.ofMillis(maxAgeMs));
	}

	private ResponseCookie removeCookie(String name, boolean httpOnly) {
		return ResponseCookie.from(name, "")
			.domain(domain)
			.path(COOKIE_PATH)
			.secure(secure)
			.sameSite(SAME_SITE)
			.httpOnly(httpOnly)
			.maxAge(0)
			.build();
	}
}
