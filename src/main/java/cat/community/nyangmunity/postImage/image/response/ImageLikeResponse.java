package cat.community.nyangmunity.postImage.image.response;

import lombok.Builder;

@Builder
public record ImageLikeResponse(
	String imageId,
	boolean state // true: 좋아요 활성화, false: 좋아요 비활성화
) {
}
