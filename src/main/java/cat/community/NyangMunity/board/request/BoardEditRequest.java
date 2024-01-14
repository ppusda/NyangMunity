package cat.community.NyangMunity.board.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
public record BoardEditRequest(
        @Size(message = "제목은 최대 25자 까지 입력이 가능합니다.")
        @NotBlank(message = "제목을 입력해주세요.")
        String title,
        @Size(message = "내용은 최대 100자 까지 입력이 가능합니다.")
        @NotBlank(message = "간단한 설명을 입력해주세요.")
        String content,
        List<MultipartFile> boardImages,
        List<Long> removeList
) {

}
