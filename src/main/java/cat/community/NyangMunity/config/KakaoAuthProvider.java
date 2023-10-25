package cat.community.NyangMunity.config;

import cat.community.NyangMunity.response.KakaoTokenResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component @Getter @Setter
@Slf4j
@ConfigurationProperties(prefix = "kakao")
public class KakaoAuthProvider {

    public String clientKey;
    public String secretKey;
    public String grantType;
    public String redirectUri;

    String REQUEST_URL = "https://kauth.kakao.com/oauth/token";
    RestTemplate restTemplate = new RestTemplate();

    public KakaoTokenResponse getToken(String code) throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Accept", "application/json");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientKey + " a");
        params.add("client_secret", secretKey);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", grantType);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(REQUEST_URL, request, String.class);

        String json = responseEntity.getBody();
        JSONObject  jsonObject = new JSONObject(json);

        return KakaoTokenResponse.builder()
                .access_token((String) jsonObject.get("access_token"))
                .refresh_token((String) jsonObject.get("refresh_token"))
                .build();
    }
}
