package cat.community.nyangmunity.global.config;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Getter
@Configuration
public class AppConfig {

	private static final int MIN_JWT_KEY_BYTES = 32; // HMAC-SHA256 최소 키 길이

	@Value("${nyangmunity.jwt-key}")
	private String encodedJwtKey;

	@Value("${nyangmunity.domain}")
	private String domain;

	@Value("${nyangmunity.cookie-secure}")
	private boolean cookieSecure;

	private byte[] jwtKey;

	@PostConstruct
	public void init() {
		this.jwtKey = Base64.getDecoder().decode(encodedJwtKey);
		if (this.jwtKey.length < MIN_JWT_KEY_BYTES) {
			throw new IllegalStateException(
				"nyangmunity.jwt-key 가 너무 짧습니다. HMAC-SHA256 은 최소 " + MIN_JWT_KEY_BYTES + " bytes 필요."
			);
		}
	}
}

