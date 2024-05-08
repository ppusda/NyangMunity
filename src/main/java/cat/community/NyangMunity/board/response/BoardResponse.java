package cat.community.NyangMunity.board.response;

import cat.community.NyangMunity.board.entity.Board;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record BoardResponse(
    Long id,
    String title,
    String content,
    List<BoardImageResponse> boardImages,
    LocalDateTime createDate,
    Long uid,
    String writer
) {

    public static BoardResponse from(Board board, List<BoardImageResponse> boardImages) {
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .boardImages(boardImages)
                .createDate(board.getCreateDate())
                .uid(board.getUser().getId())
                .writer(board.getUser().getNickname())
                .build();
    }
}
