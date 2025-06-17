package cat.community.nyangmunity.postImage.image.response;

import cat.community.nyangmunity.postImage.response.PostImageResponse;
import lombok.Builder;

@Builder
public record MaxLikePostImageResponse(
	String id,
	PostImageResponse imageInfo,
	String nickname,
	String message
) {

}
