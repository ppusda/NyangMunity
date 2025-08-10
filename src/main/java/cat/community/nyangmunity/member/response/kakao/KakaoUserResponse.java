package cat.community.nyangmunity.member.response.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserResponse(
	String id,
	@JsonProperty(value = "connected_at")
	String connectedAt,
	@JsonProperty(value = "kakao_account")
	KakaoAccount kakaoAccount
) {
}
