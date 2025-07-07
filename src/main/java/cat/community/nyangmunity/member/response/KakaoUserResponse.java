package cat.community.nyangmunity.member.response;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public record KakaoUserResponse(
	String id,

	@SerializedName(value = "connected_at")
	String connectedAt,

	@SerializedName(value = "kakao_account")
	Map<?, ?> kakaoAccount
) {
}
