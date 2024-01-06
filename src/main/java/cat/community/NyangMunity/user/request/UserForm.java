package cat.community.NyangMunity.user.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
public class UserForm {
    @NotBlank(message = "ID를 입력해주세요.")
    private String email;
    @NotBlank(message = "PW를 입력해주세요.")
    private String password;

    private String nickname;
    private String birthday;

    @Builder
    public UserForm(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
