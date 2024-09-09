package cat.community.NyangMunity.user.response;

import lombok.Builder;

@Builder
public record UserTokens(
        String accessToken,
        String refreshToken
) {

}
