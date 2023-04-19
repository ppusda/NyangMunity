package cat.community.NyangMunity.response;

import cat.community.NyangMunity.domain.Board;
import cat.community.NyangMunity.domain.BoardImage;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BoardResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final List<BoardImage> boardImages;

    public BoardResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.boardImages = board.getBoardImages();
    }

    @Builder
    public BoardResponse(Long id, String title, String content, List<BoardImage> boardImages) {
        this.id = id;
        this.title = title.substring(0, Math.min(title.length(), 10));
        this.content = content;
        this.boardImages = boardImages;
    }
}
// 서비스 정책에 맞는 클래스
