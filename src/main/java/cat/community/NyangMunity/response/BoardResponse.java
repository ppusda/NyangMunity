package cat.community.NyangMunity.response;

import cat.community.NyangMunity.domain.Board;
import cat.community.NyangMunity.domain.BoardImage;
import lombok.Builder;
import lombok.Getter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BoardResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final List<BoardImageResponse> boardImages;
    private final LocalDateTime createDate;

    public BoardResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.boardImages = board.getBoardImages().stream()
                .map(BoardImageResponse::new)
                .collect(Collectors.toList());
        this.createDate = board.getCreateDate();
    } // 무한루프로 인한 BoardDTO 내에서 BoardImageDTO 설정

    @Builder
    public BoardResponse(Long id, String title, String content, List<BoardImageResponse> boardImages, LocalDateTime createDate) {
        this.id = id;
        this.title = title.substring(0, Math.min(title.length(), 10));
        this.content = content;
        this.boardImages = boardImages;
        this.createDate = createDate;
    }
}
// 서비스 정책에 맞는 클래스
