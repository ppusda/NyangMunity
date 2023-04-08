package cat.community.NyangMunity.controller.form;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.jni.Local;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter @Setter
public class UserForm {
    @NotBlank(message = "ID를 입력해주세요.")
    private String email;
    @NotBlank(message = "PW를 입력해주세요.")
    private String password;

    private String nickname;
    private String birthday;

}
