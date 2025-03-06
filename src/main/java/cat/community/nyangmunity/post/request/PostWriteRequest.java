package cat.community.nyangmunity.post.request;

import cat.community.nyangmunity.global.exception.InvalidRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

public record PostWriteRequest(
        @Size(message = "내용은 최대 100자 까지 입력이 가능합니다.")
        @NotBlank(message = "간단한 설명을 입력해주세요.")
        String content,
        List<String> postImageIds
) {

    @Builder
    public PostWriteRequest(String content, List<String> postImageIds) {
        this.content = content;
        this.postImageIds = postImageIds;
    }

    // TODO: 특정 키워드 Valid 옵션 추가 고려
   public void validate(String content) {
        if (content.contains("바보")) {
            throw new InvalidRequest();
        }
    }
}
