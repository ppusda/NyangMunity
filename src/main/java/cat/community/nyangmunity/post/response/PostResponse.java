package cat.community.nyangmunity.post.response;

import cat.community.nyangmunity.post.entity.Post;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostResponse(
    Long id,
    String content,
    List<PostImageResponse> postImages,
    LocalDateTime createDate,
    Long uid,
    String writer
) {

    public static PostResponse from(Post post, List<PostImageResponse> postImages) {
        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .postImages(postImages)
                .createDate(post.getCreateDate())
                .uid(post.getMember().getId())
                .writer(post.getMember().getNickname())
                .build();
    }
}
