package cat.community.nyangmunity.member.response;

import java.util.Map;

import lombok.Getter;

public record KakaoUserAccount(
	Map<String, String> profile,
	String email
) {

}
