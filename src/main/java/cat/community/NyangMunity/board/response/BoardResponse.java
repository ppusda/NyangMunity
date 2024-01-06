package cat.community.NyangMunity.board.response;

import cat.community.NyangMunity.board.entity.Board;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BoardResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final List<BoardImageResponse> boardImages;
    private final LocalDateTime createDate;

    private final Long uid;
    private final String writer;

    private boolean writerCheck = false;

    public BoardResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.boardImages = board.getBoardImages().stream()
                .map(BoardImageResponse::new)
                .collect(Collectors.toList());
        this.createDate = board.getCreateDate();
        this.uid = board.getUser().getId();
        this.writer = board.getUser().getNickname();
    } // 무한루프로 인한 BoardDTO 내에서 BoardImageDTO 설정

    @Builder
    public BoardResponse(Long id, String title, String content, List<BoardImageResponse> boardImages, LocalDateTime createDate, Long uid, String writer) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.boardImages = boardImages;
        this.createDate = createDate;
        this.uid = uid;
        this.writer = writer;
    }

    public void setWriterCheck(boolean writerCheck) {
        this.writerCheck = writerCheck;
    }
}
// 서비스 정책에 맞는 클래스
