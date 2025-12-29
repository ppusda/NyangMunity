package cat.community.nyangmunity.tag.repository;

import cat.community.nyangmunity.tag.entity.Tag;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Tag 엔티티를 위한 Repository 인터페이스 */
public interface TagRepository extends JpaRepository<Tag, Long> {

	/**
	 * 태그 이름으로 태그를 조회합니다.
	 *
	 * @param name 조회할 태그 이름
	 * @return 해당 이름의 태그 (Optional)
	 */
	Optional<Tag> findByName(String name);

	/**
	 * 사용 횟수 기준 상위 N개의 태그를 조회합니다.
	 *
	 * @param limit 조회할 최대 개수
	 * @return 상위 태그 목록
	 */
	@Query("SELECT t FROM Tag t ORDER BY t.usageCount DESC")
	List<Tag> findTopNByOrderByUsageCountDesc(@Param("limit") int limit);

	/**
	 * 태그 이름에 특정 문자열이 포함된 태그를 조회합니다. (자동완성용)
	 *
	 * @param keyword 검색 키워드
	 * @param limit 조회할 최대 개수
	 * @return 매칭되는 태그 목록
	 */
	@Query("SELECT t FROM Tag t WHERE t.name LIKE %:keyword% ORDER BY t.usageCount DESC")
	List<Tag> findByNameContainingOrderByUsageCountDesc(@Param("keyword") String keyword, Pageable pageable);
}
