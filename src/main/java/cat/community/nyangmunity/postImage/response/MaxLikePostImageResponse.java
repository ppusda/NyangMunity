package cat.community.nyangmunity.postImage.response;

import lombok.Builder;

@Builder
public record MaxLikePostImageResponse(
	Long id,
	PostImageResponse postImage,
	String nickname,
	String message
) {

}
