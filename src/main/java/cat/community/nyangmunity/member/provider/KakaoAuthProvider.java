package cat.community.nyangmunity.member.provider;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import cat.community.nyangmunity.member.config.KakaoAuthConfig;
import cat.community.nyangmunity.member.response.KakaoTokenResponse;
import cat.community.nyangmunity.member.response.KakaoUserResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class KakaoAuthProvider {

    private final KakaoAuthConfig kakaoAuthConfig;
    private final WebClient webClient;

    public String requestAuthorizationUrl() {
        return kakaoAuthConfig.generateAuthorizationUrl();
    }

    public Mono<KakaoTokenResponse> getToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", kakaoAuthConfig.getClientKey());
        params.add("client_secret", kakaoAuthConfig.getSecretKey());
        params.add("redirect_uri", kakaoAuthConfig.getRedirectUri());
        params.add("grant_type", kakaoAuthConfig.getGrantType());
        params.add("code", code);

        return webClient.post()
            .uri(kakaoAuthConfig.getTOKEN_REQUEST_URL())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromFormData(params))
            .retrieve()
            .bodyToMono(KakaoTokenResponse.class);
    }

    public Mono<KakaoUserResponse> getUserInfo(String accessToken) {
        return webClient.post()
            .uri(kakaoAuthConfig.getUSER_REQUEST_URL())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(KakaoUserResponse.class);
    }
}
