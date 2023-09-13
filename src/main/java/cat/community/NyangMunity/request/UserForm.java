package cat.community.NyangMunity.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter @Setter @ToString
public class UserForm {
    @NotBlank(message = "ID를 입력해주세요.")
    private String email;
    @NotBlank(message = "PW를 입력해주세요.")
    private String password;

    private String nickname;
    private String birthday;

}
