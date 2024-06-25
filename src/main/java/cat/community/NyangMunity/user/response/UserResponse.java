package cat.community.NyangMunity.user.response;

import cat.community.NyangMunity.user.entity.User;
import lombok.Builder;

@Builder
public record UserResponse(
        String email,
        String nickname
) {

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}
