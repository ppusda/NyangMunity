package cat.community.nyangmunity.tag.response;

import cat.community.nyangmunity.tag.entity.Tag;
import lombok.Builder;
import lombok.Getter;

/** 태그 정보를 응답하는 DTO */
@Getter
@Builder
public class TagResponse {

  private Long id;
  private String name;
  private Integer usageCount;

  /**
   * Tag 엔티티로부터 TagResponse를 생성합니다.
   *
   * @param tag Tag 엔티티
   * @return TagResponse
   */
  public static TagResponse from(Tag tag) {
    return TagResponse.builder()
        .id(tag.getId())
        .name(tag.getName())
        .usageCount(tag.getUsageCount())
        .build();
  }
}
