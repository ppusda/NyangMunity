package cat.community.NyangMunity.board.response;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
