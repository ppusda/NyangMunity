package cat.community.NyangMunity.board.request;

import cat.community.NyangMunity.global.exception.InvalidRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record BoardFormRequest(
        @Size(message = "내용은 최대 100자 까지 입력이 가능합니다.")
        @NotBlank(message = "간단한 설명을 입력해주세요.")
        String content,

        List<String> boardImages // url 형식
) {

    @Builder
    public BoardFormRequest(String content, List<String> boardImages) {
        validate();
        this.content = content;
        this.boardImages = boardImages;
    }

    // TODO: 특정 키워드 Valid 옵션 추가 고려
   public void validate() {
        if (content.contains("바보")) {
            throw new InvalidRequest();
        }
    }
}
