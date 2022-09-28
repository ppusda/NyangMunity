package cat.community.NyangMunity.controller.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class JoinForm {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String passwordCheck;
    @NotBlank
    private String nickname;

    private String birthday;
}
