package cat.community.nyangmunity.global.config;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Getter
@Configuration
public class AppConfig {

	@Value("${nyangmunity.jwt-key}")
	private String encodedJwtKey;

	@Value("${nyangmunity.domain}")
	private String domain;

	private byte[] jwtKey;

	@PostConstruct
	public void init() {
		this.jwtKey = Base64.getDecoder().decode(encodedJwtKey);
	}
}

