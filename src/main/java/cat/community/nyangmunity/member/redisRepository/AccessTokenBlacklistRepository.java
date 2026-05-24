package cat.community.nyangmunity.member.redisRepository;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

/**
 * 로그아웃 후에도 access 토큰이 만료 전까지 살아 있는 문제를 막기 위한 블랙리스트.
 * 키는 토큰의 jti, TTL 은 토큰 남은 만료시간만큼만 부여해 만료 후 자동 정리되게 한다.
 */
@Repository
@RequiredArgsConstructor
public class AccessTokenBlacklistRepository {

	private static final String KEY_PREFIX = "blacklist:access:";

	private final StringRedisTemplate redisTemplate;

	public void blacklist(String jti, long ttlMillis) {
		if (jti == null || ttlMillis <= 0) {
			return;
		}
		redisTemplate.opsForValue().set(KEY_PREFIX + jti, "1", Duration.ofMillis(ttlMillis));
	}

	public boolean isBlacklisted(String jti) {
		if (jti == null) {
			return false;
		}
		return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + jti));
	}
}
