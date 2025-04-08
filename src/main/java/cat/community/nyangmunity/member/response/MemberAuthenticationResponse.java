package cat.community.nyangmunity.member.response;

import lombok.Builder;

@Builder
public record MemberAuthenticationResponse(
        MemberInfoResponse memberInfoResponse,
        MemberTokens memberTokens
) {

}
