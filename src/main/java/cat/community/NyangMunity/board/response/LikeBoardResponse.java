package cat.community.NyangMunity.board.response;

import lombok.Builder;
import java.util.List;

@Builder
public record LikeBoardResponse (
    Long bid,
    List<BoardImageResponse> boardImages,
    String nickName
) {

}
