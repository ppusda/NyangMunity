package cat.community.nyangmunity.postImage.image.batch.response;

import cat.community.nyangmunity.postImage.image.entity.Image;

public record ImageResponse(
	String id,
	String url
) {

	public static ImageResponse from(Image image) {
		return new ImageResponse(image.getId(), image.getUrl());
	}

}
