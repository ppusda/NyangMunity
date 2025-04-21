package cat.community.nyangmunity.member.response;

import cat.community.nyangmunity.member.entity.Member;
import lombok.Builder;

@Builder
public record MemberInfoResponse(
	Long id,
	String nickname,
	String email
) {

	public static MemberInfoResponse from(Member member) {
		return MemberInfoResponse.builder()
			.id(member.getId())
			.nickname(member.getNickname())
			.email(member.getEmail())
			.build();
	}
}
