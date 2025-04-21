package cat.community.nyangmunity.member.response;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class KakaoUserResponse {
	private String id;

	@SerializedName(value = "connected_at")
	private String connectedAt;

	@SerializedName(value = "kakao_account")
	private Map<?, ?> kakaoAccount;
}
