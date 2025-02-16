package cat.community.nyangmunity.post.response;

import cat.community.nyangmunity.post.entity.Post;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostResponse(
    Long id,
    String content,
    List<PostImageResponse> boardImages,
    LocalDateTime createDate,
    Long uid,
    String writer
) {

    public static PostResponse from(Post post, List<PostImageResponse> boardImages) {
        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .boardImages(boardImages)
                .createDate(post.getCreateDate())
                .uid(post.getMember().getId())
                .writer(post.getMember().getNickname())
                .build();
    }
}
