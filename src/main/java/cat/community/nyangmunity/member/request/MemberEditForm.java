package cat.community.nyangmunity.member.request;

import lombok.Builder;

@Builder
public record MemberEditForm(
	String password,
	String nickname,
	String birthday
) {

}
