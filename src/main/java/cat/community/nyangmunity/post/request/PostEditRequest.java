package cat.community.nyangmunity.post.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record PostEditRequest(
	@Size(message = "내용은 최대 100자 까지 입력이 가능합니다.")
	@NotBlank(message = "간단한 설명을 입력해주세요.")
	String content,
	List<String> imageUrls,
	List<Long> removeList
) {

}
