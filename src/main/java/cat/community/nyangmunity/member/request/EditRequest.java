package cat.community.nyangmunity.member.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EditRequest(
	String nickname,
	String newPassword,

	@NotBlank(message = "변경 시 현재 비밀번호는 필수로 입력해주세요.")
	String currentPassword
) {

}
