package cat.community.NyangMunity.board.request;

import cat.community.NyangMunity.global.exception.InvalidRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record BoardFormRequest(
        @Size(message = "제목은 최대 25자 까지 입력이 가능합니다.")
        @NotBlank(message = "제목을 입력해주세요.")
        String title,

        @Size(message = "내용은 최대 100자 까지 입력이 가능합니다.")
        @NotBlank(message = "간단한 설명을 입력해주세요.")
        String content,

        List<MultipartFile> boardImages
) {
    private final static String[] validList = new String[] {};

    @Builder
    public BoardFormRequest(String title, String content, List<MultipartFile> boardImages) {
        validate();
        this.title = title;
        this.content = content;
        this.boardImages = boardImages;
    }

    // TODO: 특정 키워드 Valid 옵션 추가 예정
   public void validate() {
        if (title.contains("바보")) {
            throw new InvalidRequest();
        }
    }
}
