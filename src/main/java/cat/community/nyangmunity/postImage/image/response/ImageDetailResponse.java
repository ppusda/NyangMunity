package cat.community.nyangmunity.postImage.image.response;

import cat.community.nyangmunity.postImage.image.entity.Image;
import cat.community.nyangmunity.tag.response.TagResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

/**
 * 이미지 상세 응답 DTO (태그 포함)
 */
@Getter
@Builder
public class ImageDetailResponse {

  private String id;
  private String name;
  private String url;
  private String description;
  private String provider;
  private LocalDateTime uploadDate;
  private Integer likesCount;
  private Integer viewsCount;
  private String uploader;
  private List<TagResponse> tags;
  private Boolean likeState;

  /**
   * Image 엔티티로부터 ImageDetailResponse를 생성합니다.
   */
  public static ImageDetailResponse from(Image image) {
    return from(image, false);
  }

  /**
   * Image 엔티티로부터 ImageDetailResponse를 생성합니다 (좋아요 상태 포함).
   */
  public static ImageDetailResponse from(Image image, boolean likeState) {
    List<TagResponse> tags =
        image.getImageTags().stream()
            .map(imageTag -> TagResponse.from(imageTag.getTag()))
            .collect(Collectors.toList());

    return ImageDetailResponse.builder()
        .id(image.getId())
        .name(image.getName())
        .url(image.getUrl())
        .description(image.getDescription())
        .provider(image.getProvider().name())
        .uploadDate(image.getUploadDate())
        .likesCount(image.getLikesCount())
        .viewsCount(image.getViewsCount())
        .uploader(image.getMember() != null ? image.getMember().getNickname() : "Anonymous")
        .tags(tags)
        .likeState(likeState)
        .build();
  }
}
