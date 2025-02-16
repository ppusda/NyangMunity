package cat.community.nyangmunity.image.batch.response;

import lombok.Builder;

@Builder
public record ImageResponse(
        String id,
        String url
) {

}
