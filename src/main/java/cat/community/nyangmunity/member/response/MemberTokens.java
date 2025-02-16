package cat.community.nyangmunity.member.response;

import lombok.Builder;

@Builder
public record MemberTokens(
        String accessToken,
        String refreshToken
) {

}
