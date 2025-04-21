package cat.community.nyangmunity.post.response;

import java.util.List;

import lombok.Builder;

@Builder
public record PostLikeResponse(
	Long id,
	List<PostImageResponse> postImages,
	String nickname,
	String message
) {

}
