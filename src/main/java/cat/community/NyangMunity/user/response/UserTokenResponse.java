package cat.community.NyangMunity.user.response;

import lombok.Builder;

@Builder
public record UserTokenResponse(
        String accessToken,
        String refreshToken
) {

}
