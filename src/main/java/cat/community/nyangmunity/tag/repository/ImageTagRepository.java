package cat.community.nyangmunity.tag.repository;

import cat.community.nyangmunity.tag.entity.ImageTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** ImageTag 엔티티를 위한 Repository 인터페이스 */
public interface ImageTagRepository extends JpaRepository<ImageTag, Long> {

  /**
   * 특정 이미지의 모든 태그를 조회합니다.
   *
   * @param imageId 이미지 ID
   * @return 해당 이미지의 태그 목록
   */
  @Query("SELECT it FROM ImageTag it JOIN FETCH it.tag WHERE it.image.id = :imageId")
  List<ImageTag> findByImageId(@Param("imageId") String imageId);

  /**
   * 특정 태그가 부여된 모든 이미지를 조회합니다.
   *
   * @param tagId 태그 ID
   * @return 해당 태그가 부여된 이미지 목록
   */
  @Query("SELECT it FROM ImageTag it JOIN FETCH it.image WHERE it.tag.id = :tagId")
  List<ImageTag> findByTagId(@Param("tagId") Long tagId);

  /**
   * 여러 태그를 모두 포함하는 이미지를 조회합니다.
   *
   * @param tagIds 태그 ID 목록
   * @param tagCount 태그 개수
   * @return 모든 태그를 포함하는 이미지 목록
   */
  @Query(
      "SELECT it.image.id FROM ImageTag it "
          + "WHERE it.tag.id IN :tagIds "
          + "GROUP BY it.image.id "
          + "HAVING COUNT(DISTINCT it.tag.id) = :tagCount")
  List<String> findImageIdsByAllTags(@Param("tagIds") List<Long> tagIds, @Param("tagCount") long tagCount);
}
