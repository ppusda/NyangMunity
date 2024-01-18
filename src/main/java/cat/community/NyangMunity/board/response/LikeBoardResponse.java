package cat.community.NyangMunity.board.response;

import cat.community.NyangMunity.board.entity.Board;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class LikeBoardResponse {

    private final Long bid;
    private final List<BoardImageResponse> boardImages;
    private final String nickName;

    @Builder
    public LikeBoardResponse(Board board) {
        this.bid = board.getId();
        this.boardImages = board.getBoardImages().stream()
                .map(BoardImageResponse::new)
                .collect(Collectors.toList());
        this.nickName = board.getUser().getNickname();
    }

}
