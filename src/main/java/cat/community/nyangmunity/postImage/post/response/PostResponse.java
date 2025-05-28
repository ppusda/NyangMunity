package cat.community.nyangmunity.postImage.post.response;

import java.time.LocalDateTime;
import java.util.List;

import cat.community.nyangmunity.postImage.post.entity.Post;
import cat.community.nyangmunity.postImage.response.PostImageResponse;
import lombok.Builder;

@Builder
public record PostResponse(
	Long id,
	String content,
	List<PostImageResponse> postImages,
	LocalDateTime createDate,
	Long memberId,
	String writer
) {

	public static PostResponse from(Post post, List<PostImageResponse> postImages) {
		return PostResponse.builder()
			.id(post.getId())
			.content(post.getContent())
			.postImages(postImages)
			.createDate(post.getCreateDate())
			.memberId(post.getMember().getId())
			.writer(post.getMember().getNickname())
			.build();
	}
}
