package cat.community.NyangMunity.board.response;

import lombok.Builder;

@Builder
public record BoardDetailResponse(
        BoardResponse boardResponse,
        Boolean isWriter
) {

}
