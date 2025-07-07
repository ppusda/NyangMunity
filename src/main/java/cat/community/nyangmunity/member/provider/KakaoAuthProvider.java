package cat.community.nyangmunity.member.provider;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import cat.community.nyangmunity.member.config.KakaoAuthConfig;
import cat.community.nyangmunity.member.response.KakaoTokenResponse;
import cat.community.nyangmunity.member.response.KakaoUserResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KakaoAuthProvider {

	private final KakaoAuthConfig kakaoAuthConfig;

	private final RestTemplate restTemplate = new RestTemplate();
	public final Gson gson = new Gson();

	public HttpHeaders setDefaultHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Accept", "application/json");

		return headers;
	}

	public KakaoTokenResponse getToken(String code) {
		HttpHeaders headers = setDefaultHeaders();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("client_id", kakaoAuthConfig.getClientKey());
		params.add("client_secret", kakaoAuthConfig.getSecretKey());
		params.add("redirect_uri", kakaoAuthConfig.getRedirectUri());
		params.add("grant_type", kakaoAuthConfig.getGrantType());
		params.add("code", code);

		ResponseEntity<String> responseEntity = getResponse(headers, params, kakaoAuthConfig.getTOKEN_REQUEST_URL());

		return gson.fromJson(responseEntity.getBody(), KakaoTokenResponse.class);
	}

	public KakaoUserResponse getUserInfo(String accessToken) {
		HttpHeaders headers = setDefaultHeaders();
		headers.add("Authorization", "Bearer " + accessToken);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = getResponse(headers, kakaoAuthConfig.getUSER_REQUEST_URL());

		return gson.fromJson(responseEntity.getBody(), KakaoUserResponse.class);
	}

	public ResponseEntity<String> getResponse(HttpHeaders headers, String url) {
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
		return restTemplate.postForEntity(url, request, String.class);
	}

	public ResponseEntity<String> getResponse(HttpHeaders headers, MultiValueMap<String, String> params, String url) {
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
		return restTemplate.postForEntity(url, request, String.class);
	}

}
