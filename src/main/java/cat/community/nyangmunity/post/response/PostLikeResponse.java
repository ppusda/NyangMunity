package cat.community.nyangmunity.post.response;

import lombok.Builder;
import java.util.List;

@Builder
public record PostLikeResponse(
    Long id,
    List<PostImageResponse> postImages,
    String nickname,
	String message
) {

}
