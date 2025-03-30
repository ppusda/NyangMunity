package cat.community.nyangmunity.member.response;

import lombok.Builder;

@Builder
public record MemberCheckResponse(
	Long memberId,
	String nickname,
	Boolean result
) {

}
