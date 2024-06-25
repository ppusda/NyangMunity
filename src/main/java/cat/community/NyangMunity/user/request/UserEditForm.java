package cat.community.NyangMunity.user.request;

import lombok.Builder;


@Builder
public record UserEditForm (
        String password,
        String nickname,
        String birthday
) {

}
