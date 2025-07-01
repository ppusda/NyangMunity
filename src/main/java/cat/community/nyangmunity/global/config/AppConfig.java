package cat.community.nyangmunity.global.config;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class AppConfig {

	@Value("${nyangmunity.jwt-key}")
	private final byte[] jwtKey;

	@Value("${nyangmunity.domain}")
	private final String domain;

	public AppConfig(String jwtKey, String domain) {
		this.jwtKey = Base64.getDecoder().decode(jwtKey);
		this.domain = domain;
	}
}
