package cat.community.NyangMunity.request;

import cat.community.NyangMunity.exception.InvalidRequest;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardForm {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "content를 입력해주세요.")
    private String content;

    private List<MultipartFile> boardImages;

    @Builder
    public BoardForm(String title, String content, List<MultipartFile> boardImages) {
        this.title = title;
        this.content = content;
        this.boardImages = boardImages;
    }

    // todo 테스트 코드 항목 중 "게시글 작성 시 제목에 '바보'는 포함될 수 없다." 를 테스트 해보려 함.
   public void validate() {
        if (title.contains("바보")) {
            throw new InvalidRequest();
        }
    }
}

// Builder는 생성자라고 봐도 무방할 것 같다.
// - 가독성에 좋다. (값 생성에 대한 유연함)
// - 필요한 값만 받을 수 있다. => 오버로딩 가능한 조건 찾아보자.
// - 객체의 불변성 (final에도 사용할 수 있음)