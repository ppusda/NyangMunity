package cat.community.nyangmunity.member.response;

import lombok.Builder;

@Builder
public record KakaoTokenResponse(
	String access_token,
	String refresh_token
) {

}
