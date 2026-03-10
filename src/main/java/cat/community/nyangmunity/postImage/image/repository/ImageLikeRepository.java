package cat.community.nyangmunity.postImage.image.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.postImage.image.entity.Image;
import cat.community.nyangmunity.postImage.image.entity.ImageLike;

@Repository
public interface ImageLikeRepository
	extends JpaRepository<ImageLike, Long>, ImageLikeRepositoryCustom {

	Optional<ImageLike> findByImageIdAndMemberId(String imageId, Long memberId);

	/**
	 * 회원과 이미지로 좋아요 존재 여부 확인
	 */
	boolean existsByMemberAndImage(Member member, Image image);

	/**
	 * 회원과 이미지로 좋아요 조회
	 */
	Optional<ImageLike> findByMemberAndImage(Member member, Image image);

	/**
	 * 회원이 특정 이미지들에 대해 좋아요한 목록 조회
	 */
	List<ImageLike> findByMemberAndImageIdIn(Member member, List<String> imageIds);
}
