package cat.community.NyangMunity.board.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
public record BoardListResponse (
        List<BoardResponse> boardList,
        Long totalCnt
) {


}
