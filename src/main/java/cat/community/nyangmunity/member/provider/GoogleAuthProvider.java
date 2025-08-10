package cat.community.nyangmunity.member.provider;

import cat.community.nyangmunity.member.config.GoogleAuthConfig;
import cat.community.nyangmunity.member.response.google.GoogleTokenResponse;
import cat.community.nyangmunity.member.response.google.GoogleUserResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GoogleAuthProvider {

	private final GoogleAuthConfig googleAuthConfig;
	private final WebClient webClient;

	public String requestAuthorizationUrl() {
		return googleAuthConfig.generateAuthorizationUrl();
	}

	public Mono<GoogleTokenResponse> getToken(String code) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("client_id", googleAuthConfig.getClientId());
		params.add("client_secret", googleAuthConfig.getClientSecret());
		params.add("redirect_uri", googleAuthConfig.getRedirectUri());
		params.add("grant_type", googleAuthConfig.getGrantType());
		params.add("code", code);

		return webClient.post()
			.uri(googleAuthConfig.getTOKEN_REQUEST_URL())
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.accept(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromFormData(params))
			.retrieve()
			.bodyToMono(GoogleTokenResponse.class);
	}

	public Mono<GoogleUserResponse> getUserInfo(String accessToken) {
		return webClient.get()
			.uri(googleAuthConfig.getUSER_REQUEST_URL())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToMono(GoogleUserResponse.class);
	}
}
