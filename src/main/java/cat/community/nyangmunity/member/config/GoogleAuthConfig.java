package cat.community.nyangmunity.member.config;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class GoogleAuthConfig {

	@Value("${google.client-id}")
	private String clientId;

	@Value("${google.client-secret}")
	private String clientSecret;

	@Value("${google.redirect-uri}")
	private String redirectUri;

	@Value("${google.grant-type}")
	private String grantType;

	private final String TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";
	private final String USER_REQUEST_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
	private final String AUTHORIZATION_URL = "https://accounts.google.com/o/oauth2/v2/auth";

	public String generateAuthorizationUrl() {
		return AUTHORIZATION_URL + "?" +
			"client_id=" + clientId +
			"&redirect_uri=" + redirectUri +
			"&response_type=code" +
			"&scope=email%20profile";
	}
}
