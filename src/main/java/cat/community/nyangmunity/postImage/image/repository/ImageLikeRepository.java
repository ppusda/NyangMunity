package cat.community.nyangmunity.postImage.image.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cat.community.nyangmunity.postImage.image.entity.ImageLike;

@Repository
public interface ImageLikeRepository
	extends JpaRepository<ImageLike, Long>, ImageLikeRepositoryCustom {

	Optional<ImageLike> findByImageIdAndMemberId(String imageId, Long memberId);
}
