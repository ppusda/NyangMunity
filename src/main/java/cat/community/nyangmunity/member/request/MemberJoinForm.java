package cat.community.nyangmunity.member.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record MemberJoinForm(
        @NotBlank(message = "ID를 입력해주세요.")
        String email,
        @NotBlank(message = "PW를 입력해주세요.")
        String password,
        String nickname,
        String birthday
){

}
