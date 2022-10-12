package cat.community.NyangMunity.controller.form;

import cat.community.NyangMunity.domain.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardForm {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "content를 입력해주세요.")
    private String content;

    @Builder
    public BoardForm(String title, String content) {
        this.title = title;
        this.content = content;
    }
}

// Builder는 생성자라고 봐도 무방할 것 같다.
// - 가독성에 좋다. (값 생성에 대한 유연함)
// - 필요한 값만 받을 수 있다. => 오버로딩 가능한 조건 찾아보자.
// - 객체의 불변성 (final에도 사용할 수 있음)