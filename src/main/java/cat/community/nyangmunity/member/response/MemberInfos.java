package cat.community.nyangmunity.member.response;

import lombok.Builder;

@Builder
public record MemberInfos(
        Long id,
        String nickname
) {

    public static MemberInfos from(Long id, String nickname) {
        return MemberInfos.builder()
                .id(id)
                .nickname(nickname)
                .build();
    }
}
