package cat.community.NyangMunity.board.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Getter
@Setter
@Builder
public class BoardSearch {

    private static final Integer MAX_SIZE = 2000;

    @Builder.Default
    private Integer page = 1;

    @Builder.Default
    private Integer size = 10;

    public BoardSearch(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public long getOffset() {
        return (long) (max(1, page) - 1) * min(MAX_SIZE, size);
    }
}
