package cat.community.nyangmunity.global.provider;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import cat.community.nyangmunity.global.config.AppConfig;
import cat.community.nyangmunity.global.data.JwtValidateStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	private final SecretKey secretKey;

	@Value("${jwt.access-token-expiration}")
	private Long accessTokenExpiration;
	@Value("${jwt.refresh-token-expiration}")
	private Long refreshTokenExpiration;

	public JwtTokenProvider(AppConfig appConfig) {
		this.secretKey = Keys.hmacShaKeyFor(appConfig.getJwtKey());
	}

	/**
	 * Access 토큰 생성. jti 를 부여해 로그아웃 시 블랙리스트 키로 사용한다.
	 */
	public String createAccessToken(Long memberId) {
		Date now = new Date();
		return Jwts.builder()
			.id(UUID.randomUUID().toString())
			.subject(memberId.toString())
			.signWith(secretKey)
			.expiration(new Date(now.getTime() + accessTokenExpiration))
			.issuedAt(now)
			.compact();
	}

	public String createRefreshToken(Long memberId) {
		Date now = new Date();
		return Jwts.builder()
			.subject(memberId.toString())
			.signWith(secretKey)
			.expiration(new Date(now.getTime() + refreshTokenExpiration))
			.issuedAt(now)
			.compact();
	}

	public Claims getClaims(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	public JwtValidateStatus validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token);
			return JwtValidateStatus.ACCEPTED;
		} catch (ExpiredJwtException e) {
			return JwtValidateStatus.EXPIRED;
		} catch (JwtException e) {
			return JwtValidateStatus.DENIED;
		}
	}

	public Authentication getAuthentication(String token) {
		Claims claims = getClaims(token);
		User principal = new User(claims.getSubject(), "", Collections.emptyList());
		return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
	}

	/**
	 * 블랙리스트 키로 쓰는 jti.
	 */
	public String getJti(String token) {
		return getClaims(token).getId();
	}

	/**
	 * 토큰 만료까지 남은 시간(ms). 0 이하면 이미 만료. 블랙리스트 TTL 산정에 쓴다.
	 */
	public long getRemainingMillis(String token) {
		long remaining = getClaims(token).getExpiration().getTime() - System.currentTimeMillis();
		return Math.max(remaining, 0);
	}

}
