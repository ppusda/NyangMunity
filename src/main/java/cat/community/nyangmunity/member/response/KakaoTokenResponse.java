package cat.community.nyangmunity.member.response;

import lombok.Builder;
import lombok.Getter;

@Builder
public record KakaoTokenResponse(
	String access_token,
	String refresh_token
) {

}
