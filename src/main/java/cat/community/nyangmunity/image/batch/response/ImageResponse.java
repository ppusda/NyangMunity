package cat.community.nyangmunity.image.batch.response;

import cat.community.nyangmunity.image.entity.Image;

public record ImageResponse(
        String id,
        String url
) {

	public static ImageResponse from(Image image) {
		return new ImageResponse(image.getId(), image.getUrl());
	}

}
