package cat.community.nyangmunity.member.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Getter;

@Getter
@Configuration
public class KakaoAuthConfig {

	@Value("${kakao.client-key}")
	private String clientKey;

	@Value("${kakao.secret-key}")
	private String secretKey;

	@Value("${kakao.grant-type}")
	private String grantType;

	@Value("${kakao.redirect-uri}")
	private String redirectUri;

	private final String AUTHORIZATION_URL = "https://kauth.kakao.com/oauth/authorize";
	private final String TOKEN_REQUEST_URL = "https://kauth.kakao.com/oauth/token";
	private final String USER_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

	public String generateAuthorizationUrl() {
		return AUTHORIZATION_URL +
			"?client_id=" + clientKey +
			"&redirect_uri=" + redirectUri +
			"&response_type=code";
	}

	@Bean
	public WebClient webClient() {
		return WebClient.builder().build();
	}
}
