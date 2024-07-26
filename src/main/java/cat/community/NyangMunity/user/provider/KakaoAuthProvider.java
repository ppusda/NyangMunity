package cat.community.NyangMunity.user.provider;

import cat.community.NyangMunity.user.response.KakaoTokenResponse;
import cat.community.NyangMunity.user.response.KakaoUserResponse;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@ConfigurationProperties("kakao")
public class KakaoAuthProvider {

    private final String clientKey;
    private final String secretKey;
    private final String grantType;
    private final String redirectUri;

    String TOKEN_REQUEST_URL = "https://kauth.kakao.com/oauth/token";
    String USER_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

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
        params.add("client_id", clientKey);
        params.add("client_secret", secretKey);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", grantType);
        params.add("code", code);

        ResponseEntity<String> responseEntity = getResponse(headers, params, TOKEN_REQUEST_URL);

        return gson.fromJson(responseEntity.getBody(), KakaoTokenResponse.class);
    }

    public KakaoUserResponse getUserInfo(String accessToken) {
        HttpHeaders headers = setDefaultHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = getResponse(headers, USER_REQUEST_URL);

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
