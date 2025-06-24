package cat.community.nyangmunity.postImage.response;

import cat.community.nyangmunity.postImage.entity.PostImage;
import cat.community.nyangmunity.postImage.image.entity.Image;
import lombok.Builder;

@Builder
public record PostImageResponse(

	String id,
	String url,
	boolean likeState
) {

	public static PostImageResponse from(PostImage postImage, boolean likeSate) {
		return PostImageResponse.builder()
			.id(postImage.getImage().getId())
			.url(postImage.getImage().getUrl())
			.likeState(likeSate)
			.build();
	}

	public static PostImageResponse from(Image image) {
		return PostImageResponse.builder()
			.id(image.getId())
			.url(image.getUrl())
			.build();
	}

}
