package cat.community.nyangmunity.postImage.response;

import lombok.Builder;

@Builder
public record MaxLikePostImageResponse(
	String id,
	PostImageResponse imageInfo,
	String nickname,
	String message
) {

}
