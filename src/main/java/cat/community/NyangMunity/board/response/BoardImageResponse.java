package cat.community.NyangMunity.board.response;


import cat.community.NyangMunity.board.entity.Board;
import cat.community.NyangMunity.image.entity.Image;
import java.util.List;
import lombok.Builder;

@Builder
public record BoardImageResponse(
        Long id,
        String name,
        String url
) {

    public static BoardImageResponse from(Image image) {
        return BoardImageResponse.builder()
                .id(image.getId())
                .name(image.getName())
                .url(image.getUrl())
                .build();
    }

}
