package cat.community.nyangmunity.member.response;

import lombok.Builder;

@Builder
public record MemberLoginResponse(
        MemberInfos memberInfos,
        MemberTokens memberTokens
) {

}
