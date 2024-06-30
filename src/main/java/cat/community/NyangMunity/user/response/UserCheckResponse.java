package cat.community.NyangMunity.user.response;

import lombok.Builder;

@Builder
public record UserCheckResponse(
        String nickname,
        Boolean result
) {

}
