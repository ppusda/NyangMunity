package cat.community.nyangmunity.member.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record KakaoUserResponse(
	String id,
	@JsonProperty(value = "connected_at")
	String connectedAt,
	@JsonProperty(value = "kakao_account")
	Map<String, String> kakaoAccount
) {
}
