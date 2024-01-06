package cat.community.NyangMunity.user.response;

import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class KakaoTokenResponse {
    private String access_token;
    private String refresh_token;
}
