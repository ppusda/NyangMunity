package cat.community.NyangMunity.user.response;

import lombok.Builder;

@Builder
public record UserInfos(
        Long id,
        String nickname
) {

    public static UserInfos from(Long id, String nickname) {
        return UserInfos.builder()
                .id(id)
                .nickname(nickname)
                .build();
    }
}
