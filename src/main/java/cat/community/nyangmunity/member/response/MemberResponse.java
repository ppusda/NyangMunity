package cat.community.nyangmunity.member.response;

import cat.community.nyangmunity.member.entity.Member;
import lombok.Builder;

@Builder
public record MemberResponse(
        String email,
        String nickname
) {

    public static MemberResponse toUserResponse(Member member) {
        return MemberResponse.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }
}
