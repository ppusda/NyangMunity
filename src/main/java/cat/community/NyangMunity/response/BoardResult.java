package cat.community.NyangMunity.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardResult {
    private final List<BoardResponse> boardList;
    private final Long totalCnt;

    @Builder
    public BoardResult(List<BoardResponse> boardList, Long totalCnt) {
        this.boardList = boardList;
        this.totalCnt = totalCnt;
    }
}
