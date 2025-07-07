package cat.community.nyangmunity.member.response;

import java.util.Map;

public record KakaoUserAccount(
	Map<String, String> profile,
	String email
) {

}
