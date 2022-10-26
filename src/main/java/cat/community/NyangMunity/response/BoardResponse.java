package cat.community.NyangMunity.response;

import cat.community.NyangMunity.domain.Board;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BoardResponse {

    private final Long id;
    private final String title;
    private final String content;

    public BoardResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
    }

    @Builder
    public BoardResponse(Long id, String title, String content) {
        this.id = id;
        this.title = title.substring(0, Math.min(title.length(), 10));
        this.content = content;
    }
}
// 서비스 정책에 맞는 클래스
