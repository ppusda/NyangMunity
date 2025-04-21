package cat.community.nyangmunity.member.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record JoinRequest(
	@NotBlank(message = "아이디를 입력해주세요.")
	@Email(message = "아이디는 이메일 형식만 가능합니다.")
	String email,
	@NotBlank(message = "패스워드를 입력해주세요.")
	String password,
	String nickname
) {

}
