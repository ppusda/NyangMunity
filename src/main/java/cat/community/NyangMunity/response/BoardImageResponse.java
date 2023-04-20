package cat.community.NyangMunity.response;

import cat.community.NyangMunity.domain.BoardImage;
import lombok.Getter;

@Getter
public class BoardImageResponse {
    private Long id;
    private String name;
    private Long size;
    private String path;

    public BoardImageResponse(BoardImage boardImage) {
        this.id = boardImage.getId();
        this.name = boardImage.getName();
        this.size = boardImage.getSize();
        this.path = boardImage.getPath();
    }
}
// 무한루프로 인한 BoardImageDTO 작성
