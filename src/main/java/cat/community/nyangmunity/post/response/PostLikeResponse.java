package cat.community.nyangmunity.post.response;

import lombok.Builder;
import java.util.List;

@Builder
public record PostLikeResponse(
    Long bid,
    List<PostImageResponse> boardImages,
    String nickName
) {

}
