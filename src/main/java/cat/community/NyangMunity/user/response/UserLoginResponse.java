package cat.community.NyangMunity.user.response;

import lombok.Builder;

@Builder
public record UserLoginResponse(
        UserInfos userInfos,
        UserTokens userTokens
) {

}
