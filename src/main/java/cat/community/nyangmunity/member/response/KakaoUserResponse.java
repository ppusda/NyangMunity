package cat.community.nyangmunity.member.response;

import com.google.gson.annotations.SerializedName;
import java.util.Map;
import lombok.Getter;

@Getter
public class KakaoUserResponse {
    private String id;

    @SerializedName(value = "connected_at")
    private String connectedAt;

    @SerializedName(value = "kakao_account")
    private Map<?, ?> kakaoAccount;
}
