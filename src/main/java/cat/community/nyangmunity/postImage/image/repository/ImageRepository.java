package cat.community.nyangmunity.postImage.image.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cat.community.nyangmunity.postImage.image.entity.Image;
import cat.community.nyangmunity.postImage.image.entity.Provider;
import io.lettuce.core.dynamic.annotation.Param;

public interface ImageRepository extends JpaRepository<Image, String> {

	Page<Image> findAllByProvider(Pageable pageable, Provider provider);

	List<Image> findAllByIdIn(List<String> ids);

	/**
	 * Provider로 필터링
	 */
	Page<Image> findByProvider(Provider provider, Pageable pageable);

	/**
	 * 모든 태그를 포함하는 이미지 조회 (AND 조건)
	 */
	@Query(
		"SELECT DISTINCT i FROM Image i "
			+ "JOIN i.imageTags it "
			+ "WHERE it.tag.name IN :tagNames "
			+ "GROUP BY i.id "
			+ "HAVING COUNT(DISTINCT it.tag.id) = :tagCount")
	Page<Image> findByTags(
		@Param("tagNames") List<String> tagNames,
		@Param("tagCount") long tagCount,
		Pageable pageable);

	/**
	 * Provider와 모든 태그를 포함하는 이미지 조회
	 */
	@Query(
		"SELECT DISTINCT i FROM Image i "
			+ "JOIN i.imageTags it "
			+ "WHERE i.provider = :provider "
			+ "AND it.tag.name IN :tagNames "
			+ "GROUP BY i.id "
			+ "HAVING COUNT(DISTINCT it.tag.id) = :tagCount")
	Page<Image> findByProviderAndTags(
		@Param("provider") Provider provider,
		@Param("tagNames") List<String> tagNames,
		@Param("tagCount") long tagCount,
		Pageable pageable);
}
