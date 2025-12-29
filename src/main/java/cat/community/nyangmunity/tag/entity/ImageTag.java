package cat.community.nyangmunity.tag.entity;

import cat.community.nyangmunity.postImage.image.entity.Image;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이미지-태그 매핑 엔티티
 *
 * <p>Image와 Tag의 다대다 관계를 표현하는 중간 테이블입니다.
 */
@Entity
@Table(
    name = "image_tag",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"image_id", "tag_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageTag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "image_id", nullable = false)
  private Image image;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_id", nullable = false)
  private Tag tag;

  @Builder
  public ImageTag(Image image, Tag tag) {
    this.image = image;
    this.tag = tag;
  }
}
