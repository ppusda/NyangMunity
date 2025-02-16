package cat.community.nyangmunity.member.response;

import lombok.Builder;

@Builder
public record MemberCheckResponse(
        String nickname,
        Boolean result
) {

}
