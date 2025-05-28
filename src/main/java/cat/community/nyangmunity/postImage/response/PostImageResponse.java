package cat.community.nyangmunity.postImage.response;

import cat.community.nyangmunity.postImage.entity.PostImage;
import lombok.Builder;

@Builder
public record PostImageResponse(
	String id,
	String url
) {

	public static PostImageResponse from(PostImage postImage) {
		return PostImageResponse.builder()
			.id(postImage.getImage().getId())
			.url(postImage.getImage().getUrl())
			.build();
	}

}
