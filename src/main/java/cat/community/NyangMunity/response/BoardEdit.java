package cat.community.NyangMunity.response;

import cat.community.NyangMunity.domain.BoardImage;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class BoardEdit {

    private String title;
    private String content;
    private List<MultipartFile> boardImages;
    private List<Long> removeList;

    @Builder
    public BoardEdit(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
