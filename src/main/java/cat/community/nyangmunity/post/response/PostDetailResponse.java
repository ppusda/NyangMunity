package cat.community.nyangmunity.post.response;

import lombok.Builder;

@Builder
public record PostDetailResponse(
	PostResponse postResponse,
	Boolean isWriter
) {

}
