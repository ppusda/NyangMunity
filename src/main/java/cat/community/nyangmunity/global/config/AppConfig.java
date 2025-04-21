package cat.community.nyangmunity.global.config;

import java.util.Base64;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "nyangmunity")
public class AppConfig {

	private final byte[] jwtKey;
	private final String domain;

	public AppConfig(String jwtKey, String domain) {
		this.jwtKey = Base64.getDecoder().decode(jwtKey);
		this.domain = domain;
	}
}
