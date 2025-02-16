package cat.community.nyangmunity.member.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter @Setter
@NoArgsConstructor
public class MemberForm {
    @NotBlank(message = "ID를 입력해주세요.")
    private String email;
    @NotBlank(message = "PW를 입력해주세요.")
    private String password;

    private String nickname;
    private String birthday;

    @Builder
    public MemberForm(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
